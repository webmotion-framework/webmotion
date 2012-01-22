/*
 * #%L
 * Webmotion in action
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Debux
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.debux.webmotion.server.handler;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.FragmentUrl;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search in mapping the action matched at the url. In mapping file, the first 
 * line is most priority.
 * 
 * @author julien
 */
public class ActionFinderHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ActionFinderHandler.class);

    @Override
    public void init(Mapping mapping, ServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        ActionRule actionRule = getActionRule(mapping, call);
        if (actionRule != null) {
            call.setRule(actionRule);
        } else {
            Extension extension = mapping.getExtension();
            if (extension == null) {
                throw new WebMotionException("Not mapping found for url " 
                        + call.getContext().getUrl());
            }
        }
    }
    
    protected ActionRule getActionRule(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        
        String url = context.getUrl();
        if (url != null) {
            
            log.info("url = " + url);
            List<String> path = WebMotionUtils.splitPath(url);
            log.info("path = " + path);
            Map<String, Object> parameters = call.getExtractParameters();
            String method = context.getMethod();

            List<ActionRule> actionRules = mapping.getActionRules();
            for (ActionRule actionRule : actionRules) {

                if(checkMethod(actionRule, method) 
                        && checkUrl(actionRule, path, parameters)) {
                    return actionRule;
                }
            }
        }
        
        log.error("Unable to get action rule for url: " + url);
        return null;
    }
    
    // Check http method
    protected boolean checkMethod(ActionRule actionRule, String method) {
        List<String> methods = actionRule.getMethods();
        return methods.contains("*") || methods.contains(method);
    }
    
    // Check url
    protected boolean checkUrl(ActionRule actionRule, List<String> path, Map<String, Object> parameters) {
        int position;
        
        // Test url
        List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
        FragmentUrl[] expressions = ruleUrl.toArray(new FragmentUrl[0]);
        
        // All path math in rule
        if (expressions.length != path.size()) {
            return false;
        }
        
        for (position = 0; position < expressions.length; position ++) {
            FragmentUrl expression = expressions[position];
            Pattern pattern = expression.getPattern();
            String name = expression.getName();
            
            String value = path.get(position);
            if(!value.isEmpty() && pattern == null && name == null) {
                return false;
            }
                
            String[] values = new String[]{value};
            boolean matchValues = matchValues(expression, values);
            log.info("Path " + Arrays.toString(values) + " for pattern " + pattern + " match ? " + matchValues);
            if(!matchValues) {
                return false;
            }
        }

        // Test parameters
        List<FragmentUrl> ruleParameters = actionRule.getRuleParameters();
        expressions = ruleParameters.toArray(new FragmentUrl[0]);

        for (position = 0; position < expressions.length; position ++) {
            FragmentUrl expression = expressions[position];

            log.info("param " + expression.getParam());
            String param = expression.getParam();
            String[] values = null;
            Object parameterValue = parameters.get(param);
            if(!(parameterValue instanceof File)) {
                values = (String[]) parameterValue;
            }
            
            boolean matchValues = matchValues(expression, values);
            log.info("Param " + param + " for value " + Arrays.toString(values) + " match ? " + matchValues);
            if(!matchValues) {
                return false;
            }
        }

        return true;
    }
    
    protected boolean matchValues(FragmentUrl expression, String[] values) {
        if (values == null) {
            return false;
        }
        
        boolean found = true;

        Pattern pattern = expression.getPattern();
        if (pattern != null) {
            for (String value : values) {
                Matcher matcher = pattern.matcher(value);
                found &= matcher.find();
            }
        }

        return found;
    }
}

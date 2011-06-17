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
import org.debux.webmotion.server.mapping.URLPattern;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
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
    public void handle(Mapping mapping, Call call) {
        ActionRule actionRule = getActionRule(mapping, call);
        if(actionRule != null) {
            call.setActionRule(actionRule);
        } else {
            throw new WebMotionException("Not mapping found for url " 
                    + call.getContext().getUrl());
        }
    }
    
    public ActionRule getActionRule(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        
        String url = context.getUrl();
        if(url != null) {
            
            log.info("url = " + url);
            String[] path = StringUtils.splitPreserveAllTokens(url, "/");
            log.info("path = " + Arrays.toString(path));
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
        
        return null;
    }
    
    // Check http method
    public boolean checkMethod(ActionRule actionRule, String method) {
        String actionMethod = actionRule.getMethod();
        return actionMethod.equals("*") || actionMethod.equals(method);
    }
    
    // Check url
    public boolean checkUrl(ActionRule actionRule, String[] path, Map<String, Object> parameters) {
        int position;
        
        // Test url
        List<URLPattern> ruleUrl = actionRule.getRuleUrl();
        URLPattern[] expressions = ruleUrl.toArray(new URLPattern[0]);

        // All path math in rule
        if(expressions.length != path.length) {
            return false;
        }
        
        for (position = 0; position < expressions.length; position ++) {
            URLPattern expression = expressions[position];
            Pattern pattern = expression.getPattern();
            String name = expression.getName();
            
            String value = path[position];
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
        List<URLPattern> ruleParameters = actionRule.getRuleParameters();
        expressions = ruleParameters.toArray(new URLPattern[0]);

        for (position = 0; position < expressions.length; position ++) {
            URLPattern expression = expressions[position];

            log.info("param " + expression.getParam());
            String param = expression.getParam();
            String[] values = null;
            Object parameterValue = parameters.get(param);
            if(!(parameterValue instanceof File)) {
                values = (String[]) parameterValue;
            }
            
            boolean matchValues = matchValues(expression, values);
            log.info("Param " + param + " for value " + parameterValue + " match ? " + matchValues);
            if(!matchValues) {
                return false;
            }
        }

        return true;
    }
    
    public boolean matchValues(URLPattern expression, String[] values) {
        if(values == null) {
            return false;
        }
        
        boolean found = true;

        Pattern pattern = expression.getPattern();
        if(pattern != null) {
            for (String value : values) {
                Matcher matcher = pattern.matcher(value);
                found &= matcher.find();
            }
        }

        return found;
    }
}

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
        log.info("url = " + url);
        String[] path = url.split("/");
        Map<String, Object> parameters = call.getExtractParameters();
        String method = context.getMethod();
        
        List<ActionRule> actionRules = mapping.getActionRules();
        for (ActionRule actionRule : actionRules) {
            
            if(checkMethod(actionRule, method) 
                    && checkUrl(actionRule, path, parameters)) {
                return actionRule;
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
        List<URLPattern> rule = actionRule.getRule();
        URLPattern[] expressions = rule.toArray(new URLPattern[0]);

        // Match root i.e. "/"
        if(rule.isEmpty() && path.length == 0) {
            return true;
        }
        
        boolean matchUrl = true;
        
        int pathSize = 0;
        for (int position = 0; position < expressions.length; position ++) {
            URLPattern expression = expressions[position];

            log.info("param " + expression.getParam());
            log.info("name " + expression.getName());
            log.info("pattern " + expression.getPattern());

            String[] values = null;
            String param = expression.getParam();
            if(param == null) {
                if(position < path.length) {
                    values = new String[]{path[position]};
                }
                pathSize = position;

            } else {                    
                Object parameterValue = parameters.get(param);
                if(!(parameterValue instanceof File)) {
                    values = (String[]) parameterValue;
                }
            }
            log.info("value " + Arrays.toString(values));

            if(values != null) {
                Pattern pattern = expression.getPattern();
                if(pattern != null) {
                    
                    boolean found = true;
                    for (String value : values) {
                        Matcher matcher = pattern.matcher(value);
                        found &= matcher.find();
                        log.info("matcher " + found);
                    }
                    
                    if(!found) {
                        matchUrl = false;
                        break;
                    }
                }

            } else {
                matchUrl = false;
                break;
            }
        }

        // All path math in rule without parameters
        if(pathSize < path.length - 1) {
            return false;
        }

        return matchUrl;
    }
    
}

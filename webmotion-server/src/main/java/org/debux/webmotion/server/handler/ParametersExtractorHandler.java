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

import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.FragmentUrl;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract parameter in request, to map name on request and name on define in 
 * mapping.
 * 
 * @author julien
 */
public class ParametersExtractorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ParametersExtractorHandler.class);

    @Override
    public void init(Mapping mapping, ServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        // Save result in call
        Map<String, Object> aliasParameters = call.getAliasParameters();
        
        // Not action found in extension ?
        ActionRule actionRule = (ActionRule) call.getRule();
        if (actionRule == null) {
            return;
        }
        
        // Contains all parameters
        Map<String, Object> tmp = new LinkedHashMap<String, Object>();
        
        // Add default parameters
        List<FilterRule> filterRules = call.getFilterRules();
        for (FilterRule filterRule : filterRules) {
            Map<String, String[]> defaultParameters = filterRule.getDefaultParameters();
            tmp.putAll(defaultParameters);
        }
        
        Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
        tmp.putAll(defaultParameters);
        
        // Add extract parameters
        Map<String, Object> extractParameters = call.getExtractParameters();
        tmp.putAll(extractParameters);
        
        // Retrieve the good name for parameters give in mapping
        HttpContext context = call.getContext();
        String url = context.getUrl();
        List<String> path = WebMotionUtils.splitPath(url);
        
        List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
        int position = 0;
        for (FragmentUrl expression : ruleUrl) {
            String name = expression.getName();
            
            if(!StringUtils.isEmpty(name)) {
                tmp.put(name, path.get(position));
            }
            position ++;
        }
        
        List<FragmentUrl> ruleParameters = actionRule.getRuleParameters();
        for (FragmentUrl expression : ruleParameters) {
            String name = expression.getName();
            String param = expression.getParam();
            
            if(!StringUtils.isEmpty(name)) {
                Object values = extractParameters.get(param);
                if(values != null) {
                    tmp.put(name, values);
                }
            }
        }
        
        // Transform dot by map
        for (Map.Entry<String, Object> entry : tmp.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // Manage object.property=value
            Map<String, Object> map = aliasParameters;
            
            String[] split = key.split("\\.");
            
            for (position = 0; position < split.length; position++) {

                if(position == split.length - 1) {
                    map.put(split[position], value);
                    
                } else {
                    String name = split[position];

                    Map<String, Object> next = (Map<String, Object>) map.get(name);
                    if(next == null) {
                        next = new LinkedHashMap<String, Object>();
                        map.put(name, next);
                    }

                    map = next;
                }
            }
        }
    }
}

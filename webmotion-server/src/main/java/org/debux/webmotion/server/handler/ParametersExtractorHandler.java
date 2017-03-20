/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Call.ParameterTree;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.FragmentUrl;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.tools.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract parameter in request, to map name on request and name on define in 
 * mapping.
 * 
 * @author julien
 */
public class ParametersExtractorHandler extends AbstractHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ParametersExtractorHandler.class);

    protected static Pattern pattern = Pattern.compile("(\\w+)(\\[(\\d+)\\])?");
    
    @Override
    public void handle(Mapping mapping, Call call) {
        
        // Not action found in extension ?
        ActionRule actionRule = (ActionRule) call.getRule();
        if (actionRule == null) {
            return;
        }
        
        // Contains all parameters
        Map<String, Object> rawParameters = call.getRawParameters();
        
        // Add default parameters
        List<FilterRule> filterRules = call.getFilterRules();
        for (FilterRule filterRule : filterRules) {
            Map<String, String[]> defaultParameters = filterRule.getDefaultParameters();
            rawParameters.putAll(defaultParameters);
        }
        
        Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
        rawParameters.putAll(defaultParameters);
        
        // Add extract parameters
        Map<String, Object> extractParameters = call.getExtractParameters();
        rawParameters.putAll(extractParameters);
        
        // Retrieve the good name for parameters give in mapping
        HttpContext context = call.getContext();
        String url = context.getUrl();
        List<String> path = HttpUtils.splitPath(url);
        
        List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
        int position = 0;
        for (FragmentUrl expression : ruleUrl) {
            String name = expression.getName();
            
            if (!StringUtils.isEmpty(name)) {
                String value = path.get(position);
                
                String[] currentValues = (String[]) rawParameters.get(name);
                if (currentValues == null) {
                    rawParameters.put(name, new String[]{value});
                } else {
                    rawParameters.put(name, ArrayUtils.add(currentValues, value));
                }
            }
            position ++;
        }
        
        List<FragmentUrl> ruleParameters = actionRule.getRuleParameters();
        for (FragmentUrl expression : ruleParameters) {
            String name = expression.getName();
            String param = expression.getParam();
            
            if(!StringUtils.isEmpty(name)) {
                String[] values = (String[]) extractParameters.get(param);
                if (values != null) {
                    
                    String[] currentValues = (String[]) rawParameters.get(name);
                    if (currentValues == null) {
                        rawParameters.put(name, values);
                    } else {
                        rawParameters.put(name, ArrayUtils.addAll(currentValues, values));
                    }
                    rawParameters.put(name + "." + param, values);
                }
            }
        }
        
        // Transform
        ParameterTree parameterTree = toTree(rawParameters);
        call.setParameterTree(parameterTree);
    }
    
    protected static ParameterTree toTree(Map<String, Object> parameters) {
        ParameterTree tree = new ParameterTree();
        
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            
            ParameterTree current = tree;

            Matcher matcher = pattern.matcher(paramName);
            while (matcher.find()) {
                String name = matcher.group(1);
                String index = matcher.group(3);
                
                ParameterTree next;
                        
                if (index == null) {
                    Map<String, ParameterTree> object = current.getObject();
                    if (object == null) {
                        object = new HashMap<String, ParameterTree>();
                        current.setObject(object);
                    }
                    
                    next = object.get(name);
                    if (next == null) {
                        next = new ParameterTree();
                        object.put(name, next);
                    }

                } else {
                    Map<String, List<ParameterTree>> array = current.getArray();
                    if (array == null) {
                        array = new HashMap<String, List<ParameterTree>>();
                        current.setArray(array);
                    }
                    
                    List<ParameterTree> list = array.get(name);
                    if (list == null) {
                        list = new ArrayList<ParameterTree>();
                        array.put(name, list);
                    
                    }
                    
                    int position = new Integer(index);
                    
                    if (position >= 0 && position < list.size()) {
                        
                        next = list.get(position);
                        if (next == null) {
                            next = new ParameterTree();
                            list.set(position, next);
                        }

                    } else {
                        int fill = position - list.size();
                        for (int i = 0; i < fill; i++) {
                            list.add(null);
                        }
                        next = new ParameterTree();
                        list.add(next);
                    }
                }
                current = next;
            }

            
            if (paramValue != null) {
                if (paramValue.getClass().isArray()) {
                    Object[] currentValues = (Object[]) current.getValue();
                    
                    if (currentValues == null) {
                        current.setValue(ArrayUtils.clone((Object[]) paramValue));
                    } else {
                        current.setValue(ArrayUtils.addAll(currentValues, (Object[]) paramValue));
                    }
                } else {
                    current.setValue(paramValue);
                }
            }
        }
        
        return tree;
    }
}

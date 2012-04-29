/*
 * #%L
 * Webmotion server
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

import java.util.List;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Action;
import java.lang.reflect.Method;
import java.util.Map;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.mapping.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Find the filter class reprensent by name given in mapping.
 * 
 * You can add global controller in server context.
 * 
 * @author julien
 */
public class FilterMethodFinderHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(FilterMethodFinderHandler.class);

    /** Global action in server context */
    protected Map<String, Class<? extends WebMotionController>> globalControllers;
    
    @Override
    public void init(Mapping mapping, ServerContext context) {
        globalControllers = context.getGlobalControllers();
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        Rule rule = call.getRule();
        if (rule != null) {
            
            List<FilterRule> filterRules = call.getFilterRules();

            for (FilterRule filterRule : filterRules) {
                handle(mapping, call, filterRule);
            }
        }
    }

    /**
     * Process one filter.
     */
    protected void handle(Mapping mapping, Call call, FilterRule filterRule) throws WebMotionException {
        Action action = filterRule.getAction();
        
        Mapping mappingRule = filterRule.getMapping();
        Config configRule = mappingRule.getConfig();
        String packageName = configRule.getPackageFilters();
        String className = action.getClassName();
        
        String fullQualifiedName =  null;
        if(packageName == null || packageName.isEmpty()) {
            fullQualifiedName =  className;
        } else {
            fullQualifiedName = packageName + "." + className;
        }
            
        try {
            Class<? extends WebMotionController> clazz = globalControllers.get(className);
            if (clazz == null) {
                clazz = (Class<WebMotionController>) Class.forName(fullQualifiedName);
            }

            String methodName = action.getMethodName();
            Method method = WebMotionUtils.getMethod(clazz, methodName);
            if (method == null) {
                throw new WebMotionException("Method not found with name " + methodName + " on class " + fullQualifiedName, filterRule);
            }

            Executor executor = new Executor();
            executor.setClazz(clazz);
            executor.setMethod(method);
            executor.setRule(filterRule);
            
            List<Executor> filters = call.getFilters();
            filters.add(executor);

        } catch (ClassNotFoundException clnfe) {
            throw new WebMotionException("Class not found with name " + fullQualifiedName, clnfe, filterRule);
        }
    }
}

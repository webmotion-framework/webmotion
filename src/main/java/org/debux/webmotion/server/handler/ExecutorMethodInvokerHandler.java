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

import java.util.Iterator;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.call.Render;
import java.lang.reflect.Method;
import java.util.Map;
import org.debux.webmotion.server.WebMotionContextable;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invokes methods of filters then action method. If a filter returns the 
 * render, the render action is avoided.
 * 
 * @author julien
 */
public class ExecutorMethodInvokerHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorMethodInvokerHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        Iterator<Executor> filters = call.getApplyFilters();
        
        // Process action and filters
        Render render = call.getRender();
        if(render == null) {
            if(filters.hasNext()) {
                processFilter(mapping, call);
            } else {
                processAction(mapping, call);
            }
        }
        
        // Process just filters because the call contains already the render
        Executor executor = call.getExecutor();
        if(executor == null && render == null) {
            if(filters.hasNext()) {
                processFilter(mapping, call);
            }
        }
    }

    public void processAction(Mapping mapping, Call call) {
        Executor executor = call.getExecutor();
        try {
            WebMotionContextable actionInstance = executor.getInstance();
            Map<String, Object> parameters = executor.getParameters();

            Method actionMethod = executor.getMethod();
            Object[] toArray = parameters.values().toArray();
            Render render = (Render) actionMethod.invoke(actionInstance, toArray);

            call.setRender(render);

        } catch (Exception ex) {
            throw new WebMotionException("Error during invoke method for filter " 
                    + executor.getClazz().getName() 
                    + " on method " + executor.getMethod().getName(), ex);
        }
    }
    
    public void processFilter(Mapping mapping, Call call) {
        Iterator<Executor> filters = call.getApplyFilters();
        Executor executor = filters.next();
            
        try {
            WebMotionFilter filterInstance = (WebMotionFilter) executor.getInstance();
            filterInstance.setHandler(this, mapping, call);
            
            Map<String, Object> parameters = executor.getParameters();
            Object[] toArray = parameters.values().toArray();

            Method filterMethod = executor.getMethod();
            Render render = (Render) filterMethod.invoke(filterInstance, toArray);
            
            if(render != null) {
                call.setRender(render);
                // Force action to render view on filter, i.e. package name
                call.setExecutor(executor);
            }
                        
        } catch (Exception ex) {
            throw new WebMotionException("Error during invoke method for filter " 
                    + executor.getClazz().getName() 
                    + " on method " + executor.getMethod().getName(), ex);
        }
    }
    
}

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
import org.debux.webmotion.server.render.Render;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.debux.webmotion.server.WebMotionContextable;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.InitContext;
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

    protected WebMotionContextable contextable;

    public ExecutorMethodInvokerHandler(WebMotionContextable contextable) {
        this.contextable = contextable;
    }

    public ExecutorMethodInvokerHandler() {
        this(new WebMotionContextable());
    }

    public void setContextable(WebMotionContextable contextable) {
        this.contextable = contextable;
    }
    
    @Override
    public void init(InitContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        Iterator<Executor> filters = call.getApplyFilters();
        
        // Process just filters because the call contains already the render
        Executor executor = call.getExecutor(); // Search if the call contains a action
        if(executor == null) {
            if(filters.hasNext()) {
                processFilter(mapping, call);
            }
            return;
        }
        
        // Process action and filters
        Render render = call.getRender();
        if(render == null) {
            if(filters.hasNext()) {
                processFilter(mapping, call);
            } else {
                processAction(mapping, call);
            }
        }
    }

    public void processAction(Mapping mapping, Call call) {
        Executor executor = call.getExecutor();
        try {
            WebMotionController controllerInstance = executor.getInstance();
            contextable.create(this, mapping, call);
            controllerInstance.setContextable(contextable);
            
            Map<String, Object> parameters = executor.getParameters();
            Object[] toArray = parameters.values().toArray();

            Method actionMethod = executor.getMethod();
            Render render = (Render) actionMethod.invoke(controllerInstance, toArray);

            // Check if is the last executor is finished to remove the thread local
            List<Executor> filters = call.getFilters();
            if(filters.isEmpty()) {
                contextable.remove();
            }
        
            call.setRender(render);

        } catch (Exception ex) {
            contextable.remove();
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
            contextable.create(this, mapping, call);
            filterInstance.setContextable(contextable);
            
            Map<String, Object> parameters = executor.getParameters();
            Object[] toArray = parameters.values().toArray();

            Method filterMethod = executor.getMethod();
            Render render = (Render) filterMethod.invoke(filterInstance, toArray);
            
            // Check if is the last executor is finished to remove the thread local
            Executor firstFilter = call.getFilters().get(0);
            if(executor == firstFilter) {
                contextable.remove();
            }
        
            if(render != null) {
                call.setRender(render);
                // Force action to render view on filter, i.e. package name
                call.setExecutor(executor);
            }
                        
        } catch (Exception ex) {
            contextable.remove();
            throw new WebMotionException("Error during invoke method for filter " 
                    + executor.getClazz().getName() 
                    + " on method " + executor.getMethod().getName(), ex);
        }
    }
    
}

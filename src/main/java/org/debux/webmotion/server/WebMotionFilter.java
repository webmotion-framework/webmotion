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
package org.debux.webmotion.server;

import java.util.Map;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.ExecutorAction;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * This classe is an action exectuted before and/or after the main action. If
 * the filter method returns a render, the execution will be stopped and the render will be
 * displayed. If the method doProcess is call, the current process will continue.
 * 
 * @author jruchaud
 */
public class WebMotionFilter extends WebMotionAction {
    
    private static final ThreadLocal<WebMotionHandler> handler = new ThreadLocal<WebMotionHandler>();
    private static final ThreadLocal<Mapping> mapping = new ThreadLocal<Mapping>();
    private static final ThreadLocal<Call> call = new ThreadLocal<Call>();
    
    /**
     * Default constructor
     */
    public WebMotionFilter() {
    }

    /**
     * Set the current handler and parameters in thread local, to do process.
     * @param handler current handler
     * @param mapping current mapping
     * @param call current call
     */
    public void setHandler(WebMotionHandler handler, Mapping mapping, Call call) {
        WebMotionFilter.handler.set(handler);
        WebMotionFilter.mapping.set(mapping);
        WebMotionFilter.call.set(call);
    }
    
    /**
     * Call this method to continue the current execution.
     */
    public void doProcess() {
        WebMotionHandler handler = WebMotionFilter.handler.get();
        Call call = WebMotionFilter.call.get();
        Mapping mapping = WebMotionFilter.mapping.get();
        handler.handle(mapping, call);
    }
    
    /**
     * Shortcut to get parameters for action returned by {@see #getAction()} method.
     * @return current parameters for current action
     */
    public Map<String, Object> getParameters() {
        Call call = WebMotionFilter.call.get();
        Executor executor = call.getExecutor();
        if(executor instanceof ExecutorAction) {
            ExecutorAction action = (ExecutorAction) executor;
            Map<String, Object> parameters = action.getParameters();
            return parameters;
        } else {
            return null;
        }
    }

    /**
     * Get the current action, for example you can change parameter to call 
     * action.
     * 
     * @return current action
     */
    public ExecutorAction getAction() {
        Call call = WebMotionFilter.call.get();
        Executor executor = call.getExecutor();
        if(executor instanceof ExecutorAction) {
            return (ExecutorAction) executor;
        } else {
            return null;
        }
    }
    
}

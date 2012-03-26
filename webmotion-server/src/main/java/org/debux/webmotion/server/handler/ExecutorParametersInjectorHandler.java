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

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.CookieManger;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The handler injects in executor parameters from the follow type :
 * <ul>
 * <li>Mapping</li>
 * <li>Config</li>
 * <li>Call</li>
 * <li>HttpContext</li>
 * <li>HttpSession</li>
 * <li>HttpServletRequest or ServletRequest</li>
 * <li>HttpServletResponse or ServletResponse</li>
 * <li>ServerContext</li>
 * <li>ServletContext</li>
 * <li>ErrorData</li>
 * <li>Exception</li>
 * <li>FileProgressListener</li>
 * <li>CookieManager</li>
 * </ul>
 * @author jruchaud
 */
public class ExecutorParametersInjectorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersInjectorHandler.class);

    @Override
    public void init(Mapping mapping, ServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        List<Executor> executors = call.getExecutors();
        for (Executor executor : executors) {
            
            Method executorMethod = executor.getMethod();
            Class<?>[] parameterTypes = executorMethod.getParameterTypes();
            Map<String, Object> parameters = executor.getParameters();
            
            // When the call is an error, create a basic map.
            HttpContext context = call.getContext();
            if(context.isError()) {
                String[] parameterNames = WebMotionUtils.getParameterNames(mapping, executorMethod);
                parameters = new LinkedHashMap<String, Object>(parameterNames.length);
                for (String name : parameterNames) {
                    parameters.put(name, null);
                }
                executor.setParameters(parameters);
            }
            
            // Search a value with a type
            int index = 0;
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                
                if (value == null) {
                    Class<?> type = parameterTypes[index];
                    
                    Object inject = inject(mapping, call, type);
                    log.info("Inject " + name + " for type " + type + " the value " + inject);
                    parameters.put(name, inject);
                }
                
                index ++;
            }
        }
    }
    
    /**
     * Search the value to inject from a parameter type.
     */
    public Object inject(Mapping mapping, Call call, Class<?> type) {

        HttpContext context = call.getContext();
        ErrorData errorData = context.getErrorData();
        Throwable cause = errorData.getCause();
        
        Object value = null;

        if (Mapping.class.isAssignableFrom(type)) {
            value = mapping;

        } else if (Config.class.isAssignableFrom(type)) {
            value = mapping.getConfig();

        } else if (Call.class.isAssignableFrom(type)) {
            value = call;

        } else if (HttpContext.class.isAssignableFrom(type)) {
            value = context;

        } else if (HttpSession.class.isAssignableFrom(type)) {
            value = context.getSession();

        } else if (ServletRequest.class.isAssignableFrom(type)) {
            value = context.getRequest();

        } else if (ServletResponse.class.isAssignableFrom(type)) {
            value = context.getResponse();

        } else if (ServerContext.class.isAssignableFrom(type)) {
            value = context.getServerContext();

        } else if (ServletContext.class.isAssignableFrom(type)) {
            value = context.getServletContext();

        } else if (HttpContext.ErrorData.class.isAssignableFrom(type)) {
            value = errorData;

        } else if (cause != null && cause.getClass().isAssignableFrom(type)) {
            value = cause;

        } else if (FileProgressListener.class.isAssignableFrom(type)) {
            value = context.getSession().getAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
            
        } else if (CookieManger.class.isAssignableFrom(type)) {
            value = context.getCookieManger();
        }
        
        return value;
    }
    
}

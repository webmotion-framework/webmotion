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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.ClientSession;
import org.debux.webmotion.server.call.CookieManger;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Properties;
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
 * <li>ClientSession</li>
 * </ul>
 * 
 * You can add injector in server context.
 * 
 * @author jruchaud
 */
public class ExecutorParametersInjectorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersInjectorHandler.class);

    protected List<Injector> injectors;
    
    @Override
    public void init(Mapping mapping, ServerContext context) {
        injectors = context.getInjectors();
        injectors.addAll(getBasicInjector());
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        Executor executor = call.getCurrent();
        
        Method executorMethod = executor.getMethod();
        Class<?>[] parameterTypes = executorMethod.getParameterTypes();
        Type[] genericParameterTypes = executorMethod.getGenericParameterTypes();
        Map<String, Object> parameters = executor.getParameters();

        // Search a value with a type
        int index = 0;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                Class<?> type = parameterTypes[index];
                Type generic = genericParameterTypes[index];

                for (Injector injector : injectors) {
                    Object inject = injector.getValue(mapping, call, type, generic);

                    if (inject != null) {
                        log.info("Inject " + name + " for type " + type + " the value " + inject);
                        parameters.put(name, inject);
                    }
                }
            }

            index ++;
        }
    }
    
    /**
     * Use to determine if inject the value in the parameter. 
     */
    public static interface Injector {
        
        /**
         * Get the value to inject.
         * 
         * @param mapping mapping
         * @param call call
         * @param type
         * @param generic of type
         * @return value
         */
        Object getValue(Mapping mapping, Call call, Class<?> type, Type generic);
    }

    /**
     * @return All basic injector.
     */
    protected List<Injector> getBasicInjector() {
        return Arrays.asList(
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (Mapping.class.isAssignableFrom(type)) {
                        return mapping;
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (Config.class.isAssignableFrom(type)) {
                        return mapping.getConfig();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (Properties.class.isAssignableFrom(type)) {
                        return mapping.getProperties();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (Call.class.isAssignableFrom(type)) {
                        return call;
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (HttpContext.class.isAssignableFrom(type)) {
                        return call.getContext();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (HttpSession.class.isAssignableFrom(type)) {
                        return call.getContext().getSession();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (ClientSession.class.isAssignableFrom(type)) {
                        return call.getContext().getClientSession();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (ServletRequest.class.isAssignableFrom(type)) {
                        return call.getContext().getRequest();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (ServletResponse.class.isAssignableFrom(type)) {
                        return call.getContext().getResponse();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (ServerContext.class.isAssignableFrom(type)) {
                        return call.getContext().getServerContext();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (ServletContext.class.isAssignableFrom(type)) {
                        return call.getContext().getServletContext();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (HttpContext.ErrorData.class.isAssignableFrom(type)) {
                        return call.getContext().getErrorData();
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    HttpContext context = call.getContext();
                    ErrorData errorData = context.getErrorData();
                    Throwable cause = errorData.getCause();
        
                    if (cause != null && cause.getClass().isAssignableFrom(type)) {
                        return cause;
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (FileProgressListener.class.isAssignableFrom(type)) {
                        return call.getContext().getSession().getAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
                    }
                    return null;
                }
            },
            new  Injector() {
                @Override
                public Object getValue(Mapping mapping, Call call, Class<?> type, Type generic) {
                    if (CookieManger.class.isAssignableFrom(type)) {
                        return call.getContext().getCookieManger();
                    }
                    return null;
                }
            }
        );
    }
    
}

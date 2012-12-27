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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.WebMotionContextable;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invokes methods of filters then action method. If a filter returns the 
 * render, the render action is avoided. Moreover the class manages async request, 
 * you can configure the thread pool.
 * 
 * @author julien
 */
public class ExecutorMethodInvokerHandler extends AbstractHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorMethodInvokerHandler.class);

    /** Context in controller */
    protected WebMotionContextable contextable;
    
    /** Pool async request */
    protected ExecutorService threadPool;

    public ExecutorMethodInvokerHandler(WebMotionContextable contextable, ExecutorService threadPool) {
        this.contextable = contextable;
        this.threadPool = threadPool;
    }

    public ExecutorMethodInvokerHandler() {
        this(new WebMotionContextable(), Executors.newFixedThreadPool(100));
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        ServletContext servletContext = context.getServletContext();

        // Search if the request is execute to sync or async mode
        boolean isSyncRequest = true;
        Rule rule = call.getRule();
        if (rule != null) {
            Action action = rule.getAction();
            Config config = mapping.getConfig();
            Boolean async = action.getAsync();
            
            isSyncRequest = request.getDispatcherType() == DispatcherType.INCLUDE 
                    || async == null && !config.isAsync()
                    || async != null && !async;
        }
        
        call.setAsync(!isSyncRequest);
        
        // Create handler to process the request
        RunnableHandler runnableHandler = new RunnableHandler(mapping, call);
        
        // Execute the request
        log.debug("is Async " + !isSyncRequest + " for url " + context.getUrl());
        if (isSyncRequest) {
            runnableHandler.handle(mapping, call);
            
        } else {
            // Only the first request is execute at async mode
            AsyncContext asyncContext;
            if (WebMotionUtils.isTomcatContainer(servletContext)) {
                request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
                // Tomcat patch : force the dispatcher type
                asyncContext = request.startAsync(new HttpServletRequestWrapper(request) {
                    @Override
                    public DispatcherType getDispatcherType() {
                        return DispatcherType.INCLUDE;
                    }
                } , response);
                
            } else {
                
                asyncContext = request.startAsync();
            }
            
            // Set timeout to negatif value otherwise no run glassfish server
            asyncContext.setTimeout(-1);
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    log.debug("onComplete " + event);
                }

                @Override
                public void onTimeout(AsyncEvent event) throws IOException {
                    log.warn("onTimeout " + event);
                }

                @Override
                public void onError(AsyncEvent event) throws IOException {
                    log.error("onError " + event, event.getThrowable());
                }

                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {
                    log.debug("onStartAsync");
                }
                
            });
            
            threadPool.execute(runnableHandler);
        }
    }

    /**
     * Runnable use direct execute or asyn execute the filters and the action.
     */
    public class RunnableHandler implements Runnable, WebMotionHandler {

        /** Current mapping */
        protected Mapping mapping;
        
        /** Current call */
        protected Call call;
        
        /** Current filters executed. */
        protected Iterator<Executor> filtersIterator;

        /** Mark if the render is executed */
        protected boolean executed;
        
        public RunnableHandler(Mapping mapping, Call call) {
            this.mapping = mapping;
            this.call = call;
            
            this.filtersIterator = call.getFilters().iterator();
            this.executed = false;
        }
        
        @Override
        public void handlerCreated(Mapping mapping, ServerContext context) {
            throw new UnsupportedOperationException("Not call.");
        }
        
        @Override
        public void handlerInitialized(Mapping mapping, ServerContext context) {
            throw new UnsupportedOperationException("Not call.");
        }

        @Override
        public void handlerDestroyed(Mapping mapping, ServerContext context) {
            throw new UnsupportedOperationException("Not call.");
        }

        @Override
        public void run() {
            handle(mapping, call);
        }

        @Override
        public void handle(Mapping mapping, Call call) {
            // Process action and filters
            Render render = call.getRender();
            Executor executor = call.getExecutor(); // Search if the call contains a action
            
            if (render == null || !executed) {
                if (filtersIterator.hasNext()) {
                    processFilter(mapping, call);
                    
                } else if (executor != null) {
                    processAction(mapping, call);
                    
                } else {
                    // The call contains already the render
                    processResponse(mapping, call);
                }
            }
        }
        
        /**
         * Process the action.
         * 
         * @param mapping
         * @param call 
         */
        public void processAction(Mapping mapping, Call call) {
            Executor executor = call.getExecutor();
            call.setCurrent(executor);
            processHandlers(mapping, call);
            
            try {
                
                WebMotionController instance = executor.getInstance();
                contextable.create(this, mapping, call);
                instance.setContextable(contextable);

                Map<String, Object> parameters = executor.getParameters();
                Object[] toArray = parameters.values().toArray();

                Method actionMethod = executor.getMethod();
                Render render = (Render) actionMethod.invoke(instance, toArray);

                // Check if is the last executor is finished to remove the thread local
                List<Executor> filters = call.getFilters();
                if (filters.isEmpty()) {
                    contextable.remove();
                }

                call.setRender(render);
                processResponse(mapping, call);
                
            } catch (IllegalAccessException ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for action " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, executor.getRule());
                
            } catch (IllegalArgumentException ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for action " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, executor.getRule());
                
            } catch (InvocationTargetException ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for action " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, executor.getRule());
            }
        }

        /**
         * Process one filter.
         * 
         * @param mapping
         * @param call 
         */
        public void processFilter(Mapping mapping, Call call) {
            Executor executor = filtersIterator.next();
            call.setCurrent(executor);
            processHandlers(mapping, call);
                
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
                if (executor == firstFilter) {
                    contextable.remove();
                }

                if (render != null) {
                    call.setRender(render);
                    processResponse(mapping, call);
                }
                
            } catch (IllegalAccessException ex) {
                contextable.remove();
                
                throw new WebMotionException("Error during invoke method for filter " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, executor.getRule());
                
            } catch (IllegalArgumentException ex) {
                contextable.remove();
                
                throw new WebMotionException("Error during invoke method for filter " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, executor.getRule());
                
            } catch (InvocationTargetException ex) {
                contextable.remove();
                
                Throwable cause = ex.getCause();
                if (cause instanceof WebMotionException 
                        && ((WebMotionException) cause).getRule() != null) {
                    throw (WebMotionException) cause;
                    
                } else {
                    throw new WebMotionException("Error during invoke method for filter " 
                            + executor.getClazz().getName() 
                            + " on method " + executor.getMethod().getName(),
                            ex, executor.getRule());
                }
            }
        }
        
        /**
         * Setting up the elements for the response :
         * <ul>
         * <li>save the client session</li>
         * <li>execute the render</li>
         * <li>remove attribute in session for file progress</li>
         * </ul>
         * 
         * @param mapping
         * @param call 
         */
        public void processResponse(Mapping mapping, Call call) {
            // Before render, store the client session
            HttpContext context = call.getContext();
            context.saveClientSession();
        
            try {
                Render render = call.getRender();
                log.debug("Render = " + render);
                if (render != null) {
                    render.exec(mapping, call);
                }
                executed = true;
                
            } catch (IOException ioe) {
                throw new WebMotionException("Error during write the render in response", ioe, call.getRule());

            } catch (ServletException se) {
                throw new WebMotionException("Error on server when write the render in response", se, call.getRule());
            }

            // After render, remove file progress from session
            if (call.isFileUploadRequest()) {
                HttpSession session = context.getSession();
                if (session != null) {
                    session.removeAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
                }
            }
        }
    
        /**
         * Execute the handler before each filters and action.
         * 
         * @param mapping
         * @param call 
         */
        public void processHandlers(Mapping mapping, Call call) {
            List<WebMotionHandler> executorHandlers = call.getExecutorHandlers();
            for (WebMotionHandler handler : executorHandlers) {
                handler.handle(mapping, call);
            }
        }
    }
}

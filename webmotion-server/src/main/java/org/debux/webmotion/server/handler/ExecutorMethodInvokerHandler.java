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
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
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
 * render, the render action is avoided.
 * 
 * @author julien
 */
public class ExecutorMethodInvokerHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorMethodInvokerHandler.class);

    /** Attribute name use to stop asynchronous request after a dispatch to execute the includes to synchronous */
    public static final String ASYNC_STOPED_ATTRIBUTE_NAME = "org.debux.webmotion.server.ASYNC_STOPED";
    
    protected WebMotionContextable contextable;
    
    protected ExecutorService threadPool;

    public ExecutorMethodInvokerHandler(WebMotionContextable contextable, ExecutorService threadPool) {
        this.contextable = contextable;
        this.threadPool = threadPool;
    }

    public ExecutorMethodInvokerHandler() {
        this(new WebMotionContextable(), Executors.newFixedThreadPool(100));
    }

    @Override
    public void init(Mapping mapping, ServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        // Search if the request is execute to sync or async mode
        boolean isSyncRequest = true;
        Rule rule = call.getRule();
        if (rule != null) {
            Action action = rule.getAction();
            Config config = mapping.getConfig();
            Boolean async = action.getAsync();
            
            isSyncRequest = request.getAttribute(ASYNC_STOPED_ATTRIBUTE_NAME) != null
                    || async == null && !config.isAsync()
                    || async != null && !async;
        }
        
        call.setAsync(!isSyncRequest);
        
        // Create handler to process the request
        RunnableHandler runnableHandler = new RunnableHandler(mapping, call);
        
        // Execute the request
        log.info("is Async " + !isSyncRequest + " for url " + context.getUrl());
        if (isSyncRequest) {
            runnableHandler.handle(mapping, call);
            
        } else {
            // Only the first request is execute at async mode
            request.setAttribute(ASYNC_STOPED_ATTRIBUTE_NAME, true);
            
            AsyncContext asyncContext;
            if (WebMotionUtils.isTomcatContainer(request)) {
                request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
                // Tomcat patch : force the dispatcher type
                asyncContext = request.startAsync(new HttpServletRequestWrapper(request) {
                    @Override
                    public DispatcherType getDispatcherType() {
                        return DispatcherType.INCLUDE;
                    }
                } , response);
                
            } else {
                asyncContext = request.startAsync(request, response);
            }
            
            asyncContext.setTimeout(0);
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    log.info("onComplete " + event);
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
                    log.info("onStartAsync");
                }
                
            });
            
            threadPool.execute(runnableHandler);
        }
    }
    
    public class RunnableHandler implements Runnable, WebMotionHandler {

        protected Mapping mapping;
        protected Call call;
        
        /** Current filters executed. */
        protected Iterator<Executor> filtersIterator;
        protected int filtersIndex;

        /** Mark if the render is executed */
        protected boolean executed;
        
        public RunnableHandler(Mapping mapping, Call call) {
            this.mapping = mapping;
            this.call = call;
            
            this.filtersIterator = call.getFilters().iterator();
            this.filtersIndex = 0;
            this.executed = false;
        }
        
        @Override
        public void init(Mapping mapping, ServerContext context) {
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
                    processRender(mapping, call);
                }
            }
        }
        
        public void processAction(Mapping mapping, Call call) {
            Executor executor = call.getExecutor();
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
                processRender(mapping, call);
                
            } catch (IllegalAccessException ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for action " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, call.getRule());
            } catch (IllegalArgumentException ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for action " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, call.getRule());
            } catch (InvocationTargetException ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for action " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, call.getRule());
            }
        }

        public void processFilter(Mapping mapping, Call call) {
            Executor executor = filtersIterator.next();
            filtersIndex ++;
            
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
                    processRender(mapping, call);
                }
                
            } catch (IllegalAccessException ex) {
                contextable.remove();
                
                FilterRule filterRule = call.getFilterRules().get(filtersIndex - 1);
                throw new WebMotionException("Error during invoke method for filter " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, filterRule);
            } catch (IllegalArgumentException ex) {
                contextable.remove();
                
                FilterRule filterRule = call.getFilterRules().get(filtersIndex - 1);
                throw new WebMotionException("Error during invoke method for filter " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(),
                        ex, filterRule);
            } catch (InvocationTargetException ex) {
                contextable.remove();
                
                Throwable cause = ex.getCause();
                if (cause instanceof WebMotionException) {
                    throw (WebMotionException) cause;
                    
                } else {
                    FilterRule filterRule = call.getFilterRules().get(filtersIndex - 1);
                    throw new WebMotionException("Error during invoke method for filter " 
                            + executor.getClazz().getName() 
                            + " on method " + executor.getMethod().getName(),
                            ex, filterRule);
                }
            }
        }
        
        public void processRender(Mapping mapping, Call call) {
            try {
                Render render = call.getRender();
                log.info("Render = " + render);
                if (render != null) {
                    render.exec(mapping, call);
                }
                executed = true;
                
            } catch (IOException ioe) {
                throw new WebMotionException("Error during write the render in response", ioe, call.getRule());

            } catch (ServletException se) {
                throw new WebMotionException("Error on server when write the render in response", se, call.getRule());
            }

            if(call.isFileUploadRequest()) {
                HttpContext context = call.getContext();
                HttpSession session = context.getSession();
                if(session != null) {
                    session.removeAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
                }
            }
        }
    
    }
}

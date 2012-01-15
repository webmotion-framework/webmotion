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
import java.util.Iterator;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.WebMotionContextable;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.InitContext;
import org.debux.webmotion.server.render.RenderView;
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
    
    protected ExecutorService threadPool;

    public ExecutorMethodInvokerHandler(WebMotionContextable contextable, ExecutorService threadPool) {
        this.contextable = contextable;
        this.threadPool = threadPool;
    }

    public ExecutorMethodInvokerHandler() {
        this(new WebMotionContextable(), Executors.newFixedThreadPool(10));
    }

    public void setContextable(WebMotionContextable contextable) {
        this.contextable = contextable;
    }

    public void setExecutor(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }
    
    @Override
    public void init(InitContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        RunnableHandler runnableHandler = new RunnableHandler(mapping, call);
        runnableHandler.handle(mapping, call);

// Begin implements async requestt process. Don't remove comment lines.
//        AsyncContext asyncContext = null;
//        if (request.getAttribute("ASYNC_STOPPED") != null) {
//            runnableHandler.handle(mapping, call);
//            
//        } else {
//            request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
//            request.setAttribute("ASYNC_STOPPED", true);
//            
//            // Tomcat path on dispatcher type
//            asyncContext = request.startAsync(new HttpServletRequestWrapper(request) {
//                @Override
//                public DispatcherType getDispatcherType() {
//                    return DispatcherType.INCLUDE;
//                }
//            } , response);
//            
////            asyncContext = request.startAsync(request, response);
//            
//            asyncContext.setTimeout(0);
//            asyncContext.addListener(new AsyncListener() {
//                @Override
//                public void onComplete(AsyncEvent event) throws IOException {
//                    log.info("onComplete " + event);
//                }
//
//                @Override
//                public void onTimeout(AsyncEvent event) throws IOException {
//                    log.info("onTimeout " + event);
//                }
//
//                @Override
//                public void onError(AsyncEvent event) throws IOException {
//                    log.error("onError " + event, event.getThrowable());
//                }
//
//                @Override
//                public void onStartAsync(AsyncEvent event) throws IOException {
//                    log.info("onStartAsync ");
//                }
//                
//            });
//            
//            threadPool.execute(runnableHandler);
//        }
    }
    
    public class RunnableHandler implements Runnable, WebMotionHandler {

        protected Mapping mapping;
        protected Call call;
        
        /** Current filters executed. */
        protected Iterator<Executor> filtersIterator;

        public RunnableHandler(Mapping mapping, Call call) {
            this.mapping = mapping;
            this.call = call;
            this.filtersIterator = call.getFilters().iterator();
        }
        
        @Override
        public void init(InitContext context) {
            throw new UnsupportedOperationException("Not call.");
        }

        @Override
        public void run() {
            handle(mapping, call);
            
            Render render = call.getRender();
            if (!(render instanceof RenderView)) {
                HttpContext context = call.getContext();
                HttpServletRequest request = context.getRequest();
                AsyncContext asyncContext = request.getAsyncContext();
                asyncContext.complete();
            }
        }

        @Override
        public void handle(Mapping mapping, Call call) {
            // Process action and filters
            Render render = call.getRender();
            Executor executor = call.getExecutor(); // Search if the call contains a action
            
            if (render == null || !render.isExecuted()) {
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
                if(filters.isEmpty()) {
                    contextable.remove();
                }

                call.setRender(render);
                processRender(mapping, call);
                
            } catch (Exception ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for filter " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(), ex);
            }
        }

        public void processFilter(Mapping mapping, Call call) {
            Executor executor = filtersIterator.next();

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
                    processRender(mapping, call);
                }

            } catch (Exception ex) {
                contextable.remove();
                throw new WebMotionException("Error during invoke method for filter " 
                        + executor.getClazz().getName() 
                        + " on method " + executor.getMethod().getName(), ex);
            }
        }
        
        public void processRender(Mapping mapping, Call call) {
            try {
                Render render = call.getRender();
                log.info("Render = " + render);
                if(render != null) {
                    render.exec(mapping, call);
                }
                
            } catch (IOException ioe) {
                throw new WebMotionException("Error during write the render in response", ioe);

            } catch (ServletException se) {
                throw new WebMotionException("Error on server when write the render in response", se);
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

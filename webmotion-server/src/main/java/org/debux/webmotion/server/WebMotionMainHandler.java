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
package org.debux.webmotion.server;

import org.debux.webmotion.server.tools.HttpUtils;
import org.debux.webmotion.server.call.ServerContext;
import java.util.ArrayList;
import java.util.List;
import org.debux.webmotion.server.tools.SingletonFactory;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.handler.ActionExecuteRenderHandler;
import org.debux.webmotion.server.handler.ExecutorInstanceCreatorHandler;
import org.debux.webmotion.server.handler.ActionFinderHandler;
import org.debux.webmotion.server.handler.ActionMethodFinderHandler;
import org.debux.webmotion.server.handler.ErrorFinderHandler;
import org.debux.webmotion.server.handler.ErrorMethodFinderHandler;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler;
import org.debux.webmotion.server.handler.FilterMethodFinderHandler;
import org.debux.webmotion.server.handler.ExecutorMethodInvokerHandler;
import org.debux.webmotion.server.handler.ExecutorParametersConvertorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler;
import org.debux.webmotion.server.handler.FilterFinderHandler;
import org.debux.webmotion.server.handler.ParametersExtractorHandler;
import org.debux.webmotion.server.handler.ParametersMultipartHandler;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Rule;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.tools.OrderedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get chains handlers to process user request when the request is action or 
 * error type.
 * 
 * It is possible to extends to :
 * <ul>
 * <li>Add new handlers in chains, with extending of getActionHandlers or getErrorHandlers</li>
 * <li>Parameters handlers, with extending of init</li>
 * <li>Modify handler creation, with extending of getHandler</li>
 * </ul>
 * 
 * @author julien
 */
public class WebMotionMainHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebMotionMainHandler.class);

    /** All handlers are a singleton */
    protected SingletonFactory<WebMotionHandler> factory;
    
    /** Mbean for handlers */
    protected HandlerStats handlerStats;
    
    /** All handlers use to process an action */
    protected List<WebMotionHandler> actionHandlers;
    
    /** All handlers use to process an error */
    protected List<WebMotionHandler> errorHandlers;

    /** All handlers use to process an executor */
    protected List<WebMotionHandler> executorHandlers;

    @Override
    public void handlerCreated(Mapping mapping, ServerContext context) {
        factory = context.getHandlers();
        handlerStats = context.getHandlerStats();
    }

    @Override
    public void handlerInitialized(Mapping mapping, ServerContext context) {
        initHandlers(mapping, context);
        initExtensions(mapping, context);
    }

    /**
     * Init all handlers to process the request
     */
    protected void initHandlers(Mapping mapping, ServerContext context) {
        List<Class<? extends WebMotionHandler>> actionClasses = getActionHandlers();
        actionHandlers = initHandlers(mapping, context, actionClasses);

        List<Class<? extends WebMotionHandler>> errorClasses = getErrorHandlers();
        errorHandlers = initHandlers(mapping, context, errorClasses);
        
        List<Class<? extends WebMotionHandler>> executorClasses = getExecutorHandlers();
        executorHandlers = initHandlers(mapping, context, executorClasses);
    }
    
    /**
     * Chain init methods on the handlers
     */
    protected List<WebMotionHandler> initHandlers(Mapping mapping, ServerContext context, List<Class<? extends WebMotionHandler>> classes) {
        List<WebMotionHandler> handlers = new ArrayList<WebMotionHandler>(classes.size());

        for (Class<? extends WebMotionHandler> clazz : classes) {
            WebMotionHandler handler = getHandler(mapping, context, clazz);
            handler.handlerInitialized(mapping, context);
            handlers.add(handler);
        }
        
        return handlers;
    }
    
    /**
     * Use to create handlers other WebMotionHandlerFactory
     */
    public WebMotionHandler getHandler(Mapping mapping, ServerContext context, Class<? extends WebMotionHandler> clazz) {
        WebMotionHandler handler = factory.get(clazz);
        if (handler == null) {
            handler = factory.createInstance(clazz);
            handler.handlerCreated(mapping, context);
        }
        return handler;
    }
    
    /**
     * Init handler factory for extension
     */
    protected void initExtensions(Mapping mapping, ServerContext context) {
        List<Mapping> extensionsRules = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensionsRules) {
            
            Config extensionConfig = extensionMapping.getConfig();
            String className = extensionConfig.getMainHandler();
            
            WebMotionHandler mainHandler = factory.get(className);
            if (mainHandler == null) {
                mainHandler = factory.createInstance(className);
                mainHandler.handlerCreated(extensionMapping, context);
            }
            mainHandler.handlerInitialized(extensionMapping, context);
        }
    }
    
    @Override
    public void handlerDestroyed(Mapping mapping, ServerContext context) {
        for (WebMotionHandler handler : actionHandlers) {
            handler.handlerDestroyed(mapping, context);
        }
        for (WebMotionHandler handler : errorHandlers) {
            handler.handlerDestroyed(mapping, context);
        }
        for (WebMotionHandler handler : executorHandlers) {
            handler.handlerDestroyed(mapping, context);
        }
        
        List<Mapping> extensionsRules = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensionsRules) {
            
            Config extensionConfig = extensionMapping.getConfig();
            String className = extensionConfig.getMainHandler();
            
            WebMotionHandler mainHandler = factory.get(className);
            if (mainHandler != null) {
                mainHandler.handlerDestroyed(extensionMapping, context);
            }
        }
        
        factory.remove(getClass());
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        long start = System.currentTimeMillis();
        handleExtension(mapping, call);

        Rule rule = call.getRule();
        if (rule == null) {
            // Not process in extension
            handleExecutors(mapping, call);
        }

        handlerStats.registerHandlerTime(this.getClass().getName(), start);
    }

    /**
     * Begin by search in extension if a rule is available.
     * 
     * @param mapping
     * @param call 
     */
    protected void handleExtension(Mapping mapping, Call call) {
        HttpContext context = call.getContext();

        // Determine the extension is used
        String url = context.getUrl();
        log.debug("url = " + url);

        List<Mapping> extensionsRules = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensionsRules) {

            String path = extensionMapping.getExtensionPath();
            log.debug("path = " + path);
            if ("/".equals(path) 
                    || HttpUtils.find("^" + path + "(/|$)", url)) {

                context.addExtensionPath(path);

                Config newConfig = extensionMapping.getConfig();
                String className = newConfig.getMainHandler();

                WebMotionHandler mainHandler = factory.get(className);
                mainHandler.handle(extensionMapping, call);

                context.removeExtensionPath(path);

                // Stop if the first handler process the request
                Rule rule = call.getRule();
                if (rule != null) {
                    break;
                }
            }
        }
    }
    
    /**
     * Next find a rule in current mapping.
     * 
     * @param mapping
     * @param call 
     */
    protected void handleExecutors(Mapping mapping, Call call) {
        // Add handlers used during executor invoker
        call.setExecutorHandlers(executorHandlers);

        // Determine if the request contains an errors
        HttpContext context = call.getContext();
        if (context.isError()) {
            ErrorData errorData = context.getErrorData();
            log.error("Error " + errorData.getStatusCode() + " : " + errorData.getMessage() 
                    + " on " + errorData.getRequestUri(), errorData.getException());
            chainHandlers(errorHandlers, mapping, call);
        } else {
            chainHandlers(actionHandlers, mapping, call);
        }
    }
    
    /**
     * Chain handlers
     */
    protected void chainHandlers(List<WebMotionHandler> handlers, Mapping mapping, Call call) {        
        for (WebMotionHandler handler : handlers) {
            long start = System.currentTimeMillis();
            handler.handle(mapping, call);
            handlerStats.registerHandlerTime(handler.getClass().getName(), start);
        }
    }
    
    /**
     * @return list of {@see WebMotionHandler} that will be processed for action handling
     */
    public OrderedList<Class<? extends WebMotionHandler>> getActionHandlers() {
        return OrderedList.asList(ParametersMultipartHandler.class,
            ActionFinderHandler.class,
            FilterFinderHandler.class,
            ParametersExtractorHandler.class,
            ActionExecuteRenderHandler.class,
            ActionMethodFinderHandler.class,
            FilterMethodFinderHandler.class,
            ExecutorMethodInvokerHandler.class
        );
    }
    
    /**
     * @return list of {@see WebMotionHandler} that will be processed for error handling
     */
    public OrderedList<Class<? extends WebMotionHandler>> getErrorHandlers() {
        return OrderedList.asList(
            ParametersMultipartHandler.class,
            ErrorFinderHandler.class,
            ActionExecuteRenderHandler.class,
            ErrorMethodFinderHandler.class,
            ExecutorMethodInvokerHandler.class
        );
    }
    
    /**
     * @return list of {@see WebMotionHandler} that will be processed for executor
     */
    public OrderedList<Class<? extends WebMotionHandler>> getExecutorHandlers() {
        return OrderedList.asList(
            ExecutorInstanceCreatorHandler.class,
            ExecutorParametersInjectorHandler.class,
            ExecutorParametersConvertorHandler.class,
            ExecutorParametersValidatorHandler.class
        );
    }
    
}

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

import org.debux.webmotion.server.call.ServerContext;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Extension;
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
import org.debux.webmotion.server.mbean.HandlerStats;
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
public class WebMotionHandlerFactory implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebMotionHandlerFactory.class);

    /** All handlers are a singleton */
    protected SingletonFactory<WebMotionHandler> factory;
    
    /** Mbean for handlers */
    protected HandlerStats handlerStats;
    
    /** All handlers use to process an action */
    protected List<WebMotionHandler> actionHandlers;
    
    /** All handlers use to process an error */
    protected List<WebMotionHandler> errorHandlers;

    @Override
    public void init(Mapping mapping, ServerContext context) {
        factory = context.getHandlers();
        handlerStats = context.getHandlerStats();
        
        if (actionHandlers == null && errorHandlers == null) {
            initHandlers(mapping, context);
        }
        
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
    }
    
    /**
     * Chain init methods on the handlers
     */
    protected List<WebMotionHandler> initHandlers(Mapping mapping, ServerContext context, List<Class<? extends WebMotionHandler>> classes) {
        List<WebMotionHandler> handlers = new ArrayList<WebMotionHandler>(classes.size());

        for (Class<? extends WebMotionHandler> clazz : classes) {
            WebMotionHandler handler = getHandler(clazz);
            handler.init(mapping, context);
            handlers.add(handler);
        }
        
        return handlers;
    }
    
    /**
     * Use to create handlers other WebMotionHandlerFactory
     */
    public WebMotionHandler getHandler(Class<? extends WebMotionHandler> clazz) {
        WebMotionHandler handler = factory.getInstance(clazz);
        return handler;
    }
    
    /**
     * Init handler factory for extension
     */
    protected void initExtensions(Mapping mapping, ServerContext context) {
        List<Mapping> extensionsRules = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensionsRules) {
            
            Config extensionConfig = extensionMapping.getConfig();
            String handlersFactory = extensionConfig.getHandlersFactory();
            
            WebMotionHandler handlerFactory = factory.getInstance(handlersFactory);
            handlerFactory.init(extensionMapping, context);
        }
    }
    
    @Override
    public void handle(Mapping mapping, Call call) {
        long start = System.currentTimeMillis();
        HttpContext context = call.getContext();
        
        // Apply config
        Config config = mapping.getConfig();
        String requestEncoding = config.getRequestEncoding();
        HttpServletRequest request = context.getRequest();
        try {
            request.setCharacterEncoding(requestEncoding);
        } catch (UnsupportedEncodingException encodingException) {
            throw new WebMotionException("Invalid encoding for request", encodingException);
        }

        // Determine the extension is used
        String url = context.getUrl();
        String extensionPath = context.getExtensionPath();
        log.info("url = " + url);
        log.info("extension path = " + extensionPath);

        List<Mapping> extensionsRules = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensionsRules) {
            Extension extension = extensionMapping.getExtension();
            String path = extension.getPath();
            
            log.info("path = " + path);
            if(url.startsWith(path)) {
                
                // Not change path when the extension is mount on root
                if(!"/".equals(path)) {
                    context.setExtensionPath(extensionPath + path);
                }
                
                Config newConfig = extensionMapping.getConfig();
                String handlersFactory = newConfig.getHandlersFactory();
                
                WebMotionHandler handler = factory.getInstance(handlersFactory);
                handler.handle(extensionMapping, call);
                
                context.setExtensionPath(extensionPath);
                
                // Stop if the first handler process the request
                ActionRule actionRule = call.getActionRule();
                ErrorRule errorRule = call.getErrorRule();
                if(actionRule != null || errorRule != null) {
                    break;
                }
            }
        }
        
        ActionRule actionRule = call.getActionRule();
        ErrorRule errorRule = call.getErrorRule();
        if(actionRule == null && errorRule == null) {
            // Determine if the request contains an errors
            if(context.isError()) {
                ErrorData errorData = context.getErrorData();
                log.error("Error " + errorData.getStatusCode() + " : " + errorData.getMessage() 
                        + " on " + errorData.getRequestUri(), errorData.getException());
                chainHandlers(errorHandlers, mapping, call);
            } else {
                chainHandlers(actionHandlers, mapping, call);
            }
        }
        
        handlerStats.registerHandlerTime(this.getClass().getName(), start);
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
    public List<Class<? extends WebMotionHandler>> getActionHandlers() {
        return Arrays.asList(
                    ParametersMultipartHandler.class,
                    ActionFinderHandler.class,
                    FilterFinderHandler.class,
                    ParametersExtractorHandler.class,
                    ActionExecuteRenderHandler.class,
                    ActionMethodFinderHandler.class,
                    FilterMethodFinderHandler.class,
                    ExecutorInstanceCreatorHandler.class,
                    ExecutorParametersConvertorHandler.class,
                    ExecutorParametersInjectorHandler.class,
                    ExecutorParametersValidatorHandler.class,
                    ExecutorMethodInvokerHandler.class
                );
    }
    
    /**
     * @return list of {@see WebMotionHandler} that will be processed for error handling
     */
    public List<Class<? extends WebMotionHandler>> getErrorHandlers() {
        return Arrays.asList(
                    ParametersMultipartHandler.class,
                    ErrorFinderHandler.class,
                    ActionExecuteRenderHandler.class,
                    ErrorMethodFinderHandler.class,
                    ExecutorInstanceCreatorHandler.class,
                    ExecutorParametersInjectorHandler.class,
                    ExecutorMethodInvokerHandler.class
                );
    }
    
}

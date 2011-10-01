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

import java.util.ArrayList;
import java.util.List;
import org.debux.webmotion.server.handler.ActionExecuteRenderHandler;
import org.debux.webmotion.server.handler.ExecutorInstanceCreatorHandler;
import org.debux.webmotion.server.handler.ActionFinderHandler;
import org.debux.webmotion.server.handler.ActionMethodFinderHandler;
import org.debux.webmotion.server.handler.ErrorFinderHandler;
import org.debux.webmotion.server.handler.ErrorMethodFinderHandler;
import org.debux.webmotion.server.handler.FilterMethodFinderHandler;
import org.debux.webmotion.server.handler.ExecutorMethodInvokerHandler;
import org.debux.webmotion.server.handler.ExecutorParametersConvertorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler;
import org.debux.webmotion.server.handler.FilterFinderHandler;
import org.debux.webmotion.server.handler.ParametersExtractorHandler;
import org.debux.webmotion.server.handler.ParametersMultipartHandler;
import org.debux.webmotion.server.handler.RenderCreatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get chains handlers to process user request when the request is action or 
 * error type.
 * 
 * @author julien
 */
public class WebMotionHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(WebMotionHandlerFactory.class);

    /**
     * Default constructor.
     */
    public WebMotionHandlerFactory() {
    }
    
    /**
     * @return list of {@see WebMotionHandler} that will be processed for action handling
     */
    public List<WebMotionHandler> getActionHandlers() {
        List<WebMotionHandler> handlers = new ArrayList<WebMotionHandler>();
        
        handlers.add(new ParametersMultipartHandler());
        handlers.add(new FilterFinderHandler());
        handlers.add(new ActionFinderHandler());
        handlers.add(new ParametersExtractorHandler());
        handlers.add(new FilterMethodFinderHandler());
        handlers.add(new ActionExecuteRenderHandler());
        handlers.add(new ActionMethodFinderHandler());
        handlers.add(new ExecutorInstanceCreatorHandler());
        handlers.add(new ExecutorParametersConvertorHandler());
        // jru:20111001 Desactivate for the release 1.4, uncomment for version 2.0
        //handlers.add(new ExecutorParametersValidatorHandler());
        handlers.add(new ExecutorMethodInvokerHandler());
        handlers.add(new RenderCreatorHandler());
        
        return handlers;
    }
    
    /**
     * @return list of {@see WebMotionHandler} that will be processed for error handling
     */
    public List<WebMotionHandler> getErrorHandlers() {
        List<WebMotionHandler> handlers = new ArrayList<WebMotionHandler>();
        
        handlers = new ArrayList<WebMotionHandler>();
        handlers.add(new ParametersMultipartHandler());
        handlers.add(new ErrorFinderHandler());
        handlers.add(new ActionExecuteRenderHandler());
        handlers.add(new ErrorMethodFinderHandler());
        handlers.add(new ExecutorInstanceCreatorHandler());
        handlers.add(new ExecutorMethodInvokerHandler());
        handlers.add(new RenderCreatorHandler());
        
        return handlers;
    }

}

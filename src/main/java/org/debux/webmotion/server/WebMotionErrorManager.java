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
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.handler.ActionExecuteRenderHandler;
import org.debux.webmotion.server.handler.ErrorFinderHandler;
import org.debux.webmotion.server.handler.ErrorMethodFinderHandler;
import org.debux.webmotion.server.handler.ExecutorInstanceCreatorHandler;
import org.debux.webmotion.server.handler.ExecutorMethodInvokerHandler;
import org.debux.webmotion.server.handler.ParametersMultipartHandler;
import org.debux.webmotion.server.handler.RenderCreatorHandler;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chains handlers to process user request when the request is error type.
 * The error request can have filters. The handler is used when an 
 * exception or a HTTP error code is found in the request.
 * 
 * @author jruchaud
 */
public class WebMotionErrorManager implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebMotionActionManager.class);

    /**
     * List of {@see WebMotionHandler} that will be processed for error handling.
     */
    protected List<WebMotionHandler> handlers;

    /**
     * Default constructor.
     */
    public WebMotionErrorManager() {
        handlers = new ArrayList<WebMotionHandler>();
        handlers.add(new ParametersMultipartHandler());
        handlers.add(new ErrorFinderHandler());
        handlers.add(new ActionExecuteRenderHandler());
        handlers.add(new ErrorMethodFinderHandler());
        handlers.add(new ExecutorInstanceCreatorHandler());
        handlers.add(new ExecutorMethodInvokerHandler());
        handlers.add(new RenderCreatorHandler());
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        // Log
        ErrorData errorData = call.getErrorData();
        log.error("Error code : " + errorData.getStatusCode());
        if(errorData.isException()) {
            log.error("Error during execution", errorData.getException());
        }
        
        for (WebMotionHandler handler : handlers) {
            handler.handle(mapping, call);
        }
    }
}

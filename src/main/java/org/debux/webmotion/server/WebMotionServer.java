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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.parser.BasicMappingParser;
import org.debux.webmotion.server.parser.MappingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main servlet manages all call on WebMotion. The servlet invokes two differents 
 * handlers, the first for classical action management and the other one for error management. The 
 * handlers chain others handlers to answer the user query.
 * 
 * @author jruchaud
 */
public class WebMotionServer extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(WebMotionServer.class);

    protected Mapping mapping;
    protected Config config;
    
    protected WebMotionHandler actionManager;
    protected WebMotionHandler errorManager;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        InputStream stream = getClass().getResourceAsStream(MappingParser.MAPPING_FILE_NAME);
        MappingParser parser = new BasicMappingParser();
        mapping = parser.parse(stream);
        config = mapping.getConfig();
            
        try {
            String handlersFactory = config.getHandlersFactory();
            WebMotionHandlerFactory factory = (WebMotionHandlerFactory) Class.forName(handlersFactory).newInstance();
            
            List<WebMotionHandler> actionHandlers = factory.getActionHandlers();
            actionManager = getHandlerManager(actionHandlers);
            
            List<WebMotionHandler> errorHandlers = factory.getErrorHandlers();
            errorManager = getHandlerManager(errorHandlers);
            
        } catch (Exception ex) {
            throw new WebMotionException("Invalid class name for handlers factory", ex);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Apply config
        String requestEncoding = config.getRequestEncoding();
        request.setCharacterEncoding(requestEncoding);
        
        // Determine if the request contains an errors
        Call call = new Call(request, response);
        ErrorData errorData = call.getErrorData();
        if(errorData.isError()) {
            errorManager.handle(mapping, call);
        } else {
            actionManager.handle(mapping, call);
        }
    }
    
    protected WebMotionHandler getHandlerManager(final List<WebMotionHandler> handlers) {
        return new WebMotionHandler() {
            @Override
            public void handle(Mapping mapping, Call call) {
                for (WebMotionHandler handler : handlers) {
                    handler.handle(mapping, call);
                }
            }
        };
    }
}

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
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get static resource like jsp, image, ... If the file doesn't have a 
 * extension, you must call this servlet otherwise the request is considered 
 * like WebMotion action.
 * 
 * @author julien
 */
public class WebMotionStaticServlet extends HttpServlet {
    
    private static final Logger log = LoggerFactory.getLogger(WebMotionStaticServlet.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        log.info("Pass in DefaultWrapperServlet");
        ServletContext servletContext = getServletContext();
        
        // Suppress the static in path
        HttpServletRequest requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getServletPath() {
                return "";
            }
            
            @Override
            public String getPathInfo() {
                return super.getPathInfo().replaceFirst("/static", "");
            }

            @Override
            public String getRequestURI() {
                return super.getRequestURI().replaceFirst("/static", "");
            }
        };

        // Dispatch on jsp servlet
        RequestDispatcher dispatcher = servletContext.getNamedDispatcher("jsp");
        
        DispatcherType dispatcherType = request.getDispatcherType();
        if(dispatcherType == DispatcherType.INCLUDE) {
            dispatcher.include(requestWrapper, response);
        } else {
            dispatcher.forward(requestWrapper, response);
        }
    }
}

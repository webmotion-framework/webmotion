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
import java.util.regex.Pattern;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.call.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The filter routes all the request for webmotion without /deploy to 
 * WebMotionServer if is not a file.
 * 
 * @author julien
 */
public class WebMotionRouterFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(WebMotionRouterFilter.class);

    /** Test if the path contains a extension */
    protected static Pattern patternFile = Pattern.compile("\\..{2,4}$");

    protected WebMotionMainServlet servlet;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servlet = new WebMotionMainServlet();
        servlet.init(null);
    }

    @Override
    public void destroy() {
        // Do nothing
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
        httpServletRequest.getSession(true);
        
        String uri;
        DispatcherType dispatcherType = request.getDispatcherType();
        if(dispatcherType == DispatcherType.INCLUDE) {
            uri = (String) httpServletRequest.getAttribute(HttpContext.ATTRIBUTE_INCLUDE_REQUEST_URI);
        } else {
            uri = httpServletRequest.getRequestURI();
        }
        
        String contextPath = httpServletRequest.getContextPath();
        String url = StringUtils.substringAfter(uri, contextPath);
        
        log.info("Pass in filter = " + url);
        
        if(url.startsWith("/deploy")) {
            // Prevent loop
            log.info("Is deploy");
            chain.doFilter(request, response);
            
        } else if(url.startsWith("/static")) {
            // Prevent loop
            log.info("Is static");
            chain.doFilter(request, response);
            
        } else if(patternFile.matcher(url).find()) {
            // css js html png jpg jpeg xml jsp jspx ...
            log.info("Is file");
            chain.doFilter(request, response);
            
        } else {
            log.info("Is default");
            ServletContext servletContext = request.getServletContext();
            WebMotionService listener = (WebMotionService) servletContext.getAttribute(WebMotionService.SERVICE_ATTRIBUTE_NAME);
            listener.service(httpServletRequest, httpServletResponse);
        }
    }
}

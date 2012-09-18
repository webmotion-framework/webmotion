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
package org.debux.webmotion.server;

import java.io.File;
import org.debux.webmotion.server.call.ServerContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mbean.ServerStats;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.websocket.WebSocketInbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main filter manages all call on WebMotion. The servlet invokes two differents 
 * process, the first for classical action management and the other one for static resources.
 * 
 * @author julien
 */
public class WebMotionServer implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(WebMotionServer.class);

    /** Path uses to manage action in WebMotion */
    public static final String PATH_DEPLOY = "/deploy";
    
    /** Path uses to manage error in WebMotion */
    public static final String PATH_ERROR = "/error";
    
    /** Path uses to manage resources outside WebMotion */
    public static final String PATH_STATIC = "/static";
    
    /** Path uses to manage servlets outside WebMotion  */
    public static final String PATH_SERVLET = "/servlet";

    /* Servlet name to store delegate to websocket servlet */
    public static final String SERVLET_WEBSOCKET = "wm.websocket";
            
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void destroy() {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
        ServerContext serverContext = getServerContext(httpServletRequest);
        
        String uri = null;
        DispatcherType dispatcherType = request.getDispatcherType();
        if (dispatcherType == DispatcherType.INCLUDE) {
            uri = (String) httpServletRequest.getAttribute(HttpContext.ATTRIBUTE_INCLUDE_REQUEST_URI);
        }
        if (uri == null) {
            uri = httpServletRequest.getRequestURI();
        }
        
        String contextPath = httpServletRequest.getContextPath();
        String url = StringUtils.substringAfter(uri, contextPath);
        log.debug("Pass in filter = " + url);
        
        // Search if url is exclude path
        for (String path :  serverContext.getExcludePaths()) {
            if (url.startsWith(path)) {
                url = PATH_SERVLET + url;
                break;
            }
        }
        
        if (url.startsWith(PATH_DEPLOY) || url.startsWith(PATH_ERROR) || url.equals("/")) {
            log.debug("Is deploy");
            doAction(httpServletRequest, httpServletResponse);
            
        } else if (url.startsWith(PATH_STATIC)) {
            log.debug("Is static");
            doResource(httpServletRequest, httpServletResponse);
            
        } else if (url.startsWith(PATH_SERVLET)) {
            log.debug("Is servlet");
            chain.doFilter(request, response);
            
        } else {
            String webappPath = serverContext.getWebappPath();
            File file = new File(webappPath, url);
            if (file.exists()) {
                // css js html png jpg jpeg xml ...
                log.debug("Is file");
                chain.doFilter(request, response);
            } else {
                log.debug("Is default");
                doAction(httpServletRequest, httpServletResponse);
            }
        }
    }
    
    /**
     * Action management in the mapping
     */
    protected void doAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        long start = System.currentTimeMillis();
        
        // Create call context use in handler to get information on user request
        ServerContext serverContext = getServerContext(request);
        Call call = new Call(serverContext, request, response);
        
        // Apply config
        Mapping mapping = serverContext.getMapping();
        applyConfig(mapping, call);

        // Execute the main handler
        WebMotionHandler mainHandler = serverContext.getMainHandler();
        mainHandler.handle(mapping, call);
        
        // Register call in mbean
        ServerStats serverStats = serverContext.getServerStats();
        serverStats.registerCallTime(call, start);

        // Dispatch on servlet to manage websocket
        WebSocketInbound socket = (WebSocketInbound) request.getAttribute(WebSocketInbound.ATTRIBUTE_WEBSOCKET);
        if (socket != null) {
            ServletContext servletContext = request.getServletContext();
            RequestDispatcher dispatcher = servletContext.getNamedDispatcher(SERVLET_WEBSOCKET);
            dispatcher.forward(request, response);
        }
    }
    
    /**
     * Apply config in mapping on request and response
     */
    protected void applyConfig(Mapping mapping, Call call) throws WebMotionException {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        Config config = mapping.getConfig();
        String encoding = config.getEncoding();
        try {
            request.setCharacterEncoding(encoding);
            response.setCharacterEncoding(encoding);
            
        } catch (UnsupportedEncodingException encodingException) {
            throw new WebMotionException("Invalid encoding for request", encodingException);
        }
    }

    /**
     * Static resources management
     */
    protected void doResource(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Suppress the static in path
        HttpServletRequest requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String getServletPath() {
                String servletPath = super.getServletPath();
                if (servletPath != null) {
                    return servletPath.replaceFirst(PATH_STATIC, "");
                }
                return null;
            }
            
            @Override
            public String getPathInfo() {
                String pathInfo = super.getPathInfo();
                if (pathInfo != null) {
                    return pathInfo.replaceFirst(PATH_STATIC, "");
                }
                return null;
            }

            @Override
            public String getRequestURI() {
                String requestURI = super.getRequestURI();
                if (requestURI != null) {
                    return requestURI.replaceFirst(PATH_STATIC, "");
                }
                return null;
            }
        };

        // Dispatch on default servlet
        ServletContext servletContext = request.getServletContext();
        RequestDispatcher dispatcher = servletContext.getNamedDispatcher("default");
        
        DispatcherType dispatcherType = request.getDispatcherType();
        if(dispatcherType == DispatcherType.INCLUDE) {
            dispatcher.include(requestWrapper, response);
        } else {
            dispatcher.forward(requestWrapper, response);
        }
    }

    /**
     * @param request request to found servlet context
     * @return server context in servlet context
     */
    protected ServerContext getServerContext(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        ServerContext serverContext = (ServerContext) servletContext.getAttribute(ServerContext.ATTRIBUTE_SERVER_CONTEXT);
        return serverContext;
    }

}

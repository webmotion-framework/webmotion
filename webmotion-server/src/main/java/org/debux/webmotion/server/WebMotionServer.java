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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
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

    /** Filter parameter to configure mapping file name by default is mapping */
    protected final static String PARAM_MAPPING_FILE_NAME = "mapping.file.name";
            
    /** Test if the path contains a extension */
    protected static Pattern patternFile = Pattern.compile("\\.\\w{2,4}$");

    /** Current application context */
    protected ServerContext serverContext;
    
    /** Listeners on server */
    protected List<WebMotionServerListener> listeners;
    
    /** Absolute path on webapp */
    protected String path;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Remove path/. to path/
        path = filterConfig.getServletContext().getRealPath("/");
        serverContext = initServerContext(filterConfig);
        
        // Extract listeners
        listeners = new ArrayList<WebMotionServerListener>();
        Mapping mapping = serverContext.getMapping();
        extractServerListener(mapping);
        
        // Fire onStart
        for (WebMotionServerListener listener : listeners) {
            listener.onStart(serverContext);
        }
    }

    /**
     * Create the server context.
     * @param filterConfig filter config
     * @return server context
     */
    protected ServerContext initServerContext(FilterConfig filterConfig) {
        ServerContext instance = new ServerContext();
        
        // Get file name mapping in context param
        ServletContext servletContext = filterConfig.getServletContext();
        String mappingFileName = servletContext.getInitParameter(PARAM_MAPPING_FILE_NAME);
        if (mappingFileName != null && !mappingFileName.isEmpty()) {
            instance.setMappingFileName(mappingFileName);
        }
        
        instance.contextInitialized(servletContext);
        return instance;
    }

    @Override
    public void destroy() {
        // Fire onStop
        for (WebMotionServerListener listener : listeners) {
            listener.onStop(serverContext);
        }
        
        destroyServerContext();
    }

    /**
     * Detroy the server context.
     */
    protected void destroyServerContext() {
        serverContext.contextDestroyed();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
        
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
        
        if (url.startsWith("/deploy") || url.equals("/")) {
            log.debug("Is deploy");
            doAction(httpServletRequest, httpServletResponse);
            
        } else if (url.startsWith("/static")) {
            log.debug("Is static");
            doResource(httpServletRequest, httpServletResponse);
            
        } else {
            File file = new File(path, url);
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
                    return servletPath.replaceFirst("/static", "");
                }
                return null;
            }
            
            @Override
            public String getPathInfo() {
                String pathInfo = super.getPathInfo();
                if (pathInfo != null) {
                    return pathInfo.replaceFirst("/static", "");
                }
                return null;
            }

            @Override
            public String getRequestURI() {
                String requestURI = super.getRequestURI();
                if (requestURI != null) {
                    return requestURI.replaceFirst("/static", "");
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
     * Search in mapping all server listeners.
     * @param mapping mapping
     */
    public void extractServerListener(Mapping mapping) {
        
        Config config = mapping.getConfig();
        String serverListenerClassName = config.getServerListener();
        if (serverListenerClassName != null && !serverListenerClassName.isEmpty()) {
            
            // Create an instance
            try {
                Class<WebMotionServerListener> serverListenerClass = (Class<WebMotionServerListener>) Class.forName(serverListenerClassName);
                WebMotionServerListener serverListener = serverListenerClass.newInstance();
                listeners.add(serverListener);

            } catch (IllegalAccessException iae) {
                throw new WebMotionException("Error during create server listener " + serverListenerClassName, iae);
            } catch (InstantiationException ie) {
                throw new WebMotionException("Error during create server listener " + serverListenerClassName, ie);
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create server listener " + serverListenerClassName, cnfe);
            }
        }
            
        List<Mapping> extensions = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensions) {
            extractServerListener(extensionMapping);
        }
    }

}

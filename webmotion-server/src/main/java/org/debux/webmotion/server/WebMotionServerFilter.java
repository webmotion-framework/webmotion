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
import java.net.URL;
import java.util.Enumeration;
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
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.InitContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Extension;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.parser.ANTLRMappingParser;
import org.debux.webmotion.server.parser.MappingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main filter manages all call on WebMotion. The servlet invokes two differents 
 * process, the first for classical action management and the other one for static resources.
 * 
 * @author julien
 */
public class WebMotionServerFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(WebMotionServerFilter.class);

    /** Test if the path contains a extension */
    protected static Pattern patternFile = Pattern.compile("\\..{2,4}$");

    /** Attribute name use to store the handlers in ServletContext */
    public static final String HANDLERS_ATTRIBUTE_NAME = "org.debux.webmotion.server.handlers";
    
    protected Mapping mapping;
    protected WebMotionHandler handlersFactory;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Read the mapping in the current project
        InputStream stream = getClass().getResourceAsStream(MappingParser.MAPPING_FILE_NAME);
        MappingParser parser = new ANTLRMappingParser();
        mapping = parser.parse(stream);
        
        // Load mapping file in META-INF
        try {
            List<Mapping> extensionsRules = mapping.getExtensionsRules();
            
            Enumeration<URL> resources = getClass().getClassLoader().getResources("/META-INF/" + MappingParser.MAPPING_FILE_NAME);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                log.info("Loading " + url.toExternalForm());
                InputStream metaStream = url.openStream();
                Mapping metaMapping = parser.parse(metaStream);
                
                Extension extension = new Extension();
                extension.setPath("/");
                metaMapping.setExtension(extension);
                
                extensionsRules.add(metaMapping);
            }
            
        } catch (IOException ioe) {
            throw new WebMotionException("Error during load mapping in META-INF", ioe);
        }
        
        // Create the handler factory
        Config config = mapping.getConfig();
        String handlersFactoryClassName = config.getHandlersFactory();
        
        SingletonFactory<WebMotionHandler> factory = new SingletonFactory<WebMotionHandler>();
        handlersFactory = factory.getInstance(handlersFactoryClassName);
        
        ServletContext servletContext = filterConfig.getServletContext();
        servletContext.setAttribute(HANDLERS_ATTRIBUTE_NAME, factory);
            
        // Init handlers
        InitContext context = new InitContext(servletContext, mapping);
        handlersFactory.init(context);
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
        
        if (url.startsWith("/deploy")) {
            log.info("Is deploy");
            doAction(httpServletRequest, httpServletResponse);
            
        } else if (url.startsWith("/static")) {
            log.info("Is static");
            doResource(httpServletRequest, httpServletResponse);
            
        } else if (patternFile.matcher(url).find()) {
            // css js html png jpg jpeg xml jsp jspx ...
            log.info("Is file");
            chain.doFilter(request, response);
            
        } else {
            log.info("Is default");
            doAction(httpServletRequest, httpServletResponse);
        }
    }
    
    /**
     * Action management in the mapping
     */
    protected void doAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Create call context use in handler to get information on user request
        Call call = new Call(request, response);
        
        // Execute the main handler
        handlersFactory.handle(mapping, call);
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

        // Dispatch on jsp servlet
        ServletContext servletContext = request.getServletContext();
        RequestDispatcher dispatcher = servletContext.getNamedDispatcher("jsp");
        
        DispatcherType dispatcherType = request.getDispatcherType();
        if(dispatcherType == DispatcherType.INCLUDE) {
            dispatcher.include(requestWrapper, response);
        } else {
            dispatcher.forward(requestWrapper, response);
        }
    }
}

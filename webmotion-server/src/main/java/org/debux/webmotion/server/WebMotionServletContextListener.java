/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.debux.webmotion.server.call.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener use to initialized the WebMotion ServerContext.
 * 
 * @author julien
 */
public class WebMotionServletContextListener implements ServletContextListener {
    
    private static final Logger log = LoggerFactory.getLogger(WebMotionServletContextListener.class);

    /** Current application context */
    protected ServerContext serverContext;
        
    /** Filter parameter to configure mapping file name by default is mapping */
    protected final static String PARAM_MAPPING_FILE_NAME = "wm.mapping.file.name";
            
    /** Filter parameter to configure parsers for mapping file name by default is only @DefaultMappingParser */
    protected final static String PARAM_MAPPING_PARSERS = "wm.mapping.parsers";
            
    /** Filter parameter to configure convention scans to create mapping file by default is only @DefaultConventionScan */
    protected final static String PARAM_MAPPING_BY_CONVENTION = "wm.mapping.conventions";
            
    /** Filter parameter to configure excludes path for WebMotion. The value is separated by comma */
    protected final static String PARAM_EXCLUDE_PATHS = "wm.exclude.paths";
            
    @Override
    public void contextInitialized(ServletContextEvent event) {
        serverContext = new ServerContext();
        ServletContext servletContext = event.getServletContext();
        servletContext.setAttribute(ServerContext.ATTRIBUTE_SERVER_CONTEXT, serverContext);
        
        // Get file name mapping in context param
        String mappingFileNameParam = servletContext.getInitParameter(PARAM_MAPPING_FILE_NAME);
        if (mappingFileNameParam != null && !mappingFileNameParam.isEmpty()) {
            serverContext.setMappingFileNames(mappingFileNameParam.split(","));
        }
        
        // Get file name for parsers in context param
        String parsersParam = servletContext.getInitParameter(PARAM_MAPPING_PARSERS);
        if (parsersParam != null && !parsersParam.isEmpty()) {
            serverContext.setMappingParsers(parsersParam.split(","));
        }
        
        // Get exclude path in context param
        String excludePathsParam = servletContext.getInitParameter(PARAM_EXCLUDE_PATHS);
        if (excludePathsParam != null && !excludePathsParam.isEmpty()) {
            serverContext.setExcludePaths(excludePathsParam.split(","));
        }
        
        // Get file name for convention scans in context param
        String conventionsParam = servletContext.getInitParameter(PARAM_MAPPING_BY_CONVENTION);
        if (conventionsParam != null && !conventionsParam.isEmpty()) {
            serverContext.setMappingConventions(conventionsParam.split(","));
        }
        
        createWebSockets(servletContext);
        serverContext.contextInitialized(servletContext);
    }

    /**
     * Create websockets
     */
    public void createWebSockets(ServletContext servletContext) {
        try {
            String wrapperClassName = null;
            if (HttpUtils.isTomcatContainer(servletContext)) {
                wrapperClassName = "org.debux.webmotion.server.websocket.wrapper.WebSocketTomcatWrapper";

            } else if (HttpUtils.isGlassfishContainer(servletContext)) {
                wrapperClassName = "org.debux.webmotion.server.websocket.wrapper.WebSocketGlassfishWrapper";

            } else if (HttpUtils.isJettyContainer(servletContext)) {
                wrapperClassName = "org.debux.webmotion.server.websocket.wrapper.WebSocketJettyWrapper";

            } else {
                log.warn("The websockets are not supported");
                return;
            }

            Class<Servlet> wrapperClass = (Class<Servlet>) Class.forName(wrapperClassName);
            Servlet wrapper = wrapperClass.newInstance();
            
            servletContext.addServlet(WebMotionServer.SERVLET_WEBSOCKET, wrapper);
            
        } catch (IllegalArgumentException ex) {
            log.warn("The websockets are not supported", ex);
        } catch (SecurityException ex) {
            log.warn("The websockets are not supported", ex);
        } catch (InstantiationException ex) {
            log.warn("The websockets are not supported", ex);
        } catch (IllegalAccessException ex) {
            log.warn("The websockets are not supported", ex);
        } catch (ClassNotFoundException ex) {
            log.warn("The websockets are not supported", ex);
        } catch (NoClassDefFoundError ex) {
            log.warn("The websockets are not supported", ex);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        serverContext.contextDestroyed();
    }
    
}

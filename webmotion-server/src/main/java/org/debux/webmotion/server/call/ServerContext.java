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
package org.debux.webmotion.server.call;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.mbean.ServerContextManager;
import org.debux.webmotion.server.mbean.ServerStats;
import org.debux.webmotion.server.parser.ANTLRMappingParser;
import org.debux.webmotion.server.parser.MappingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebMotionServerContext contains all global informations like factories, mbeans, ...
 * The user can store the own attributes. The server context is an attributes of 
 * ServletContext.
 * 
 * @author julien
 */
public class ServerContext {
    
    private static final Logger log = LoggerFactory.getLogger(ServerContext.class);

    /** Factory of controllers*/
    protected SingletonFactory<WebMotionController> controllers;
    
    /** Factory of handelrs */
    protected SingletonFactory<WebMotionHandler> handlers;
    
    /** MBean for server stats */
    protected ServerStats serverStats;
    
    /** MBean for handler stats */
    protected HandlerStats handlerStats;
            
    /** MBean for manage server */
    protected ServerContextManager serverManager;
            
    /** Current mapping */
    protected Mapping mapping;
    
    /** The main handler is call the first */
    protected WebMotionHandler mainHandler;
    
    /** User attributes */
    protected Map<String, Object> attributes;
    
    /** Current servlet context */
    protected ServletContext servletContext;
    
    /**
     * Initialize the context.
     * 
     * @param servletContext servlet context
     */
    public void contextInitialized(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.attributes = new HashMap<String, Object>();
        this.handlers = new SingletonFactory<WebMotionHandler>();
        this.controllers = new SingletonFactory<WebMotionController>();

        // Register MBeans
        this.serverStats = new ServerStats();
        this.handlerStats = new HandlerStats();
        this.serverManager = new ServerContextManager(this);
        
        this.serverStats.register();
        this.handlerStats.register();
        this.serverManager.register();
        
        // Read the mapping
        this.loadMapping();
    }

    /**
     * Destroy the context.
     */
    public void contextDestroyed() {
        serverStats.unregister();
        handlerStats.unregister();
        serverManager.unregister();
    }

    /**
     * Load the mapping
     */
    public void loadMapping() {
        // Read the mapping in the current project
        MappingParser parser = getMappingParser();
        mapping = parser.parse();

        // Create the handler factory
        Config config = mapping.getConfig();
        String className = config.getMainHandler();
        
        mainHandler = handlers.getInstance(className);
        
        // Init handlers
        mainHandler.init(mapping, this);
    }

    /**
     * @return the instance of mapping parser
     */
    protected ANTLRMappingParser getMappingParser() {
        return new ANTLRMappingParser();
    }
        
    public SingletonFactory<WebMotionController> getControllers() {
        return controllers;
    }

    public SingletonFactory<WebMotionHandler> getHandlers() {
        return handlers;
    }

    public HandlerStats getHandlerStats() {
        return handlerStats;
    }

    public ServerStats getServerStats() {
        return serverStats;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public WebMotionHandler getMainHandler() {
        return mainHandler;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}

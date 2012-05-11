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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler.Injector;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.mbean.ServerContextManager;
import org.debux.webmotion.server.mbean.ServerStats;
import org.debux.webmotion.server.parser.MappingParser;
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
    
    /** Contain contoller which are accessible in all mapping */
    protected Map<String, Class<? extends WebMotionController>> globalControllers;
    
    /** Contain injector use in ExecutorParametersInjectorHandler */
    protected List<Injector> injectors;
            
    /** Bean utils use in handler */
    protected BeanUtilsBean beanUtil;
    
    /** Convert utils use in handler */
    protected ConvertUtilsBean converter;
    
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
    
    /** Secret key use in encryt cookie value */
    protected String secret;
    
    /** Main mapping file name to parse */
    protected String mappingFileName = "/mapping";
    
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
        this.globalControllers = new HashMap<String, Class<? extends WebMotionController>>();
        this.injectors = new ArrayList<Injector>();
        
        this.beanUtil = BeanUtilsBean.getInstance();
        this.converter = beanUtil.getConvertUtils();
                
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
        mapping = parser.parse(mappingFileName);

        // Create the handler factory
        Config config = mapping.getConfig();
        String className = config.getMainHandler();
        
        mainHandler = handlers.getInstance(className);
        
        // Read secret otherwise generate it
        secret = config.getSecret();
        if (secret == null || secret.length() < Config.SERVER_SECRET_MIN_SIZE) {
            secret = WebMotionUtils.generateSecret();
        }
        
        // Init handlers
        mainHandler.init(mapping, this);
    }

    /**
     * @return the instance of mapping parser
     */
    protected MappingParser getMappingParser() {
        return new MappingParser();
    }
        
    public SingletonFactory<WebMotionController> getControllers() {
        return controllers;
    }

    public SingletonFactory<WebMotionHandler> getHandlers() {
        return handlers;
    }

    public Map<String, Class<? extends WebMotionController>> getGlobalControllers() {
        return globalControllers;
    }

    public void setGlobalControllers(Map<String, Class<? extends WebMotionController>> globalControllers) {
        this.globalControllers = globalControllers;
    }
    
    public void addGlobalController(Class<? extends WebMotionController> clazz) {
        globalControllers.put(clazz.getSimpleName(), clazz);
    }
    
    public List<Injector> getInjectors() {
        return injectors;
    }

    public void setInjectors(List<Injector> injectors) {
        this.injectors = injectors;
    }
    
    public void addInjector(Injector injector) {
        injectors.add(injector);
    }

    public BeanUtilsBean getBeanUtil() {
        return beanUtil;
    }

    public void setBeanUtil(BeanUtilsBean beanUtil) {
        this.beanUtil = beanUtil;
    }

    public ConvertUtilsBean getConverter() {
        return converter;
    }

    public void setConverter(ConvertUtilsBean converter) {
        this.converter = converter;
    }

    public void addConverter(Converter converter, Class clazz) {
        this.converter.register(converter, clazz);
    }

    public ServerContextManager getServerManager() {
        return serverManager;
    }

    public void setServerManager(ServerContextManager serverManager) {
        this.serverManager = serverManager;
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getMappingFileName() {
        return mappingFileName;
    }

    public void setMappingFileName(String mappingFileName) {
        this.mappingFileName = mappingFileName;
    }
    
}

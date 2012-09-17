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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.*;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler.Injector;
import org.debux.webmotion.server.mapping.*;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.mbean.ServerContextManager;
import org.debux.webmotion.server.mbean.ServerStats;
import org.debux.webmotion.server.parser.MappingChecker;
import org.debux.webmotion.server.parser.MappingParser;
import org.debux.webmotion.server.websocket.WebSocketFactory;
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

    /* ServerContext name to store attributes */
    public static final String ATTRIBUTES_SERVER_CONTEXT = "wm.server.context";
            
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
    
    /** Absolute path on webapp */
    protected String webappPath;
    
    /** Listeners on server */
    protected List<WebMotionServerListener> listeners;
    
    /** Current exclude paths */
    protected String[] excludePaths;
    
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
        
        this.webappPath = servletContext.getRealPath("/");
                
        // Read the mapping in the current project
        MappingParser parser = getMappingParser();
        mapping = parser.parse(mappingFileName);
        
        // Fire onStart
        listeners = new ArrayList<WebMotionServerListener>();
        onStartServerListener(mapping);
        
        // Load mapping
        loadMapping();

        // Check mapping
        checkMapping();
        
        // Create websockets
        createWebSockets(mapping, "");
        
        log.info("WebMotion is started");
    }

    /**
     * Destroy the context.
     */
    public void contextDestroyed() {
        // Fire onStop
        for (WebMotionServerListener listener : listeners) {
            listener.onStop(this);
        }
        
        serverStats.unregister();
        handlerStats.unregister();
        serverManager.unregister();
    }

    /**
     * Load the mapping
     */
    public void loadMapping() {
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
     * Search in mapping all server listeners and fire onStart
     * @param mapping mapping
     */
    protected void onStartServerListener(Mapping mapping) {
        Config config = mapping.getConfig();
        String serverListenerClassNames = config.getServerListener();
        if (serverListenerClassNames != null && !serverListenerClassNames.isEmpty()) {
            
            // Split name by comma
            String[] serverListenerClassName = serverListenerClassNames.split(",");
            for (String className : serverListenerClassName) {
                
                // Create an instance
                try {
                    Class<WebMotionServerListener> serverListenerClass = (Class<WebMotionServerListener>) Class.forName(className);
                    WebMotionServerListener serverListener = serverListenerClass.newInstance();
                    
                    serverListener.onStart(mapping, this);

                    listeners.add(serverListener);
                    
                } catch (IllegalAccessException iae) {
                    throw new WebMotionException("Error during create server listener " + className, iae);
                } catch (InstantiationException ie) {
                    throw new WebMotionException("Error during create server listener " + className, ie);
                } catch (ClassNotFoundException cnfe) {
                    throw new WebMotionException("Error during create server listener " + className, cnfe);
                }
            }
        }
            
        List<Mapping> extensions = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensions) {
            onStartServerListener(extensionMapping);
        }
    }
    
    /**
     * Check the mapping and extensions
     */
    public void checkMapping() {
        MappingChecker mappingChecker = new MappingChecker();
        mappingChecker.checkMapping(this, mapping);
        mappingChecker.print();
    }

    /**
     * Create websockets
     */
    public void createWebSockets(Mapping mapping, String mappingPath) {
        List<WebSocketRule> webSocketRules = mapping.getWebSocketRules();
        for (WebSocketRule webSocketRule : webSocketRules) {
            try {
                String path = mappingPath;
                if (mappingPath.endsWith("/")) {
                    path = StringUtils.substringBeforeLast(path, "/");
                }
                path += webSocketRule.getPath();
                
                String className = webSocketRule.getAction().getFullName();
                
                Config config = mapping.getConfig();
                String packageName = config.getPackageActions();
                String fullQualifiedName = null;
                if (packageName == null || packageName.isEmpty()) {
                    fullQualifiedName = className;
                } else {
                    fullQualifiedName = packageName + "." + className;
                }
                
                Class<WebSocketFactory> factoryClass = (Class<WebSocketFactory>) Class.forName(fullQualifiedName);
                WebSocketFactory factory = factoryClass.newInstance();
                
                String wrapperClassName = null;
                if (WebMotionUtils.isTomcatContainer(servletContext)) {
                    wrapperClassName = "org.debux.webmotion.server.websocket.wrapper.WebSocketTomcatWrapper";
                    
                } else if (WebMotionUtils.isGlassfishContainer(servletContext)) {
                    wrapperClassName = "org.debux.webmotion.server.websocket.wrapper.WebSocketGlassfishWrapper";
                    
                } else if (WebMotionUtils.isJettyContainer(servletContext)) {
                    wrapperClassName = "org.debux.webmotion.server.websocket.wrapper.WebSocketJettyWrapper";
                    
                } else {
                    throw new WebMotionException("Unsupported WebSocket on application server");
                }
                
                Class<Servlet> wrapperClass = (Class<Servlet>) Class.forName(wrapperClassName);
                Constructor<Servlet> constructor = wrapperClass.getConstructor(WebSocketFactory.class);
                Servlet wrapper = constructor.newInstance(factory);
                
                Dynamic servlet = servletContext.addServlet(className, wrapper);
                servlet.addMapping(path);
                
                excludePaths = ArrayUtils.add(excludePaths, path);
                
            } catch (IllegalArgumentException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            } catch (InvocationTargetException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            } catch (NoSuchMethodException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            } catch (SecurityException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            } catch (InstantiationException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            } catch (IllegalAccessException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            } catch (ClassNotFoundException ex) {
                throw new WebMotionException("Error during create the websocket", ex);
            }
        }
        
        List<Mapping> extensions = mapping.getExtensionsRules();
        for (Mapping extensionMapping : extensions) {
            String extensionPath = extensionMapping.getExtensionPath();
            createWebSockets(extensionMapping, extensionPath);
        }
    }
    
    /**
     * @return the instance of mapping parser
     */
    protected MappingParser getMappingParser() {
        return new MappingParser();
    }
        
    /**
     * @return factory contains all controllers
     */
    public SingletonFactory<WebMotionController> getControllers() {
        return controllers;
    }

    /**
     * @return factory contains all handlers
     */
    public SingletonFactory<WebMotionHandler> getHandlers() {
        return handlers;
    }

    /**
     * @return global controllers register
     */
    public Map<String, Class<? extends WebMotionController>> getGlobalControllers() {
        return globalControllers;
    }

    /**
     * Set all global controllers.
     * 
     * @param globalControllers 
     */
    public void setGlobalControllers(Map<String, Class<? extends WebMotionController>> globalControllers) {
        this.globalControllers = globalControllers;
    }
    
    /**
     * Add a global controller use the simple name as class name use in mapping.
     * 
     * @param clazz 
     */
    public void addGlobalController(Class<? extends WebMotionController> clazz) {
        globalControllers.put(clazz.getSimpleName(), clazz);
    }
    
    /**
     * @return all injectors register
     */
    public List<Injector> getInjectors() {
        return injectors;
    }

    /**
     * Set all injectors.
     * 
     * @param injectors 
     */
    public void setInjectors(List<Injector> injectors) {
        this.injectors = injectors;
    }
    
    /**
     * Add a injector.
     * 
     * @param injector 
     */
    public void addInjector(Injector injector) {
        injectors.add(injector);
    }

    /**
     * @return bean utils
     */
    public BeanUtilsBean getBeanUtil() {
        return beanUtil;
    }

    /**
     * Set bean utils use for convertion.
     * 
     * @param beanUtil 
     */
    public void setBeanUtil(BeanUtilsBean beanUtil) {
        this.beanUtil = beanUtil;
    }

    /**
     * @return converter utils
     */
    public ConvertUtilsBean getConverter() {
        return converter;
    }

    /**
     * Set converter utils.
     * 
     * @param converter 
     */
    public void setConverter(ConvertUtilsBean converter) {
        this.converter = converter;
    }

    /**
     * Add a converter.
     * 
     * @param converter
     * @param clazz 
     */
    public void addConverter(Converter converter, Class clazz) {
        this.converter.register(converter, clazz);
    }

    /**
     * @return manager for JMX
     */
    public ServerContextManager getServerManager() {
        return serverManager;
    }

    /**
     * Set manager for jmx
     * @param serverManager 
     */
    public void setServerManager(ServerContextManager serverManager) {
        this.serverManager = serverManager;
    }
    
    /**
     * @return Manager server stats
     */
    public HandlerStats getHandlerStats() {
        return handlerStats;
    }

    /**
     * @return Mbean on server stats
     */
    public ServerStats getServerStats() {
        return serverStats;
    }

    /**
     * @return attributes store in server context
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    /**
     * Add an attribut in server context.
     * 
     * @param name
     * @param value 
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    /**
     * Get attribute by name.
     * 
     * @param name attribute name
     * @return attribute value
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * @return the main handler instance configure in the mapping
     */
    public WebMotionHandler getMainHandler() {
        return mainHandler;
    }

    /**
     * @return the root mapping
     */
    public Mapping getMapping() {
        return mapping;
    }

    /**
     * @return servlet context
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @return secret use for security
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Set the secret manually.
     * 
     * @param secret 
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * @return the mapping file name use to read the mapping/
     */
    public String getMappingFileName() {
        return mappingFileName;
    }

    /**
     * Set the mapping file name use to read the mapping.
     * 
     * @param mappingFileName 
     */
    public void setMappingFileName(String mappingFileName) {
        this.mappingFileName = mappingFileName;
    }

    /**
     * @return absolute path on webapp
     */
    public String getWebappPath() {
        return webappPath;
    }

    /**
     * @return current path exclude in filter
     */
    public String[] getExcludePaths() {
        return excludePaths;
    }

    /**
     * Set exclude path in filter
     * @param excludePaths 
     */
    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = excludePaths;
    }

}

/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.convention.ConventionScan;
import org.debux.webmotion.server.convention.DefaultConventionScan;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler.Injector;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.mbean.ServerContextManager;
import org.debux.webmotion.server.mbean.ServerStats;
import org.debux.webmotion.server.parser.DefaultMappingParser;
import org.debux.webmotion.server.parser.MappingChecker;
import org.debux.webmotion.server.parser.MappingParser;
import org.debux.webmotion.server.tools.HttpUtils;
import org.debux.webmotion.server.tools.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String ATTRIBUTE_SERVER_CONTEXT = "wm.server.context";
            
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
    protected String[] mappingFileNames = {"/mapping"};
    
    /** Parsers for mapping file */
    protected String[] mappingParsers = {DefaultMappingParser.class.getName()};
    
    /** Parsers for mapping file */
    protected String[] mappingConventions = {DefaultConventionScan.class.getName()};
    
    /** Absolute path on webapp */
    protected String webappPath;
    
    /** Listeners on server */
    protected List<WebMotionServerListener> listeners;
    
    /** Current exclude paths */
    protected String[] excludePaths = {};
    
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
        MappingParser[] parsers = getMappingParsers();
        for (MappingParser parser : parsers) {
            mapping = parser.parse(mappingFileNames);
            if (mapping != null) {
                break;
            }
        }

        String skipConvensionScan = servletContext.getInitParameter("wm.skip.conventionScan");
        if (!"true".equals(skipConvensionScan)) {

            // Scan to generate mapping by convention
            ConventionScan[] conventions = getMappingConventions();
            for (ConventionScan conventionScan : conventions) {
                Mapping convention = conventionScan.scan();

                if (!convention.getActionRules().isEmpty() || !convention.getFilterRules().isEmpty()) {

                    if (mapping == null) {
                        mapping = convention;
                    } else {
                        mapping.getExtensionsRules().add(convention);
                    }
                }
            }
        }
        
        if (mapping == null) {
            throw new WebMotionException("No mapping found for " + Arrays.toString(mappingFileNames) 
                    + " in " + Arrays.toString(mappingParsers));
        }
        
        // Fire onStart
        listeners = new ArrayList<WebMotionServerListener>();
        onStartServerListener(mapping);
        
        // Load mapping
        loadMapping();

        // Check mapping
        checkMapping();
        
        log.info("WebMotion is started");
    }

    /**
     * Destroy the context.
     */
    public void contextDestroyed() {
        mainHandler.handlerDestroyed(mapping, this);
    
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
        if (secret == null) {
            secret = HttpUtils.generateSecret();
        } else if (secret.length() < Config.SERVER_SECRET_MIN_SIZE) {
            log.warn("The secret key is too short, it is generated");
            secret = HttpUtils.generateSecret();
        }
        
        // Init handlers
        mainHandler.handlerCreated(mapping, this);
        mainHandler.handlerInitialized(mapping, this);
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
     * @return the instance of mapping parsers
     */
    protected MappingParser[] getMappingParsers() {
        MappingParser[] parsers = new MappingParser[mappingParsers.length];
        
        int index = 0;
        for (String className : mappingParsers) {
            try {
                Class<MappingParser> parserClasse = (Class<MappingParser>) Class.forName(className);
                MappingParser parser = parserClasse.newInstance();
                parsers[index ++] = parser;

            } catch (IllegalAccessException iae) {
                throw new WebMotionException("Error during create server listener " + className, iae);
            } catch (InstantiationException ie) {
                throw new WebMotionException("Error during create server listener " + className, ie);
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create server listener " + className, cnfe);
            }
        }
        return parsers;
    }
    
    /**
     * @return the instance of convention scans
     */
    protected ConventionScan[] getMappingConventions() {
        ConventionScan[] scans = new ConventionScan[mappingConventions.length];
        
        int index = 0;
        for (String className : mappingConventions) {
            try {
                Class<ConventionScan> parserClasse = (Class<ConventionScan>) Class.forName(className);
                ConventionScan parser = parserClasse.newInstance();
                scans[index ++] = parser;

            } catch (IllegalAccessException iae) {
                throw new WebMotionException("Error during create server listener " + className, iae);
            } catch (InstantiationException ie) {
                throw new WebMotionException("Error during create server listener " + className, ie);
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create server listener " + className, cnfe);
            }
        }
        return scans;
    }

    /**
     * Set the parsers for the mapping file.
     * @param parsers classe names
     */
    public void setMappingParsers(String[] parsers) {
        this.mappingParsers = parsers;
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
     * @return the mapping file name use to read the mapping.
     */
    public String[] getMappingFileNames() {
        return mappingFileNames;
    }

    /**
     * Set the mapping file name use to read the mapping.
     * 
     * @param mappingFileNames 
     */
    public void setMappingFileNames(String[] mappingFileNames) {
        this.mappingFileNames = mappingFileNames;
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

    /**
     * Set the convention scan file names use to generate the mapping.
     * 
     * @param mappingConventions 
     */
    public void setMappingConventions(String[] mappingConventions) {
        this.mappingConventions = mappingConventions;
    }

    
    
}

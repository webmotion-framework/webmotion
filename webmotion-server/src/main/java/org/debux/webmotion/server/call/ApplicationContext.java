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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.mbean.Stats;

/**
 * 
 * @author julien
 */
public class ApplicationContext implements ServletContextListener {
    
    public static final String ATTRIBUTE_APPLICATION_CONTEXT = "org.debux.webmotion.server.call.ApplicationContext.APPLICATION_CONTEXT";
    
    protected SingletonFactory<WebMotionController> controllers;
    
    protected Mapping mapping;
    protected WebMotionHandler handlersFactory;
    protected SingletonFactory<WebMotionHandler> handlers;

    protected Stats stats;
    protected HandlerStats handlerStats;
            
    @Override
    public void contextInitialized(ServletContextEvent event) {
        stats = new Stats();
        stats.register();
        
        handlerStats = new HandlerStats();
        handlerStats.register();
        
        ServletContext servletContext = event.getServletContext();
        servletContext.setAttribute(ATTRIBUTE_APPLICATION_CONTEXT, this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        stats.unregister();
        handlerStats.unregister();
    }

    public static ApplicationContext getApplicationContext(ServletContext servletContext) {
        return (ApplicationContext) servletContext.getAttribute(ATTRIBUTE_APPLICATION_CONTEXT);
    }
    
    public SingletonFactory<WebMotionController> getControllers() {
        return controllers;
    }

    public void setControllers(SingletonFactory<WebMotionController> controllers) {
        this.controllers = controllers;
    }

    public SingletonFactory<WebMotionHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(SingletonFactory<WebMotionHandler> handlers) {
        this.handlers = handlers;
    }

    public WebMotionHandler getHandlersFactory() {
        return handlersFactory;
    }

    public void setHandlersFactory(WebMotionHandler handlersFactory) {
        this.handlersFactory = handlersFactory;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public HandlerStats getHandlerStats() {
        return handlerStats;
    }

    public void setHandlerStats(HandlerStats handlerStats) {
        this.handlerStats = handlerStats;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
    
}

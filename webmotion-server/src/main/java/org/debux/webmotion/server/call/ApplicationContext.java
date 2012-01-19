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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.mbean.HandlerStats;
import org.debux.webmotion.server.mbean.Stats;

/**
 * 
 * @author julien
 */
public class ApplicationContext implements ServletContextListener {
    
    public static final String ATTRIBUTE_APPLICATION_CONTEXT = "org.debux.webmotion.server.call.ApplicationContext.APPLICATION_CONTEXT";
    
    protected SingletonFactory<WebMotionController> controllers;
    protected SingletonFactory<WebMotionHandler> handlers;
    
    protected Stats stats;
    protected HandlerStats handlerStats;
            
    protected Map<String, Object> attributes;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        attributes = new HashMap<String, Object>();
        handlers = new SingletonFactory<WebMotionHandler>();
        controllers = new SingletonFactory<WebMotionController>();
        
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

    public Map<String, ?> getAttributes() {
        return attributes;
    }
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
}

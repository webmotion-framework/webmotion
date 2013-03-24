/*
 * #%L
 * WebMotion unit test
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Debux
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
package org.debux.webmotion.unittest;

import java.util.concurrent.atomic.AtomicBoolean;
import static org.debux.webmotion.unittest.WebMotionJUnit.isStarted;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Abstract class uses to start/stop Jetty serveur.
 * 
 * @author julien
 */
public abstract class WebMotionTest {

    static AtomicBoolean isStarted = new AtomicBoolean(false);
    
    protected Server server;

    /**
     * Start Jetty serveur.
     * 
     * @throws Exception 
     */
    protected void startServer() throws Exception {
        server = new Server();
        
        int port = getPort();
        String contextPath = getContextPath();
        String war = getWar();

        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(contextPath);
        webAppContext.setWar(war);
        server.setHandler(webAppContext);

        server.start();
    }

    /**
     * Stop Jetty server.
     * 
     * @throws Exception 
     */
    protected void stopServer() throws Exception {
        server.stop();
    }
    
    /**
     * Start the serveur and stop this when the thread is shutdown.
     * 
     * @throws Exception 
     */
    public void runServer() throws Exception {
        boolean value = isStarted.getAndSet(true);
        if (!value) {
            startServer();
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        stopServer();
                        
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                        
                    } finally {
                        isStarted.set(false);
                    }
                }
            });
        }
    }
    
    /**
     * @return port
     */
    protected int getPort() {
        return 9999;
    }
    
    /**
     * @return context path
     */
    protected String getContextPath() {
        return "/";
    }
    
    /**
     * @return resources war location
     */
    protected String getWar() {
        return "src/main/webapp";
    }
    
    /**
     * Create a request for fluent API from httpcomponent.
     * @param url
     * @return 
     */
    public RequestBuilder createRequest(String url) {
        String contextPath = getContextPath();
        
        String path;
        if (contextPath.endsWith("/") && url.startsWith("/")) {
            path = getContextPath() + url.substring(1);
            
        } else if (contextPath.endsWith("/") ^ url.startsWith("/")) {
            path = getContextPath() + url;
            
        } else {
            path = getContextPath() + "/" + url;
        }
        
        RequestBuilder builder = (RequestBuilder) new RequestBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(getPort())
                .setPath(path);
        return builder;
    }
    
}

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

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.scan.StandardJarScanner;
import static org.debux.webmotion.unittest.WebMotionJUnit.isStarted;

/**
 * Abstract class uses to start/stop Tomcat server.
 * 
 * @author julien
 */
public abstract class WebMotionTest {

    static AtomicBoolean isStarted = new AtomicBoolean(false);
    
    protected Tomcat server;

    /**
     * Start Tomcat server.
     * 
     * @throws Exception 
     */
    protected void startServer() throws Exception {
        
        // create server
        server = new Tomcat();
        server.setPort(getPort());
        
        // Create webapp loader with jar in classpath as repository
        WebappLoader loader = new WebappLoader(this.getClass().getClassLoader());
        String classpaths = System.getProperty("java.class.path");
        String[] classpath = classpaths.split(":");
        for (String path : classpath) {
            loader.addRepository(new File(path).toURI().toURL().toString());
        }
        
        // Create a new webbapp
        StandardContext rootContext = (StandardContext) server.addWebapp(getContextPath(), new File(getWebappLocation()).getAbsolutePath());
        rootContext.setLoader(loader);
        rootContext.setReloadable(true);
        rootContext.setUnpackWAR(false);
        ((StandardJarScanner) rootContext.getJarScanner()).setScanAllDirectories(true);

        // Enabled JNDI
        server.enableNaming();
        
        // Start server for the theard
        server.start();
    }
    
    /**
     * Stop Tomcat server.
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
     * @return webapp location
     */
    protected String getWebappLocation() {
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

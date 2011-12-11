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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.InitContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Extension;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.parser.ANTLRMappingParser;
import org.debux.webmotion.server.parser.MappingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main servlet manages all call on WebMotion. The servlet invokes two differents 
 * handlers, the first for classical action management and the other one for error management. The 
 * handlers chain others handlers to answer the user query.
 * 
 * @author jruchaud
 */
public class WebMotionMainServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(WebMotionMainServlet.class);

    /** Attribute name use to store the handlers in ServletContext */
    public static final String HANDLERS_ATTRIBUTE_NAME = "org.debux.webmotion.server.handlers";

    protected Mapping mapping;
    protected WebMotionHandler handlersFactory;
    protected WebMotionContextable contextable;
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

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
        
        ServletContext servletContext = servletConfig.getServletContext();
        servletContext.setAttribute(HANDLERS_ATTRIBUTE_NAME, factory);
            
        // Init handlers
        InitContext context = new InitContext(servletContext, mapping);
        handlersFactory.init(context);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("Pass in WebMotionServer");
        
        // Create call context use in handler to get information on user request
        Call call = new Call(request, response);
        
        // Execute the main handler
        handlersFactory.handle(mapping, call);
    }
    
}

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
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main servlet manages all call on WebMotion. The servlet invokes two differents 
 * handlers, the first for classical action management and the other one for error management. The 
 * handlers chain others handlers to answer the user query.
 * 
 * @author jruchaud
 */
@WebServlet(
    name ="WebMotionServer",
    urlPatterns = {
        "/deploy/*"
    }
)
public class WebMotionServer extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(WebMotionServer.class);

    protected Mapping mapping;
    protected WebMotionHandler actionManager;
    protected WebMotionHandler errorManager;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        actionManager = new WebMotionActionManager();
        errorManager = new WebMotionErrorManager();
        
        readMapping();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Apply config
        Config config = mapping.getConfig();
        String requestEncoding = config.getRequestEncoding();
        request.setCharacterEncoding(requestEncoding);
        
        // Determine if the request contains an errors
        Call call = new Call(request, response);
        ErrorData errorData = call.getErrorData();
        if(errorData.isError()) {
            errorManager.handle(mapping, call);
        } else {
            actionManager.handle(mapping, call);
        }
    }
    
    /**
     * The mapping is read during initialisation.
     * 
     * @throws ServletException 
     */
    protected void readMapping() throws ServletException {
        mapping = new Mapping();
        Config config = mapping.getConfig();
        
        try {
            InputStream mappings = getClass().getResourceAsStream("/mapping");
            List<String> rules = IOUtils.readLines(mappings);

            int section = 0;
            for (String rule : rules) {
                
                rule = rule.trim();
                rule = rule.replaceAll(" +", " ");

                if(rule.startsWith("#") || rule.isEmpty()) {
                    // Comments
                } else if(rule.startsWith("[errors")) {
                    section = 1;
                } else if(rule.startsWith("[filters")) {
                    section = 2;
                } else if(rule.startsWith("[actions")) {
                    section = 3;
                } else if(rule.startsWith("[config")) {
                    section = 4;

                } else if(section == 1) {
                    // Errors section
                    mapping.extractSectionErrors(rule);

                } else if(section == 2) {
                    // Filters section
                    mapping.extractSectionFilters(rule);

                } else if(section == 3) {
                    // Actions section
                    mapping.extractSectionActions(rule);

                } else if(section == 4 && rule.startsWith(Config.PACKAGE_VIEWS)) {
                    String value = config.extractConfig(Config.PACKAGE_VIEWS, rule);
                    config.setPackageViews(value);

                } else if(section == 4 && rule.startsWith(Config.PACKAGE_ACTIONS)) {
                    String value = config.extractConfig(Config.PACKAGE_ACTIONS, rule);
                    config.setPackageActions(value);
                    
                } else if(section == 4 && rule.startsWith(Config.PACKAGE_FILTERS)) {
                    String value = config.extractConfig(Config.PACKAGE_FILTERS, rule);
                    config.setPackageFilters(value);
                    
                } else if(section == 4 && rule.startsWith(Config.PACKAGE_ERRORS)) {
                    String value = config.extractConfig(Config.PACKAGE_ERRORS, rule);
                    config.setPackageErrors(value);
                    
                } else if(section == 4 && rule.startsWith(Config.REQUEST_ENCODING)) {
                    String value = config.extractConfig(Config.REQUEST_ENCODING, rule);
                    config.setRequestEncoding(value);
                    
                } else if(section == 4 && rule.startsWith(Config.RELOADABLE)) {
                    String value = config.extractConfig(Config.RELOADABLE, rule);
                    config.setReloadable(Boolean.valueOf(value));
                    
                } else if(section == 4 && rule.startsWith(Config.MODE)) {
                    String value = config.extractConfig(Config.MODE, rule);
                    config.setMode(value);
                }
            }
        } catch(IOException ioe) {
            throw new ServletException(ioe);
        }
    }
    
}

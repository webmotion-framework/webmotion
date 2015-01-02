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
package org.debux.webmotion.server.render;

import java.io.IOException;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The render represents result for user request. It contains the necessary 
 * elements to create the http response. In the {@see WebMotionAction} class, there are
 * utility methods to instantiated correctly a render. The render can be a 
 * view (template), an URL (referer), an action, a model (json, jsonp, xml) or 
 * directly a content with specified mime-type.
 * 
 * @author julien
 */
public abstract class Render {

    private static final Logger log = LoggerFactory.getLogger(Render.class);

    /** Default encoding uses to write the reponse */
    public static String DEFAULT_ENCODING = "UTF-8";
    
    /** Default model name uses when neither name is given */
    public static String DEFAULT_MODEL_NAME = "model";
    
    /**
     * Execute the render and store a state as executed.
     */
    public void exec(Mapping mapping, Call call) throws IOException, ServletException {
        create(mapping, call);
        complete(mapping, call);
    }
    
    /**
     * Call after the render is create. If the request is async, call complete method
     * on AsyncContext.
     */
    public void complete(Mapping mapping, Call call) throws IOException, ServletException {
        if (call.isAsync()) {
            HttpContext context = call.getContext();
            HttpServletRequest request = context.getRequest();
            AsyncContext asyncContext = request.getAsyncContext();
            asyncContext.complete();
        }
    }
    
    /**
     * Use to do the render.
     */
    public abstract void create(Mapping mapping, Call call) throws IOException, ServletException;

    /**
     * Get the url to the view.
     * @param mapping
     * @param view
     * @return 
     */
    protected String getViewPath(Mapping mapping, Call call, String view) {
        String path = "/" + view;
        
        Config config = mapping.getConfig();
        String packageName = config.getPackageViews().replaceAll("\\.", "/");
        if(packageName != null && !packageName.isEmpty()) {
            path = "/" + packageName + path;
        }
        
        log.debug("path = " + path);
        return path;
    }

    /**
     * Add model in attribute
     * @param call
     * @param model
     */
    protected void addModel(Call call, Map<String, Object> model) {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        
        if(model != null) {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                request.setAttribute(key, value);
            }
        }
    }

    /**
     * Add model in parameter
     * @param url
     * @param model
     * @return 
     */
    protected String addModel(String url, Map<String, Object> model) {
        String path = url;
        if(model != null) {

            String separator = "?";
            if(path.contains("?")) {
                separator = "&";
            }
            
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                
                Object value = entry.getValue();
                if (value != null) {
                    
                    String key = entry.getKey();
                    path += separator + key + "=" + value;
                    separator = "&";
                }
            }
        }
        return path;
    }
    
}

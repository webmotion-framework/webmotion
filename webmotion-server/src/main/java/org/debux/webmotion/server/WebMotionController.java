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

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderAction;
import org.debux.webmotion.server.render.RenderContent;
import org.debux.webmotion.server.render.RenderError;
import org.debux.webmotion.server.render.RenderJson;
import org.debux.webmotion.server.render.RenderJsonP;
import org.debux.webmotion.server.render.RenderReferer;
import org.debux.webmotion.server.render.RenderStatus;
import org.debux.webmotion.server.render.RenderStream;
import org.debux.webmotion.server.render.RenderTemplate;
import org.debux.webmotion.server.render.RenderUrl;
import org.debux.webmotion.server.render.RenderView;
import org.debux.webmotion.server.render.RenderXml;

/**
 * The classes represents an action to execute following a user request. The action 
 * can be a {@see WebMotionFilter}. It contains utility for render a view. 
 * Each method must return a {@see Render}, it is used to display or 
 * execute the next action.
 * 
 * @author jruchaud
 */
public class WebMotionController {

    protected WebMotionContextable contextable;
            
    /**
     * Default construtor, used to call direct new instance.
     */
    public WebMotionController() {
    }

    /**
     * Set the context
     */
    public void setContextable(WebMotionContextable contextable) {
        this.contextable = contextable;
    }
    
    /**
     * Get the HTTP context.
     * @return HTTP context
     */
    public HttpContext getContext() {
        Call call = contextable.getCall();
        return call.getContext();
    }
    
    /**
     * Can send any content, specifying the mime-type.
     * 
     * @param content String representation of the content, put directly in content of HTTP response
     * @param mimeType The content mime-type.
     * @param encoding The content encoding.
     * @return render represents the next step for user
     */
    public Render renderContent(String content, String mimeType, String encoding) {
        return new RenderContent(content, mimeType, encoding);
    }

    /**
     * Can send any content, specifying the mime-type. The encoding for response
     * is UTF-8  by default.
     * 
     * @param content String representation of the content, put directly in content of HTTP response
     * @param mimeType The content mime-type.
     * @return render represents the next step for user
     */
    public Render renderContent(String content, String mimeType) {
        return new RenderContent(content, mimeType);
    }

    /**
     * Can send any content, specifying the mime-type. For example return a file
     * content. 
     * 
     * @param stream Stream representation of the content, put directly in content of HTTP response
     * @param mimeType The content mime-type.
     * @param encoding The content encoding.
     * @return render represents the next step for user
     */
    public Render renderStream(InputStream stream, String mimeType, String encoding) {
        return new RenderStream(stream, mimeType, encoding);
    }
    
    /**
     * Can send any content, specifying the mime-type. The encoding for response
     * is UTF-8  by default. For example return a file content. 
     * 
     * @param stream Stream representation of the content, put directly in content of HTTP response
     * @param mimeType The content mime-type.
     * @return render represents the next step for user
     */
    public Render renderStream(InputStream stream, String mimeType) {
        return new RenderStream(stream, mimeType);
    }
    
    /**
     * Forward a page view (html, jsp, ...) defined in view package. For example,
     * if you have a class Test in org.my.app package, the view will be
     * searched in the /webapp/org/my/app/test. To use subfolders, 
     * just put the path conventionally with slashes.
     * 
     * @param page Fully qualified page name (with its subfolders from view package, if exist).
     * @param model data to use, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderView(String page, Object ... model) {
        return new RenderView(page, toMap(model));
    }

    /**
     * Include a page view (html, jsp, ...) defined in view package. It is use 
     * in AJAX call. For example, if you have a class Test in org.my.app package, the view will be
     * searched in the /webapp/org/my/app/test. To use subfolders, 
     * just put the path conventionally with slashes.
     * 
     * @param page Fully qualified page name (with its subfolders from view package, if exist).
     * @param model data to use, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderTemplate(String page, Object ... model) {
        return new RenderTemplate(page, toMap(model));
    }

    /**
     * Reload the previous page of user. Use referer in header to get last page.
     * You can add supplementary parameters with model. This render recovers the 
     * parameters only if the previous request was in GET http method.
     * 
     * @param model data used, either just one object, either keys with values
     * @return render represents the next step for user
     */
    public Render reloadPage(Object ... model) {
        return new RenderReferer(toMap(model));
    }

    /**
     * Redirect the user on a action. The action name contains the subpackage 
     * name, the classe name and the method name. Post/Redirect/Get design 
     * parttern is used. You must declare the action in mapping.
     * 
     * @param action action with subpackage, classe and method
     * @param model data used, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderAction(String action, Object ... model) {
        return new RenderAction(action, model);
    }

    /**
     * Redirect the user to an URL. The model is put as parameters in url;
     * 
     * @param url The redirect URL.
     * @param model data used, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderURL(String url, Object ... model) {
        return new RenderUrl(url, toMap(model));
    }

    /**
     * Send a status code to user (200, 302, ...) @HttpServletResponse.SC_* to 
     * get all the code values.
     * 
     * @param code code http like 200 or 302
     * @return render represents the next step for user
     */
    public Render renderStatus(int code) {
        return new RenderStatus(code);
    }
    
    /**
     * Send a error code to user (404, 500, ...) @HttpServletResponse.SC_* to 
     * get all the code values.
     * 
     * @param code code http like 404 or 500
     * @param message message to display
     * @return render represents the next step for user
     */
    public Render renderError(int code, String message) {
        return new RenderError(code, message);
    }
    
    /**
     * Serialize the model to XML.
     * 
     * @param model serialized data, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderXML(Object ... model) {
        return new RenderXml(toMap(model));
    }

    /**
     * Serialize the model to json.
     * 
     * @param model serialized data, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderJSON(Object ... model) {
        return new RenderJson(toMap(model));
    }

    /**
     * Call the callback in javascript with the model serialize to json. It is 
     * useful to avoid cross domain in browser. For more information you can 
     * consult JSONP documentation.
     * 
     * @param callback The Javascript callback function to call.
     * @param model serialized data, either just one object, either key/value pairs.
     * @return render represents the next step for user
     */
    public Render renderJSONP(String callback, Object ... model) {
        return new RenderJsonP(callback, toMap(model));
    }
    
    /**
     * Transforms the model to map. If the model contains only one element,  
     * creates a entry with key "model". Else the model is a pair of key and 
     * value.
     * @param model The model to transform.
     * @return map The transformed model.
     */
    protected Map<String, Object> toMap(Object ... model) {
        Map<String, Object> map = new LinkedHashMap<String, Object>(model.length / 2);
        
        if(model.length == 1) {
            map.put("model", model[0]);
            
        } else {
            for (int index = 0; index < model.length; index += 2) {
                String key = (String) model[index];
                Object value = model[index + 1];

                map.put(key, value);
            }
        }
        
        return map;
    }
}

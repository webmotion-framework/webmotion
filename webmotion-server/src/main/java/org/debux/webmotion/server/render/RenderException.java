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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.debux.webmotion.server.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Basic render execption to user.
 * 
 * @author julien
 */
public class RenderException extends RenderStringTemplate {

    public RenderException() {
        super("template/render_exception.stg", "text/html", new HashMap<String, Object>());
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        HttpSession session = context.getSession();
        ServerContext serverContext = context.getServerContext();
        ServletContext servletContext = context.getServletContext();
        
        // Retrieve the three possible error attributes, some may be null
        ErrorData errorData = context.getErrorData();
        Integer code = errorData.getStatusCode();
        String message = errorData.getMessage();
        Class<?> type = errorData.getExceptionType();
        Throwable throwable = errorData.getException();
        String uri = errorData.getRequestUri();
        
        if (uri == null) {
            uri = request.getRequestURI(); // in case there's no URI given
        }
        
        // The error reason is either the status code or exception type
        String reason = code != null ? "Error " + code.toString() : type.toString();
        
        // Get the stack trace
        StringWriter trace = new StringWriter();
        PrintWriter printTrace = new PrintWriter(trace);
        if (throwable != null) {
            throwable.printStackTrace(printTrace);
        }
                
        // Create the model
        model.put("reason", reason);
        model.put("message", message);
        model.put("trace", trace.toString());
        model.put("uri", uri);
        model.put("request", request);
        model.put("requestParameters", request.getParameterMap());
        model.put("requestAttributes", getRequestAttributes(request));
        model.put("requestHeaders", getRequestHeaders(request));
        model.put("session", session);
        model.put("sessionAttributes", getSessionAttributes(session));
        model.put("serverContextAttributes", serverContext.getAttributes());
        model.put("servletContextAttributes", getServletContextAttributes(servletContext));
        model.put("system", System.getProperties());
        
        super.create(mapping, call);
    }
    
    protected Map<String, Object> getRequestAttributes(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            Object value = request.getAttribute(name);
            result.put(name, value);
        }
        return result;
    }
    
    protected Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            Object value = request.getHeader(name);
            result.put(name, value);
        }
        return result;
    }
    
    protected Map<String, Object> getSessionAttributes(HttpSession session) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            Object value = session.getAttribute(name);
            result.put(name, value);
        }
        return result;
    }
    
    protected Map<String, Object> getServletContextAttributes(ServletContext servletContext) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Enumeration<String> names = servletContext.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            Object value = servletContext.getAttribute(name);
            result.put(name, value);
        }
        return result;
    }
    
}

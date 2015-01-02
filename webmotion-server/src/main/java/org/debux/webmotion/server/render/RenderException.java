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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.debux.webmotion.server.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Rule;

/**
 * Basic render execption to user.
 * 
 * @author julien
 */
public class RenderException extends RenderStringTemplate {

    public RenderException(String fileName) {
        super(fileName, "text/html", new HashMap<String, Object>());
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        ErrorData errorData = context.getErrorData();

        // Request
        HttpServletRequest request = context.getRequest();
        model.put("request", request);
        model.put("requestParameters", request.getParameterMap());
        model.put("requestAttributes", getRequestAttributes(request));
        model.put("requestHeaders", getRequestHeaders(request));

        // Session
        HttpSession session = context.getSession();
        model.put("session", session);
        model.put("sessionAttributes", getSessionAttributes(session));

        // ServerContext
        ServerContext serverContext = context.getServerContext();
        model.put("serverContextAttributes", serverContext.getAttributes());
        
        // Servlet context
        ServletContext servletContext = context.getServletContext();
        model.put("servletContextAttributes", getServletContextAttributes(servletContext));
        
        // Message
        String message = errorData.getMessage();
        model.put("message", message);
        
        // Uri
        String uri = errorData.getRequestUri();
        if (uri == null) {
            uri = request.getRequestURI(); // in case there's no URI given
        }
        model.put("uri", uri);
        
        // The error reason is either the status code or exception type
        Class<?> type = errorData.getExceptionType();
        Integer code = errorData.getStatusCode();
        String reason = code != null ? "Error " + code.toString() : type.toString();
        model.put("reason", reason);
        
        // Get the stack trace
        StringWriter trace = new StringWriter();
        PrintWriter printTrace = new PrintWriter(trace);
        Throwable throwable = errorData.getException();
        if (throwable != null) {
            throwable.printStackTrace(printTrace);
        }
        model.put("trace", trace.toString());
                
        // Get exception
        if (throwable instanceof WebMotionException) {
            WebMotionException exception = (WebMotionException) throwable;
            
            Rule rule = exception.getRule();
            if (rule != null) {
                String name = rule.getMapping().getName();
                int line = rule.getLine();
                
                URL url = new URL(name);
                InputStream stream = url.openStream();
                List<String> readLines = IOUtils.readLines(stream);
                String content = readLines.get(line - 1);
                
                model.put("mappingName", name);
                model.put("mappingLine", line);
                model.put("mappingContent", content);
            }
        }
        
        // System properties
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

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
package org.debux.webmotion.server.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to include a page, like jsp, html, .. in response.
 * 
 * @author julien
 */
public class RenderTemplate extends Render {
    protected String view;
    protected Map<String, Object> model;

    public RenderTemplate(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getView() {
        return view;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        RenderTemplate render = (RenderTemplate) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        addModel(call, model);
        
        ServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        String path = getActionPath(mapping, view);
        request.getRequestDispatcher(path).include(requestWrapper, responseWrapper);
        
        String contentType = responseWrapper.getContentType();
        response.setContentType(contentType);
        
        PrintWriter out = context.getOut();
        String include = responseWrapper.getContent();
        out.write(include);
    }
    
    
    /**
     * Wrap response to get content. Use to manage template with AJAX call.
     */
    public static class ResponseWrapper extends HttpServletResponseWrapper {
        
        protected ByteArrayOutputStream stream;

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
            this.stream = new ByteArrayOutputStream();
        }
            
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStreamWrapper(stream);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(stream);
        }

        @Override
        public void flushBuffer() throws IOException {
            stream.flush();
        }
        
        public String getContent() {
            return stream.toString();
        }
    }

    public static class ServletOutputStreamWrapper extends ServletOutputStream {
        protected OutputStream outputStream;

        public ServletOutputStreamWrapper(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }
    }
}

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
package org.debux.webmotion.server.handler;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.call.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Render creator do the render for user. Apply the good redirect, foward or 
 * include on response.
 * 
 * @author jruchaud
 */
public class RenderCreatorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(RenderCreatorHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        try {
            Render render = call.getRender();
            if(render == null) {
                return;
            }
            
            if(render instanceof Render.RenderView) {
                renderView(mapping, call);
                
            } else if(render instanceof Render.RenderTemplate) {
                renderTemplate(mapping, call);
                
            } else if(render instanceof Render.RenderReferer) {
                renderReferer(mapping, call);
                
            } else if(render instanceof Render.RenderStream) {
                renderStream(mapping, call);
                
            } else if(render instanceof Render.RenderAction) {
                renderAction(mapping, call);
                
            } else if(render instanceof Render.RenderError) {
                renderError(mapping, call);
                
            } else if(render instanceof Render.RenderUrl) {
                renderUrl(mapping, call);
                
            } else if(render instanceof Render.RenderXml) {
                renderXml(mapping, call);
                
            } else if(render instanceof Render.RenderJson) {
                renderJson(mapping, call);
                
            } else if(render instanceof Render.RenderJsonP) {
                renderJsonP(mapping, call);
                
            } else {
                renderContent(mapping, call);
            }

            if(call.isFileUploadRequest()) {
                HttpContext context = call.getContext();
                HttpSession session = context.getSession();
                if(session != null) {
                    session.removeAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
                }
            }
            
        } catch (IOException ioe) {
            throw new WebMotionException("Error during write the render in response", ioe);
            
        } catch (ServletException se) {
            throw new WebMotionException("Error on server when write the render in response", se);
        }
    }

    protected void renderView(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderView render = (Render.RenderView) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        Map<String, Object> model = render.getModel();
        addModel(call, model);
        
        String view = render.getView();
        String path = getActionPath(mapping, call, view);
        
        request.getRequestDispatcher(path).forward(request, response);
    }

    protected void renderTemplate(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderTemplate render = (Render.RenderTemplate) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        Map<String, Object> model = render.getModel();
        addModel(call, model);

        ServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        
        String view = render.getView();
        String path = getActionPath(mapping, call, view);
        request.getRequestDispatcher(path).include(requestWrapper, responseWrapper);
        
        String contentType = responseWrapper.getContentType();
        response.setContentType(contentType);

        PrintWriter out = context.getOut();
        String include = responseWrapper.getContent();
        out.write(include);
    }

    protected void renderReferer(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderReferer render = (Render.RenderReferer) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String path = context.getHeader(HttpContext.HEADER_REFERER);
        Map<String, Object> model = render.getModel();
        path = addModel(call, path, model);
        
        response.sendRedirect(path);
    }
    
    protected void renderAction(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderAction render = (Render.RenderAction) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String content = render.getAction();
        String path = context.getBaseUrl() + "/"
                + WebMotionUtils.unCapitalizeClass(content).replaceAll("\\.", "/");
        
        Map<String, Object> model = render.getModel();
        path = addModel(call, path, model);
        
        response.sendRedirect(path);
    }

    protected void renderContent(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderContent render = (Render.RenderContent) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String mineType = render.getMimeType();
        if(mineType != null) {
            response.setContentType(mineType);
        }
        
        String encoding = render.getEncoding();
        response.setCharacterEncoding(encoding);

        String content = render.getContent();
        PrintWriter out = context.getOut();
        out.print(content);
    }

    protected void renderStream(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderStream render = (Render.RenderStream) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String mineType = render.getMimeType();
        if(mineType != null) {
            response.setContentType(mineType);
        }
        
        String encoding = render.getEncoding();
        response.setCharacterEncoding(encoding);

        InputStream inputStream = render.getStream();
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.copy(inputStream, outputStream);
    }

    protected void renderError(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderError render = (Render.RenderError) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String message = render.getMessage();
        int code = render.getCode();
        response.sendError(code, message);
    }

    protected void renderUrl(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderUrl render = (Render.RenderUrl) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String url = render.getUrl();
        response.sendRedirect(url);
    }

    protected void renderXml(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderXml render = (Render.RenderXml) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        
        Map<String, Object> model = render.getModel();
        Object object = model;
        if (model != null && model.size() == 1) {
            object = model.values().toArray()[0];
        }

        XStream xstream = new XStream();
        String xml = xstream.toXML(object);
        
        PrintWriter out = context.getOut();
        out.print(xml);
        
        response.setContentType("application/xml");
    }

    protected void renderJson(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderJson render = (Render.RenderJson) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        Map<String, Object> model = render.getModel();
        Object object = model;
        if(model != null && model.size() == 1) {
            object = model.values().toArray()[0];
        }
        
        Gson gson = new Gson();
        String json = gson.toJson(object);
        
        PrintWriter out = context.getOut();
        out.print(json);
        
        response.setContentType("application/json");
    }

    protected void renderJsonP(Mapping mapping, Call call) throws IOException, ServletException {
        Render.RenderJsonP render = (Render.RenderJsonP) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        Map<String, Object> model = render.getModel();
        Object object = model;
        if(model != null && model.size() == 1) {
            object = model.values().toArray()[0];
        }
        
        Gson gson = new Gson();
        String json = gson.toJson(object);
        
        PrintWriter out = context.getOut();
        String callback = render.getCallback();
        out.print(callback + "(" + json + ");");
        
        response.setContentType("application/javascript");
    }
    
    protected String getActionPath(Mapping mapping, Call call, String view) {
        Config config = mapping.getConfig();
        String packageName = config.getPackageViews().replaceAll("\\.", "/");
        
        String subPackageName = "";
        Executor executor = call.getExecutor();
        if(executor != null) {
            String packageActions = config.getPackageActions();
            String packageFilters = config.getPackageFilters();
            String packageErrors = config.getPackageErrors();

            subPackageName = executor.getClazz().getName();
            subPackageName = subPackageName.replace(packageActions, "");
            subPackageName = subPackageName.replace(packageFilters, "");
            subPackageName = subPackageName.replace(packageErrors, "");
            
            subPackageName = WebMotionUtils.unCapitalizeClass(subPackageName);
            subPackageName = subPackageName.replaceAll("\\.", "/");
        }

        String path = "/" + packageName + "/" + subPackageName + "/" + view;
        log.info("path = " + path);
        return path;
    }

    /**
     * Add model in attribute
     * @param render
     * @param request 
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
     * @param call
     * @param url
     * @return 
     */
    protected String addModel(Call call, String url, Map<String, Object> model) {
        if(model != null) {

            String separator = "?";
            if(url.contains("?")) {
                separator = "&";
            }
            
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                url += separator + key + "=" + value;
                separator = "&";
            }
        }
        return url;
    }

    /**
     * Wrap response to get content. Use to manage template with AJAX call.
     */
    public class ResponseWrapper extends HttpServletResponseWrapper {
        
        protected ByteArrayOutputStream stream;

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
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

    public class ServletOutputStreamWrapper extends ServletOutputStream {
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

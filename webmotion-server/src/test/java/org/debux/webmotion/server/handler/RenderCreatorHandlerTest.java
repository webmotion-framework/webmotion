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
package org.debux.webmotion.server.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderContent;
import org.debux.webmotion.server.render.RenderJson;
import org.debux.webmotion.server.render.RenderJsonP;
import org.debux.webmotion.server.render.RenderXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * 
 * @author julien
 */
public class RenderCreatorHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(RenderCreatorHandlerTest.class);

    public static class ModelExemple {
        public String attribute1 = "value1";
        public String attribute2 = "value2";
    }
    
    @Test
    public void testRenderXml() throws IOException, ServletException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("model", new ModelExemple());
        RenderXml render = new RenderXml(model);
        
        Call call = new CallWrapper(render);
        render.create(null, call);
        
        ContextWrapper context = (ContextWrapper) call.getContext();
        StringWriter content = context.getContent();
        AssertJUnit.assertNotNull(content.toString());
    }
    
    @Test
    public void testRenderJson() throws IOException, ServletException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("model", new ModelExemple());
        RenderJson render = new RenderJson(model);
        
        Call call = new CallWrapper(render);
        render.create(null, call);
        
        ContextWrapper context = (ContextWrapper) call.getContext();
        StringWriter content = context.getContent();
        AssertJUnit.assertNotNull(content.toString());
    }
    
    @Test
    public void testRenderJsonP() throws IOException, ServletException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("model", new ModelExemple());
        RenderJsonP render = new RenderJsonP("callback", model);
        
        Call call = new CallWrapper(render);
        render.create(null, call);
        
        ContextWrapper context = (ContextWrapper) call.getContext();
        StringWriter content = context.getContent();
        AssertJUnit.assertNotNull(content.toString());
        AssertJUnit.assertTrue(content.toString().startsWith("callback"));
    }
    
    @Test
    public void testRenderContent() throws IOException, ServletException {
        RenderContent render = new RenderContent("content", "text/plain");
        Call call = new CallWrapper(render);
        render.create(null, call);
        
        ContextWrapper context = (ContextWrapper) call.getContext();
        StringWriter content = context.getContent();
        AssertJUnit.assertNotNull(content.toString());
        AssertJUnit.assertEquals("content", content.toString());
    }
    
    public static class CallWrapper extends Call {
        protected Render render;
        protected ContextWrapper context;

        public CallWrapper(Render render) {
            this.render = render;
            this.context = new ContextWrapper();
        }
        
        @Override
        public HttpContext getContext() {
            return context;
        }

        @Override
        public Render getRender() {
            return render;
        }
    }
    
    public static class ContextWrapper extends HttpContext {
        protected StringWriter content = new StringWriter();

        @Override
        public PrintWriter getOut() throws IOException {
            return new PrintWriter(content);
        }

        public StringWriter getContent() {
            return content;
        }
        
        @Override
        public HttpServletResponse getResponse() {
            return new ResponseWrapper();
        }
    }
    
    public static class ResponseWrapper implements HttpServletResponse {

        @Override
        public void addCookie(Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String string) {
            return true;
        }

        @Override
        public String encodeURL(String string) {
            return string;
        }

        @Override
        public String encodeRedirectURL(String string) {
            return string;
        }

        @Override
        public String encodeUrl(String string) {
            return string;
        }

        @Override
        public String encodeRedirectUrl(String string) {
            return string;
        }

        @Override
        public void sendError(int i, String string) throws IOException {
        }

        @Override
        public void sendError(int i) throws IOException {
        }

        @Override
        public void sendRedirect(String string) throws IOException {
        }

        @Override
        public void setDateHeader(String string, long l) {
        }

        @Override
        public void addDateHeader(String string, long l) {
        }

        @Override
        public void setHeader(String string, String string1) {
        }

        @Override
        public void addHeader(String string, String string1) {
        }

        @Override
        public void setIntHeader(String string, int i) {
        }

        @Override
        public void addIntHeader(String string, int i) {
        }

        @Override
        public void setStatus(int i) {
        }

        @Override
        public void setStatus(int i, String string) {
        }

        @Override
        public String getCharacterEncoding() {
            return "utf-8";
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public void setCharacterEncoding(String string) {
        }

        @Override
        public void setContentLength(int i) {
        }

        @Override
        public void setContentType(String string) {
        }

        @Override
        public void setBufferSize(int i) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() throws IOException {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(Locale locale) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public String getHeader(String value) {
            return value;
        }

        @Override
        public Collection<String> getHeaders(String value) {
            return new ArrayList<String>();
        }

        @Override
        public Collection<String> getHeaderNames() {
            return new ArrayList<String>();
        }
    }
}

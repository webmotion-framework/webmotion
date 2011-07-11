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
package org.debux.webmotion.server.call;

import java.io.InputStream;
import java.util.Map;
import org.debux.webmotion.server.WebMotionAction;

/**
 * The render represents result for user request. It contains the necessary 
 * elements to create the http response. In the {@see WebMotionAction} class, there are
 * utility methods to instantiated correctly a render. The render can be a 
 * view (template), an URL (referer), an action, a model (json, jsonp, xml) or 
 * directly a content with specified mime-type.
 * 
 * @author julien
 */
public class Render {

    public static String DEFAULT_ENCODING = "UTF-8";
    
    public static class RenderContent extends Render {
        protected String content;
        protected String mimeType;
        protected String encoding;

        public RenderContent(String content, String mimeType, String encoding) {
            this.content = content;
            this.mimeType = mimeType;
            this.encoding = encoding;
        }

        public RenderContent(String content, String mimeType) {
            this(content, mimeType, DEFAULT_ENCODING);
        }

        public String getContent() {
            return content;
        }

        public String getEncoding() {
            return encoding;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
    
    public static class RenderStream extends Render {
        protected InputStream stream;
        protected String mimeType;
        protected String encoding;

        public RenderStream(InputStream stream, String mimeType, String encoding) {
            this.stream = stream;
            this.mimeType = mimeType;
            this.encoding = encoding;
        }

        public RenderStream(InputStream stream, String mimeType) {
            this(stream, mimeType, DEFAULT_ENCODING);
        }

        public String getEncoding() {
            return encoding;
        }

        public String getMimeType() {
            return mimeType;
        }

        public InputStream getStream() {
            return stream;
        }
    }
    
    public static class RenderView extends Render {
        protected String view;
        protected Map<String, Object> model;

        public RenderView(String view, Map<String, Object> model) {
            this.view = view;
            this.model = model;
        }

        public Map<String, Object> getModel() {
            return model;
        }

        public String getView() {
            return view;
        }
    }
    
    public static class RenderTemplate extends Render {
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
    }
    
    public static class RenderReferer extends Render {
        protected Map<String, Object> model;

        public RenderReferer(Map<String, Object> model) {
            this.model = model;
        }

        public Map<String, Object> getModel() {
            return model;
        }
    }
    
    public static class RenderAction extends Render {
        protected String action;
        protected Map<String, Object> model;

        public RenderAction(String action, Map<String, Object> model) {
            this.action = action;
            this.model = model;
        }

        public String getAction() {
            return action;
        }

        public Map<String, Object> getModel() {
            return model;
        }
    }
    
    public static class RenderUrl extends Render {
        protected String url;
        protected Map<String, Object> model;

        public RenderUrl(String url, Map<String, Object> model) {
            this.url = url;
            this.model = model;
        }

        public Map<String, Object> getModel() {
            return model;
        }

        public String getUrl() {
            return url;
        }
    }
    
    public static class RenderJson extends Render {
        protected Map<String, Object> model;

        public RenderJson(Map<String, Object> model) {
            this.model = model;
        }

        public Map<String, Object> getModel() {
            return model;
        }
    }
    
    public static class RenderXml extends Render {
        protected Map<String, Object> model;

        public RenderXml(Map<String, Object> model) {
            this.model = model;
        }

        public Map<String, Object> getModel() {
            return model;
        }
    }
    
    public static class RenderJsonP extends Render {
        protected String callback;
        protected Map<String, Object> model;

        public RenderJsonP(String callback, Map<String, Object> model) {
            this.callback = callback;
            this.model = model;
        }

        public String getCallback() {
            return callback;
        }

        public Map<String, Object> getModel() {
            return model;
        }
    }
    
    public static class RenderError extends Render {
        protected int code;
        protected String message;

        public RenderError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
    
}

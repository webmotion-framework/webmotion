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

import java.util.Map;

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
    
    public static MimeType MIME_VIEW         = new MimeType();
    public static MimeType MIME_TEMPLATE     = new MimeType();
    public static MimeType MIME_REFERER      = new MimeType();
    public static MimeType MIME_ACTION       = new MimeType();
    public static MimeType MIME_URL          = new MimeType();
    public static MimeType MIME_JSON         = new MimeType("application/javascript");
    public static MimeType MIME_JSONP        = new MimeType("application/json");
    public static MimeType MIME_XML          = new MimeType("application/xml");
    
    public static class MimeType {
        protected String mimeType;

        public MimeType() {
        }
        
        public MimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        @Override
        public String toString() {
            return mimeType;
        }
                
    }
    
    protected String content;
    protected MimeType mimeType;
    protected String encoding;
    protected Map<String, Object> model;

    public Render(String content, MimeType mimeType, String encoding, Map<String, Object> model) {
        this.content = content;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.model = model;
    }
    
    public Render(String content, String mineType, String encoding, Map<String, Object> model) {
        this(content, new MimeType(mineType), encoding, model);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public MimeType getMimeType() {
        return mimeType;
    }
    
    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
    
}

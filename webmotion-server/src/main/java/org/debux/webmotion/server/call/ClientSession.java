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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.Transient;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import org.debux.webmotion.server.call.CookieManger.CookieEntity;

/**
 * @author jruchaud
 */
public class ClientSession implements HttpSession {

    public static final String SESSION_CONTEXT_COOKIE_NAME = "wm_session_context";
    public static final String ATTRIBUTES_COOKIE_NAME = "wm_attributes";
    
    protected JsonParser parser = new JsonParser();
    protected Gson gson = new Gson();
    protected CookieManger manger;
        
    protected HttpContext context;
    protected ClientSessionContext sessionContext;
    protected JsonObject attributes;
    
    public static class ClientSessionContext {

        protected String id;
        protected long creationTime;
        protected long lastAccessedTime;
        protected int maxInactiveInterval;
        
        @Transient
        protected boolean newly;

        public ClientSessionContext() {
            newly = true;
            creationTime = System.currentTimeMillis();
        }

        public long getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        public void setMaxInactiveInterval(int maxInactiveInterval) {
            this.maxInactiveInterval = maxInactiveInterval;
        }

        public boolean isNewly() {
            return newly;
        }

        public void setNewly(boolean newly) {
            this.newly = newly;
        }
    }

    public ClientSession(HttpContext context, String id) {
        this.context = context;
        this.manger = new CookieManger(context, id, true, true);
        
        CookieEntity sessionContextCookie = this.manger.get(SESSION_CONTEXT_COOKIE_NAME);
        this.sessionContext = sessionContextCookie.getValue(ClientSessionContext.class);
        if (this.sessionContext == null) {
            this.sessionContext = new ClientSessionContext();
        }
        
        CookieEntity attributesCookie = this.manger.get(ATTRIBUTES_COOKIE_NAME);
        String value = attributesCookie.getValue();
        if (value == null) {
            this.attributes = new JsonObject();
        } else {
            this.attributes = this.parser.parse(value).getAsJsonObject();
        }
    }
    
    @Override
    public long getCreationTime() {
        return sessionContext.getCreationTime();
    }

    @Override
    public String getId() {
        return sessionContext.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return sessionContext.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        sessionContext.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return sessionContext.getMaxInactiveInterval();
    }

    @Override
    public Object getAttribute(String name) {
        // Return only JsonElement
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        String[] names = getValueNames();
        List<String> list = Arrays.asList(names);
        return Collections.enumeration(list);
    }

    @Override
    public String[] getValueNames() {
        Set<Entry<String, JsonElement>> entries = attributes.entrySet();
        String[] result = new String[entries.size()];
        int index = 0;
        
        for (Entry<String, JsonElement> entry : entries) {
            String name = entry.getKey();
            result[index++] = name;
        }
        return result;
    }

    @Override
    public void setAttribute(String name, Object value) {
        JsonElement element = gson.toJsonTree(value);
        attributes.add(name, element);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        attributes = new JsonObject();
        sessionContext = new ClientSessionContext();
    }

    @Override
    public boolean isNew() {
        return sessionContext.isNewly();
    }
    
    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

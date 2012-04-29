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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.Transient;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.debux.webmotion.server.call.CookieManger.CookieEntity;

/**
 * @author jruchaud
 */
public class ClientSession implements HttpSession {

    public static final int DEFAULT_TIMEOUT = 2 * 60 * 60; // 2h
    
    public static final String SESSION_CONTEXT_COOKIE_NAME = "wm_session_context";
    public static final String ATTRIBUTES_COOKIE_NAME = "wm_attributes";
    
    protected JsonParser parser = new JsonParser();
    protected Gson gson = new Gson();
        
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
            id = RandomStringUtils.random(32, true, true);
            newly = true;
            creationTime = System.currentTimeMillis();
            maxInactiveInterval = DEFAULT_TIMEOUT;
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

    public ClientSession(HttpContext context) {
        this.context = context;
        
        int maxInactiveInterval = sessionContext.getMaxInactiveInterval() * 1000;
        long lastAccessedTime = sessionContext.getLastAccessedTime();
        long currentAccessedTime = System.currentTimeMillis();
        
        if (maxInactiveInterval <= 0 || 
                maxInactiveInterval + lastAccessedTime < currentAccessedTime) {
            sessionContext.setLastAccessedTime(currentAccessedTime);
            attributes = readAttributes(sessionContext);
        } else {
            invalidate();
        }
    }
    
    protected ClientSessionContext readSessionContext() {
        CookieManger manger = context.getCookieManger();
        CookieEntity cookie = manger.get(SESSION_CONTEXT_COOKIE_NAME);
        
        ClientSessionContext result = null;
        if (cookie != null) {
            result = cookie.getValue(ClientSessionContext.class);
        } else {
            result = new ClientSessionContext();
        }
        return result;
    }
    
    protected void writeSessionContext(ClientSessionContext sessionContext) {
        CookieManger manger = context.getCookieManger();
        CookieEntity cookie = manger.create(SESSION_CONTEXT_COOKIE_NAME, sessionContext);
        cookie.setPath("/");
        cookie.setMaxAge(sessionContext.getMaxInactiveInterval());
        manger.add(cookie);
    }
    
    protected JsonObject readAttributes(ClientSessionContext sessionContext) {
        String id = sessionContext.getId();
        CookieManger manger = context.getCookieManger(id, true, true);
        CookieEntity cookie = manger.get(ATTRIBUTES_COOKIE_NAME);
        
        JsonObject result = null;
        if (cookie != null) {
            String value = cookie.getValue();
            result = parser.parse(value).getAsJsonObject();
        } else {
            result = new JsonObject();
        }
        return result;
    }
    
    protected void writeAttributes(ClientSessionContext sessionContext, JsonObject attributes) {
        String id = sessionContext.getId();
        CookieManger manger = context.getCookieManger(id, true, true);
        CookieEntity cookie = manger.create(ATTRIBUTES_COOKIE_NAME, attributes);
        cookie.setPath("/");
        cookie.setMaxAge(sessionContext.getMaxInactiveInterval());
        manger.add(cookie);
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

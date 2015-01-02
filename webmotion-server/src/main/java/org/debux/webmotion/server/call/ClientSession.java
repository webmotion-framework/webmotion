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
package org.debux.webmotion.server.call;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.debux.webmotion.server.call.CookieManager.CookieEntity;

/**
 * Manages a client session with the cookies. Thus you can easily scale your server. 
 * The class uses @see CookieManager to store secure data on client. The data is 
 * serialized to json object, to get the value use getAttribute(s) method with 
 * class as parameter. Currently the session is limited by cookie limitation, 
 * the session can't exceeded 4ko.
 * 
 * @author jruchaud
 */
public class ClientSession implements HttpSession {

    /** Default timeout for the session, you can modify with setMaxInactiveInterval method */
    public static final int DEFAULT_TIMEOUT = 2 * 60 * 60; // 2h
    
    /** Cookie name to store SessionContext */
    public static final String SESSION_CONTEXT_COOKIE_NAME = "wm_session_context";
    
    /* Cookie name to store attributes */
    public static final String ATTRIBUTES_COOKIE_NAME = "wm_attributes";
    
    /** Parser json */
    protected JsonParser parser = new JsonParser();
    
    /** Parser json */
    protected Gson gson = new Gson();
        
    /** Current http context */
    protected HttpContext context;
    
    /** Current session context */
    protected ClientSessionContext sessionContext;
    
    /** Current attributes in session */
    protected JsonObject attributes;
    
    /**
     * The class contains information on state of session.
     */
    public static class ClientSessionContext {

        /** Session id */
        protected String id;
        
        /** Creation date (millisecond) */
        protected long creationTime;
        
        /** Last access (millisecond) */
        protected long lastAccessedTime;
        
        /** Interval beetween access before to invalidate the session (second) */
        protected int maxInactiveInterval;
        
        /** Indicate if the session just created */
        protected transient boolean newly;

        /**
         * Default constructor, generate the id and indicate the session is new.
         */
        public ClientSessionContext() {
            this.id = RandomStringUtils.random(32, true, true);
            this.newly = true;
            this.creationTime = System.currentTimeMillis();
            this.lastAccessedTime = creationTime;
            this.maxInactiveInterval = DEFAULT_TIMEOUT;
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

    /**
     * Constructor use for the tests.
     */
    protected ClientSession() {
        sessionContext = new ClientSessionContext();
        attributes = new JsonObject();
    }
    
    /**
     * Read the session in cookie and validate if the session is valid.
     * 
     * @param context http context
     */
    public ClientSession(HttpContext context) {
        this.context = context;
        
        sessionContext = readSessionContext();
        
        long currentAccessedTime = System.currentTimeMillis();
        if (isExpired(currentAccessedTime)) {
            invalidate();
        } else {
            sessionContext.setLastAccessedTime(currentAccessedTime);
            attributes = readAttributes(sessionContext);
        }
    }
    
    /**
     * Check if the session is expired. The session is expired when the last 
     * access is too late betwwen inactive interval.
     * 
     * @param currentAccessedTime reference last access, generally equals <code>System.currentTimeMillis()</code>
     * @return true if the session is expired else false
     */
    protected boolean isExpired(long currentAccessedTime) {
        int maxInactiveInterval = sessionContext.getMaxInactiveInterval() * 1000;
        long lastAccessedTime = sessionContext.getLastAccessedTime();
        
        return maxInactiveInterval > 0 && 
                maxInactiveInterval + lastAccessedTime < currentAccessedTime;
    }
    
    /**
     * Read the session context in cookie.
     * 
     * @return session context
     */
    protected ClientSessionContext readSessionContext() {
        CookieManager manager = context.getCookieManager();
        CookieEntity cookie = manager.get(SESSION_CONTEXT_COOKIE_NAME);
        
        ClientSessionContext result = null;
        if (cookie != null) {
            result = cookie.getValue(ClientSessionContext.class);
        } else {
            result = new ClientSessionContext();
        }
        return result;
    }
    
    /**
     * Read the attributes in cookie. Requires session id to decrypt the cookie.
     * 
     * @param sessionContext session context
     * @return attributes
     */
    protected JsonObject readAttributes(ClientSessionContext sessionContext) {
        String id = sessionContext.getId();
        CookieManager manager = context.getCookieManager(id, true, true);
        CookieEntity cookie = manager.get(ATTRIBUTES_COOKIE_NAME);
        
        JsonObject result = new JsonObject();
        if (cookie != null) {
            String value = cookie.getValue();
            if (value != null) {
                result = parser.parse(value).getAsJsonObject();
            }
        }
        return result;
    }
    
    /**
     * Write the session context and attributes in cookie.
     */
    public void write() {
        CookieManager manager = context.getCookieManager();
        CookieEntity cookie = manager.create(SESSION_CONTEXT_COOKIE_NAME, sessionContext);
        cookie.setAbsolutePath("/");
        cookie.setMaxAge(sessionContext.getMaxInactiveInterval());
        manager.add(cookie);
        
        String id = sessionContext.getId();
        manager = context.getCookieManager(id, true, true);
        
        String toJson = gson.toJson(attributes);
        cookie = manager.create(ATTRIBUTES_COOKIE_NAME, toJson);
        cookie.setAbsolutePath("/");
        cookie.setMaxAge(sessionContext.getMaxInactiveInterval());
        manager.add(cookie);
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

    /**
     * Return only JsonElement use getAttribute and getAttributes with class as 
     * parameter to get a value.
     */
    @Override
    public Object getAttribute(String name) {
        JsonElement value = attributes.get(name);
        if (value == null) {
            return value;
            
        } else if (value.isJsonPrimitive()) {
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
                
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
                
            } else if (primitive.isNumber()) {
                return primitive.getAsDouble();
            }
            
        } else if (value.isJsonArray()) {
            return value.getAsJsonArray();
            
        } else if (value.isJsonObject()) {
            return value.getAsJsonObject();
            
        } else if (value.isJsonNull()) {
            return value.getAsJsonNull();
        } 
        return value;
    }

    /**
     * Get the attribute value as object representation. It is not possible to deserialize 
     * collection with objects of arbitrary types, in this case getAttribute 
     * as JsonElement.
     * For example get int array, pass <code>int[].class</code> as class.
     * @param name attribute name
     * @param clazz class to value
     * @return object
     */
    public <T> T getAttribute(String name, Class<T> clazz) {
        JsonElement value = attributes.get(name);
        T fromJson = gson.fromJson(value, clazz);
        return fromJson;
    }
    
    /**
     * Get the attribute value as collection. It is not possible to deserialize 
     * collection with objects of arbitrary types, in this case getAttribute 
     * as JsonElement.
     * @param name attribute name
     * @param clazz object class in collection
     * @return collection of values
     */
    public <T> Collection<T> getAttributes(String name, Class<T> clazz) {
        JsonElement value = attributes.get(name);
        Class arrayClass = Array.newInstance(clazz, 0).getClass();
        T[] fromJson = (T[]) gson.fromJson(value, arrayClass);
        if (fromJson != null) {
            return Arrays.asList(fromJson);
        }
        return null;
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

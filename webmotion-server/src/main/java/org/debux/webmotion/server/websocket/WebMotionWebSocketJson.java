/*
 * #%L
 * WebMotion server
 * $Id:$
 * $HeadURL:$
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
package org.debux.webmotion.server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Class to manage JSON as message in the websocket. For example the JSON object :
 * {
 *      method : "run"
 *      params : {
 *          value : 3
 *      }
 * }
 * call run method with param value equal 3.
 * 
 * The method return the result as JSON object :
 * {
 *      method : "run"
 *      result : true
 * }
 * 
 * @author julien
 */
public class WebMotionWebSocketJson extends WebMotionWebSocket {

    /** Json serializer to store object */
    protected Gson gson;
    protected JsonParser parser;

    public WebMotionWebSocketJson() {
        gson = new Gson();
        parser = new JsonParser();
    }
    
    @Override
    public void receiveDataMessage(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendDataMessage(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendTextMessage(String message) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void receiveTextMessage(String json) {
        try {
            JsonElement element = parser.parse(json);
            JsonObject object = element.getAsJsonObject();
            
            String methodName = object.get("method").getAsString();
            Method method = WebMotionUtils.getMethod(getClass(), methodName);
            
            ServerContext serverContext = getServerContext();
            Mapping mapping = serverContext.getMapping();
            
            String[] parameterNames = WebMotionUtils.getParameterNames(mapping, method);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            
            JsonElement params = object.get("params");
            JsonObject values = null;
            if (params != null) {
                values = params.getAsJsonObject();
            }
            
            List<Object> convertedParameters = new ArrayList<Object>();
            for (int position = 0; position < parameterNames.length; position ++) {
                String name = parameterNames[position];
                Class<?> type = parameterTypes[position];
                Type genericType = genericParameterTypes[position];
                
                if (values == null) {
                    convertedParameters.add(null);
                    
                } else {
                    JsonElement value = values.get(name);
                    if (value == null) {
                        convertedParameters.add(null);
                        
                    } else {
                        if (Collection.class.isAssignableFrom(type)) {
                            Collection converted;
                            if (type.isInterface()) {
                                if (List.class.isAssignableFrom(type)) {
                                    converted = new ArrayList();

                                } else if (Set.class.isAssignableFrom(type)) {
                                    converted = new HashSet();

                                } else if (SortedSet.class.isAssignableFrom(type)) {
                                    converted = new TreeSet();

                                } else {
                                    converted = new ArrayList();
                                }
                            } else {
                                converted = (Collection) type.newInstance();
                            }

                            Class convertType = String.class;
                            if (genericType != null && genericType instanceof ParameterizedType) {
                                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                                convertType = (Class) parameterizedType.getActualTypeArguments()[0];
                            }

                            Collection collection = getValues(value, convertType);
                            converted.addAll(collection);
                            convertedParameters.add(converted);

                        } else {
                            Object converted = getValue(value, type);
                            convertedParameters.add(converted);
                        }
                    }
                }
            }
            
            Object invoke = method.invoke(this, convertedParameters.toArray());
            sendObjectMessage(methodName, invoke);
            
        } catch (InstantiationException ex) {
            throw new WebMotionException("Error during invocation", ex);
        } catch (IllegalAccessException ex) {
            throw new WebMotionException("Error during invocation", ex);
        } catch (IllegalArgumentException ex) {
            throw new WebMotionException("Error during invocation", ex);
        } catch (InvocationTargetException ex) {
            throw new WebMotionException("Error during invocation", ex);
        }
    }
    
    public void sendObjectMessage(String methodName, Object message) {
        JsonObject result = new JsonObject();
        result.add("method", new JsonPrimitive(methodName));
        result.add("result", gson.toJsonTree(message));
        super.sendTextMessage(gson.toJson(result));
    }

    public <T> T getValue(JsonElement value, Class<T> clazz) {
        T fromJson = gson.fromJson(value, clazz);
        return fromJson;
    }

    public <T> Collection<T> getValues(JsonElement value, Class<T> clazz) {
        Class arrayClass = Array.newInstance(clazz, 0).getClass();
        T[] fromJson = (T[]) gson.fromJson(value, arrayClass);
        if (fromJson != null) {
            return Arrays.asList(fromJson);
        }
        return null;
    }

}

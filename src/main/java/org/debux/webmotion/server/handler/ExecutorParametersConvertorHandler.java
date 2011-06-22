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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.WebMotionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store in the call object, all parameters converted depending action method 
 * invoked. Use apache ConvertUtilsBean to process.
 * 
 * All following convertion is possible :
 * <ul>
 * <li>java.lang.BigDecimal (no default value)</li>
 * <li>java.lang.BigInteger (no default value)</li>
 * <li>boolean & java.lang.Boolean (default to false)</li>
 * <li>byte & java.lang.Byte (default to zero)</li>
 * <li>char & java.lang.Character (default to a space)</li>
 * <li>java.lang.Class (no default value)</li>
 * <li>double & java.lang.Double (default to zero)</li>
 * <li>float & java.lang.Float (default to zero)</li>
 * <li>int & java.lang.Integer (default to zero)</li>
 * <li>long & java.lang.Long (default to zero)</li>
 * <li>short & java.lang.Short (default to zero)</li>
 * <li>java.lang.String (default to null)</li>
 * <li>java.io.File (no default value)</li>
 * <li>java.net.URL (no default value)</li>
 * <li>java.sql.Date (no default value) (string format [yyyy-MM-dd])</li>
 * <li>java.sql.Time (no default value) (string format [HH:mm:ss])</li>
 * <li>java.sql.Timestamp (no default value) (string format [yyyy-MM-dd HH:mm:ss.fffffffff])</li>
 * <li>POJO (no default value)</li>
 * <li>java.util.Map (no default value)</li>
 * <li>java.util.Set (no default value)</li>
 * <li>Arrays (no default value)</li>
 * </ul>
 * 
 * @author julien
 */
public class ExecutorParametersConvertorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersConvertorHandler.class);

    protected ConvertUtilsBean converter = new ConvertUtilsBean();
    protected BeanUtilsBean beanUtil = BeanUtilsBean.getInstance();
    
    @Override
    public void handle(Mapping mapping, Call call) {
            List<Executor> executors = call.getExecutors();
            for (Executor executor : executors) {

                Method executorMethod = executor.getMethod();
                String[] parameterNames = WebMotionUtils.getParameterNames(mapping, executorMethod);

                // Sort parameters and convert
                Map<String, Object> parameters = call.getAliasParameters();
                Class<?>[] parameterTypes = executorMethod.getParameterTypes();
                Type[] genericParameterTypes = executorMethod.getGenericParameterTypes();

                // Save object in call
                Map<String, Object> convertedParameters = new LinkedHashMap<String, Object>(parameterNames.length);
                executor.setParameters(convertedParameters);

                for (int position = 0; position < parameterNames.length; position ++) {
                    String name = parameterNames[position];
                    Object value = parameters.get(name);
                    Class<?> type = parameterTypes[position];
                    Type genericType = genericParameterTypes[position];

                    try {
                        value = convert(value, type, genericType);
                        convertedParameters.put(name, value);
                        
                    } catch (Exception ex) {
                        throw new WebMotionException("Error during converting parameter " 
                                + name + " with value " + value 
                                + " before invoke the method", ex);
                    }
                }
            }
    }
    
    protected Object convert(Object value, Class<?> type, Type genericType) throws Exception {
        Object result = null;

        if(genericType == null) {
            genericType = type.getGenericSuperclass();
        }
        
        // Manage collection
        if(Collection.class.isAssignableFrom(type)) {

            Collection instance;
            if(type.isInterface()) {
                if(List.class.isAssignableFrom(type)) {
                    instance = new ArrayList();

                } else if(Set.class.isAssignableFrom(type)) {
                    instance = new HashSet();

                } else if(SortedSet.class.isAssignableFrom(type)) {
                    instance = new TreeSet();

                } else {
                    instance = new ArrayList();
                }
            } else {
               instance  = (Collection) type.newInstance();
            }

            Class convertType = String.class;
            if(genericType != null && genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                convertType = (Class) parameterizedType.getActualTypeArguments()[0];
            }

            if(value instanceof Map) {
                Map<?, ?> map = (Map) value;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object object = entry.getValue();
                    Object converted = convert(object, convertType, null);
                    instance.add(converted);
                }
            } else {
                Object[] tab = (Object[]) value;
                for (Object object : tab) {
                    Object converted = convert(object, convertType, null);
                    instance.add(converted);
                }
            }

            result = instance;

        // Manage map
        } else if(Map.class.isAssignableFrom(type)) {
            Map instance;
            if(type.isInterface()) {
                if(SortedMap.class.isAssignableFrom(type)) {
                    instance = new TreeMap();

                } else {
                    instance = new HashMap();
                }
            } else {
               instance  = (Map) type.newInstance();
            }

            Class convertKeyType = String.class;
            Class convertValueType = String.class;
            if(genericType != null && genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                convertKeyType = (Class) parameterizedType.getActualTypeArguments()[0];
                convertValueType = (Class) parameterizedType.getActualTypeArguments()[1];
            }

            Map<?, ?> map = (Map) value;
            for (Map.Entry entry : map.entrySet()) {
                Object mapKey = entry.getKey();
                Object mapValue = entry.getValue();

                Object convertedKey = convert(mapKey, convertKeyType, null);
                Object convertedValue = convert(mapValue, convertValueType, null);

                instance.put(convertedKey, convertedValue);
            }

            result = instance;

        // Manage simple object
        } else if(type.isArray()) {
            Class<?> componentType = type.getComponentType();
            
            if(value instanceof Map) {
                Map<?, ?> map = (Map) value;
                Object[] tabConverted = (Object[]) Array.newInstance(componentType, map.size());
                result = tabConverted;
                
                int index = 0;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object object = entry.getValue();
                    Object objectConverted = convert(object, componentType, null);
                    tabConverted[index] = objectConverted;
                    index ++;
                }
                
            } else {
                Object[] tab = (Object[]) value;
                Object[] tabConverted = (Object[]) Array.newInstance(componentType, tab.length);
                result = tabConverted;
                
                for (int index = 0; index < tab.length; index++) {
                    Object object = tab[index];
                    Object objectConverted = convert(object, componentType, null);
                    tabConverted[index] = objectConverted;
                }
            }
            
        // Manage simple object
        } else if(value instanceof Map) {
            Object instance = type.newInstance();
            
            Map<String, ?> attributes = (Map) value;
            for (Map.Entry<String, ?> attribut : attributes.entrySet()) {
                String attributName = attribut.getKey();
                Object attributeValue = attribut.getValue();
                
                Field field = type.getDeclaredField(attributName);
                Class<?> attributeType = field.getType();
                
                genericType = field.getGenericType();
                Object attributeConverted = convert(attributeValue, attributeType, genericType);
                beanUtil.setProperty(instance, attributName, attributeConverted);
            }
            
            result = instance;

        // Other simple convertion (string, string[], int, int[], ...)
        } else {
            result = converter.convert(value, type);
        }
        
        return result;
    }
    
}

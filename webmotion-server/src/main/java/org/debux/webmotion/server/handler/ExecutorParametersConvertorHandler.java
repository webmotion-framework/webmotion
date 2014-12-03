/*
 * #%L
 * Webmotion server
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

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Call.ParameterTree;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.UploadFile;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.tools.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.lang3.reflect.FieldUtils;

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
 * You can add injector in server context.
 * 
 * @author julien
 */
public class ExecutorParametersConvertorHandler extends AbstractHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersConvertorHandler.class);

    protected BeanUtilsBean beanUtil;
    protected ConvertUtilsBean converter;
    protected PropertyUtilsBean propertyUtils;
    
    @Override
    public void handlerCreated(Mapping mapping, ServerContext context) {
        beanUtil = context.getBeanUtil();
        converter = context.getConverter();
        propertyUtils = beanUtil.getPropertyUtils();
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        Executor executor = call.getCurrent();
        
        Method executorMethod = executor.getMethod();
        String[] parameterNames = ReflectionUtils.getParameterNames(mapping, executorMethod);

        // Sort parameters and convert
        ParameterTree parameterTree = call.getParameterTree();
        Map<String, List<ParameterTree>> parameterArray = parameterTree.getArray();
        Map<String, ParameterTree> parameterObject = parameterTree.getObject();
        
        Class<?>[] parameterTypes = executorMethod.getParameterTypes();
        Type[] genericParameterTypes = executorMethod.getGenericParameterTypes();
        List<String> protectedParameters = executor.getProtectedParameters();

        // Save object in call
        Map<String, Object> convertedParameters = executor.getParameters();

        for (int position = 0; position < parameterNames.length; position ++) {
            String name = parameterNames[position];
            Class<?> type = parameterTypes[position];
            Type genericType = genericParameterTypes[position];

            if (!protectedParameters.contains(name)) {
                try {
                    if (parameterArray != null) {
                        List<ParameterTree> array = parameterArray.get(name);
                        if (array != null) {
                            Object value = convert(array, type, genericType);
                            convertedParameters.put(name, value);
                        }
                    }
                    
                    if (parameterObject != null) {
                        ParameterTree object = parameterObject.get(name);
                        
                        if (object == null
                                && !Collection.class.isAssignableFrom(type)
                                && !Map.class.isAssignableFrom(type)
                                && !UploadFile.class.isAssignableFrom(type)
                                && !File.class.isAssignableFrom(type)
                                && !type.isArray()
                                && converter.lookup(type) == null) {

                            object = parameterTree;
                        }
                        
                        if (object != null) {
                            Object value = convert(object, type, genericType);
                            convertedParameters.put(name, value);
                        }
                    }

                } catch (Exception ex) {
                    throw new WebMotionException("Error during converting parameter " 
                            + name + " before invoke the method", ex);
                }
            }
        }
    }
    
    protected Object convert(List<ParameterTree> parameterTrees , Class<?> type, Type genericType) throws Exception {
        Object result = null;
        
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            
            Object[] tabConverted = (Object[]) Array.newInstance(componentType, parameterTrees.size());

            int index = 0;
            for (ParameterTree parameterTree : parameterTrees) {
                Object objectConverted = convert(parameterTree, componentType, null);
                tabConverted[index] = objectConverted;
                index ++;
            }

            result = tabConverted;
            
        } else if (Collection.class.isAssignableFrom(type)) {

            Collection instance;
            if (type.isInterface()) {
                if (List.class.isAssignableFrom(type)) {
                    instance = new ArrayList();

                } else if (Set.class.isAssignableFrom(type)) {
                    instance = new HashSet();

                } else if (SortedSet.class.isAssignableFrom(type)) {
                    instance = new TreeSet();

                } else {
                    instance = new ArrayList();
                }
            } else {
               instance = (Collection) type.newInstance();
            }

            Class convertType = String.class;
            if (genericType != null && genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                convertType = (Class) parameterizedType.getActualTypeArguments()[0];
            }

            for (ParameterTree parameterTree : parameterTrees) {
                Object converted = convert(parameterTree, convertType, null);
                instance.add(converted);
            }

            result = instance;
        }
        
        return result;
    }
    
    protected Object convert(ParameterTree parameterTree , Class<?> type, Type genericType) throws Exception {
        Object result = null;

        if (parameterTree == null) {
            return null;
        }

        if (genericType == null) {
            genericType = type.getGenericSuperclass();
        }

        Map<String, List<ParameterTree>> parameterArray = parameterTree.getArray();
        Map<String, ParameterTree> parameterObject = parameterTree.getObject();
        Object value = parameterTree.getValue();

        Converter lookup = converter.lookup(type);
        if (lookup != null) {

            // converter found, use it
            result = lookup.convert(type, value);
            return result;
        }

        // Manage enums
        if (type.isEnum()) {
            Object name = value == null ? null : ((Object[]) value)[0];
            if (name != null) {
                result = Enum.valueOf((Class<? extends Enum>) type, name.toString());
            }

        // Manage collection
        } else if (Collection.class.isAssignableFrom(type)) {

            Collection instance;
            if (type.isInterface()) {
                if (List.class.isAssignableFrom(type)) {
                    instance = new ArrayList();

                } else if (Set.class.isAssignableFrom(type)) {
                    instance = new HashSet();

                } else if (SortedSet.class.isAssignableFrom(type)) {
                    instance = new TreeSet();

                } else {
                    instance = new ArrayList();
                }
            } else {
               instance = (Collection) type.newInstance();
            }

            Class convertType = String.class;
            if (genericType != null && genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                convertType = (Class) parameterizedType.getActualTypeArguments()[0];
            }

            if (parameterObject != null) {
                for (Map.Entry<String, ParameterTree> entry : parameterObject.entrySet()) {
                    ParameterTree object = entry.getValue();
                    Object converted = convert(object, convertType, null);
                    instance.add(converted);
                }
            } else {
                Object[] tab = (Object[]) value;
                for (Object object : tab) {
                    Object converted = converter.convert(object, convertType);
                    instance.add(converted);
                }
            }

            result = instance;

        // Manage map
        } else if (Map.class.isAssignableFrom(type)) {
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
            if (genericType != null && genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                convertKeyType = (Class) parameterizedType.getActualTypeArguments()[0];
                convertValueType = (Class) parameterizedType.getActualTypeArguments()[1];
            }

            for (Map.Entry<String, ParameterTree> entry : parameterObject.entrySet()) {
                String mapKey = entry.getKey();
                ParameterTree mapValue = entry.getValue();

                Object convertedKey = converter.convert(mapKey, convertKeyType);
                Object convertedValue = convert(mapValue, convertValueType, null);

                instance.put(convertedKey, convertedValue);
            }

            result = instance;

        // Manage simple object
        } else if (type.isArray()) {
            Class<?> componentType = type.getComponentType();

            if (parameterObject != null) {
                Object[] tabConverted = (Object[]) Array.newInstance(componentType, parameterObject.size());
                result = tabConverted;

                int index = 0;
                for (Map.Entry<String, ParameterTree> entry : parameterObject.entrySet()) {
                    ParameterTree object = entry.getValue();
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
                    Object objectConverted = converter.convert(object, componentType);
                    tabConverted[index] = objectConverted;
                }
            }

        } else if (value instanceof UploadFile) {
            if (File.class.isAssignableFrom(type)) {
                UploadFile uploadFile = (UploadFile) value;
                result = uploadFile.getFile();
            } else {
                result = value;
            }

        // Manage simple object
        } else {
            Object instance = type.newInstance();
            boolean one = false;

            if (parameterObject != null) {
                for (Map.Entry<String, ParameterTree> attribut : parameterObject.entrySet()) {
                    String attributeName = attribut.getKey();
                    ParameterTree attributeValue = attribut.getValue();

                    boolean writeable = propertyUtils.isWriteable(instance, attributeName);
                    if (writeable) {
                        one = true;

                        Field field = FieldUtils.getField(type, attributeName, true);
                        Class<?> attributeType = field.getType();

                        genericType = field.getGenericType();
                        Object attributeConverted = convert(attributeValue, attributeType, genericType);
                        beanUtil.setProperty(instance, attributeName, attributeConverted);
                    }
                }
            }
            
            if (parameterArray != null) {
                for (Map.Entry<String, List<ParameterTree>> entry : parameterArray.entrySet()) {
                    String attributeName = entry.getKey();
                    List<ParameterTree> attributeValues = entry.getValue();

                    boolean writeable = propertyUtils.isWriteable(instance, attributeName);
                    if (writeable) {
                        one = true;

                        Field field = FieldUtils.getField(type, attributeName, true);
                        Class<?> attributeType = field.getType();

                        genericType = field.getGenericType();
                        Object attributeConverted = convert(attributeValues, attributeType, genericType);
                        beanUtil.setProperty(instance, attributeName, attributeConverted);
                    }
                }
            }

            if (one) {
                result = instance;

            } else {
                result = null;
            }
        }

        return result;
    }

}

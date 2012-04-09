/*
 * #%L
 * WebMotion full stack
 * 
 * $Id$
 * $HeadURL$
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
package org.debux.webmotion.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.EntityManager;
import javax.persistence.MapKey;
import javax.persistence.Parameter;
import javax.persistence.Query;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.debux.webmotion.server.WebMotionException;

/**
 *
 * @author julien
 */
public class GenericDAO {
    
    protected BeanUtilsBean beanUtil;
    protected ConvertUtilsBean convertUtils;
    protected PropertyUtilsBean propertyUtils;

    protected EntityManager manager;
    protected String entityName;
    protected Class<? extends IdentifiableEntity> entityClass;
    
    public GenericDAO(EntityManager manager, String entityName) {
        this.manager = manager;
        try {
            this.entityClass = (Class<? extends IdentifiableEntity>) Class.forName(entityName);
        } catch (ClassNotFoundException cnfe) {
            throw new WebMotionException("Invalid class name", cnfe);
        }
        
        this.beanUtil = BeanUtilsBean.getInstance();
        this.convertUtils = beanUtil.getConvertUtils();
        this.propertyUtils = beanUtil.getPropertyUtils();
    }
    
    public void create(Map<String, String[]> parameters) {
        IdentifiableEntity entity = extract(parameters);
        manager.persist(entity);
    }
            
    public void update(Map<String, String[]> parameters) {
        IdentifiableEntity entity = extract(parameters);
        manager.merge(entity);
    }
    
    public void delete(String id) {
        IdentifiableEntity entity = find(id);
        manager.remove(entity);
    }
    
    public IdentifiableEntity find(String id) {
        IdentifiableEntity entity = manager.find(entityClass, id);
        manager.detach(entity);
        return entity;
    }
    
    public List query(String name, Map<String, String[]> parameters) {
        Query query = manager.createNamedQuery(name);
        
        Set<Parameter<?>> queryParameters = query.getParameters();
        for (Parameter<?> parameter : queryParameters) {
            String parameterName = parameter.getName();
            String[] values = parameters.get(parameterName);
            
            List<String> converted = Arrays.asList(values);
            query.setParameter(parameterName, converted);
        }
        
        return query.getResultList();
    }
    
    protected IdentifiableEntity extract(Map<String, String[]> parameters) {
        try {
            IdentifiableEntity entity = entityClass.newInstance();

            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {

                String name = field.getName();
                Class<?> type = field.getType();
                String[] values = parameters.get(name);

                if (values == null) {
                    beanUtil.setProperty(entity, name, null);
                    
                } else if (type.isAnnotationPresent(javax.persistence.Entity.class)) {
                    List<Object> references = new ArrayList<Object>(values.length);
                    for (String value : values) {
                        Object reference = manager.find(type, value);
                        references.add(reference);
                    }
                    
                    Object converted = null;
                    if (List.class.isAssignableFrom(type)) {
                        converted = references;
                        
                    } else if (Set.class.isAssignableFrom(type)) {
                        converted = new HashSet<Object>(references);
                        
                    } else if (SortedSet.class.isAssignableFrom(type)) {
                        converted = new TreeSet<Object>(references);
                        
                    } else if (type.isArray()) {
                        converted = references.toArray();

                    } else if (Map.class.isAssignableFrom(type)) {
                        String keyName = "id";
                        MapKey annotation = type.getAnnotation(MapKey.class);
                        if (annotation != null) {
                            String annotationName = annotation.name();
                            if (annotationName != null && !annotationName.isEmpty()) {
                                keyName = annotationName;
                            }
                        }
                        
                        Map<Object, Object> map = new HashMap<Object, Object>();
                        for (Object object : references) {
                            Object key = propertyUtils.getProperty(object, keyName);
                            map.put(key, object);
                        }
                        converted = map;
                        
                    } else if (!references.isEmpty()) {
                        converted = references.get(0);
                    }
                    
                    beanUtil.setProperty(entity, name, converted);
                    
                } else {
                    
                    if (Collection.class.isAssignableFrom(type)) {
                        Class convertType = String.class;
                        Type genericType = field.getGenericType();
                        if(genericType != null && genericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;
                            convertType = (Class) parameterizedType.getActualTypeArguments()[0];
                        }

                        Collection<Object> collection = null;
                        if (Set.class.isAssignableFrom(type)) {
                            collection = new HashSet<Object>();
                        } else if (SortedSet.class.isAssignableFrom(type)) {
                            collection = new TreeSet();
                        } else {
                            collection = new ArrayList<Object>();
                        }
                        
                        for (Object object : values) {
                            Object convertedObject = convertUtils.convert(object, convertType);
                            collection.add(convertedObject);
                        }
                        
                        beanUtil.setProperty(entity, name, collection);
                        
                    } else if (Map.class.isAssignableFrom(type)) {
                        throw new UnsupportedOperationException("Map is not supported, you must create a specific entity.");
                        
                    } else {
                        Object converted = convertUtils.convert(values, type);
                        beanUtil.setProperty(entity, name, converted);
                    }
                }
            }

            return entity;
            
        } catch (InstantiationException ie) {
            throw new WebMotionException("Not default constructor", ie);
        } catch (IllegalAccessException iae) {
            throw new WebMotionException("Error during create instance", iae);
        } catch (InvocationTargetException ite) {
            throw new WebMotionException("Error during set field on instance", ite);
        } catch (NoSuchMethodException nsme) {
            throw new WebMotionException("Error during set field on instance", nsme);
        }
    }
    
}

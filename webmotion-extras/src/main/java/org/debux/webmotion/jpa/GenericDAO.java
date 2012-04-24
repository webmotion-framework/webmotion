/*
 * #%L
 * WebMotion extras
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
import org.apache.commons.lang3.ArrayUtils;
import org.debux.webmotion.server.WebMotionException;

/**
 * The class is a generic DAO. It is used to CRUD operations on specific class.
 * The DAO use JPA specification. In oder to create a generic DAO on entity, you
 * must extends @see IdentifiableEntity.
 * 
 * @author julien
 */
public class GenericDAO {
    
    /** Utility on bean */
    protected BeanUtilsBean beanUtil;
    
    /** Utility to convert value */
    protected ConvertUtilsBean convertUtils;
    
    /** Utility on property */
    protected PropertyUtilsBean propertyUtils;

    /** Current entity manager */
    protected EntityManager manager;

    /** Current entity class do crud */
    protected Class<? extends IdentifiableEntity> entityClass;
    
    /**
     * Constructor with direct entity class.
     * 
     * @param manager entity manager
     * @param entityClass entity class
     */
    public GenericDAO(EntityManager manager, Class<? extends IdentifiableEntity> entityClass) {
        this.manager = manager;
        this.entityClass = entityClass;
        
        this.beanUtil = BeanUtilsBean.getInstance();
        this.convertUtils = beanUtil.getConvertUtils();
        this.propertyUtils = beanUtil.getPropertyUtils();
    }
    
    /**
     * Constructor with entity class as string.
     * 
     * @param manager entity manager
     * @param entityName entity class as string
     */
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
    
    /**
     * Helper to create dynamic parameter for the GenericDAO. Thus you can pass 
     * parameters easily from the http request or from code.<br/>
     * 
     * By default the parameters come from the http request, which are 
     * represent with a Map. The key represents the name of parameter and the 
     * object array represent the values.
     */
    public static class Parameters {
        
        /** All values */
        protected Map<String, Object[]> parameters;

        /**
         * Constructor by default
         */
        public Parameters() {
            this(new HashMap<String, Object[]>());
        }

        /**
         * Constructor with the parameters.
         * 
         * @param parameters parameters
         */
        public Parameters(Map<String, Object[]> parameters) {
            this.parameters = parameters;
        }
        
        /**
         * @return empty Parameters object
         */
        public static Parameters create() {
            return new Parameters();
        }
        
        /**
         * @return Parameters object the parameters
         */
        public static Parameters create(Map values) {
            return new Parameters(values);
        }
        
        /**
         * Add all parameters.
         * 
         * @param parameters parameters to add
         * @return Parameters object
         */
        public Parameters addAll(Map parameters) {
            this.parameters.putAll(parameters);
            return this;
        }
        
        /**
         * Add one parameter with all values.
         * 
         * @param name parameter name
         * @param values parameter values
         * @return Parameters object
         */
        public Parameters add(String name, Object[] values) {
            this.parameters.put(name, values);
            return this;
        }
        
        /**
         * Add one parameter with null value.
         * 
         * @param name parameter name
         * @return Parameters object
         */
        public Parameters add(String name) {
            this.parameters.put(name, new Object[]{});
            return this;
        }
        
        /**
         * Add one parameter to complete with a value.
         * 
         * @param name parameter name
         * @param value parameter value
         * @return Parameters object
         */
        public Parameters add(String name, Object value) {
            Object[] values = this.parameters.get(name);
            if (values == null) {
                values = new Object[]{value};
                
            } else {
                values = ArrayUtils.add(values, value);
            }
            
            this.parameters.put(name, values);
            return this;
        }

        /**
         * @return all parameters
         */
        public Map<String, Object[]> getParameters() {
            return parameters;
        }
        
        /**
         * @param name parameter name
         * @return parameter values
         */
        public Object[] get(String name) {
            return parameters.get(name);
        }
    }
    
    /**
     * Create an entity with parameters.
     * 
     * @param parameters parameters
     * @return entity created.
     */
    public IdentifiableEntity create(Parameters parameters) {
        IdentifiableEntity entity = extract(parameters);
        manager.persist(entity);
        return entity;
    }
            
    /**
     * Update an entity with parameters, that is identified by this id.
     * 
     * @param id identifier
     * @param parameters parameters
     * @return entity updated
     */
    public IdentifiableEntity update(String id, Parameters parameters) {
        IdentifiableEntity entity = manager.find(entityClass, id);
        if (entity != null) {
            entity = extract(entity, parameters);
        }
        return entity;
    }
    
    /**
     * Delete an entity, that is identified by this id.
     * 
     * @param id identifier
     * @return true if is deleted else false if the entity was not found.
     */
    public boolean delete(String id) {
        IdentifiableEntity entity = manager.find(entityClass, id);
        if (entity != null) {
            manager.remove(entity);
            return true;
        }
        return false;
    }
    
    /**
     * Find entity by the id.
     * 
     * @param id identifier
     * @return 
     */
    public IdentifiableEntity find(String id) {
        IdentifiableEntity entity = manager.find(entityClass, id);
        return entity;
    }
    
    /**
     * Execute a query named as read in entity.
     * 
     * @param name query name
     * @param parameters parameters
     * @return outturn
     */
    public List query(String name, Parameters parameters) {
        Query query = manager.createNamedQuery(name);
        extract(query, parameters);
        List list = query.getResultList();
        return list;
    }

    /**
     * Execute a query named as write in entity.
     * 
     * @param name query name
     * @param parameters parameters
     * @return the number of entities updated or deleted 
     */
    public int exec(String name, Parameters parameters) {
        Query query = manager.createNamedQuery(name);
        extract(query, parameters);
        int executeUpdate = query.executeUpdate();
        return executeUpdate;
    }
    
    /**
     * Set parameter in the query.
     * 
     * @param query query
     * @param parameters parameters
     */
    protected void extract(Query query, Parameters parameters) {
        Set<Parameter<?>> queryParameters = query.getParameters();
        for (Parameter<?> parameter : queryParameters) {
            String parameterName = parameter.getName();
            Object[] values = parameters.get(parameterName);
            
            List<Object> converted = Arrays.asList(values);
            query.setParameter(parameterName, converted);
        }
    }
    
    /**
     * Create a new entity with parameters.
     * 
     * @param parameters parameters
     * @return entity with parameters
     */
    protected IdentifiableEntity extract(Parameters parameters) {
        try {
            IdentifiableEntity entity = entityClass.newInstance();
            return extract(entity, parameters);
            
        } catch (IllegalAccessException iae) {
            throw new WebMotionException("Error during create instance", iae);
        } catch (InstantiationException ie) {
            throw new WebMotionException("Not default constructor", ie);
        }
    }
    
    /**
     * Complete entity with parameters. Try to convert parameter, if the type not 
     * match. To create an association on entity, you must pass only identifier.
     * 
     * @param entity entity
     * @param parameters parameters
     * @return entity completed
     */
    protected IdentifiableEntity extract(IdentifiableEntity entity, Parameters parameters) {
        try {
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {

                String name = field.getName();
                Class<?> type = field.getType();
                Object[] values = parameters.get(name);

                // The identifier can't be set
                if (!IdentifiableEntity.ATTRIBUTE_NAME_ID.equals(name) && values != null) {
                    
                    if (values.length == 0) {
                        // Null value
                        beanUtil.setProperty(entity, name, null);

                    } else if (type.isAnnotationPresent(javax.persistence.Entity.class)) {
                        // Association
                        List<Object> references = new ArrayList<Object>(values.length);
                        for (Object value : values) {
                            Object reference = manager.find(type, value);
                            if (reference != null) {
                                references.add(reference);
                            }
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
                            String keyName = IdentifiableEntity.ATTRIBUTE_NAME_ID;
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
                        // Basic object
                        if (Collection.class.isAssignableFrom(type)) {
                            Class convertType = String.class;
                            Type genericType = field.getGenericType();
                            if (genericType != null && genericType instanceof ParameterizedType) {
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
            }

            return entity;
            
        } catch (IllegalAccessException iae) {
            throw new WebMotionException("Error during create instance", iae);
        } catch (InvocationTargetException ite) {
            throw new WebMotionException("Error during set field on instance", ite);
        } catch (NoSuchMethodException nsme) {
            throw new WebMotionException("Error during set field on instance", nsme);
        }
    }
    
}

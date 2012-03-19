/*
 * #%L
 * WebMotion JPA
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
import java.util.Map;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Manager extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Manager.class);

    protected BeanUtilsBean beanUtil;
    protected ConvertUtilsBean convertUtils;
    
    public Manager() {
        beanUtil = BeanUtilsBean.getInstance();
        convertUtils = beanUtil.getConvertUtils();
    }
    
    public void create(EntityManager manager, HttpServletRequest request, String entityName) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        Object entity = extractEntity(manager, entityName, parameters);
        manager.persist(entity);
    }

    public Render read(EntityManager manager, String entityName, String id) throws Exception {
        Object entity = findEntity(manager, entityName, id);
        manager.detach(entity);
        return renderJSON(entity);
    }
    
    public void update(EntityManager manager, HttpServletRequest request, String entityName) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        Object entity = extractEntity(manager, entityName, parameters);
        manager.merge(entity);
    }
    
    public void delete(EntityManager manager, String entityName, String id) throws Exception {
        Object entity = findEntity(manager, entityName, id);
        manager.remove(entity);
    }
    
    protected Object extractEntity(EntityManager manager, String entityName,  Map<String, String[]> parameters) throws Exception {
        Class<?> entityClass = Class.forName(entityName);
        Object entity = entityClass.newInstance();
        
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            
            if ("action".equals(name)) {
                continue;
            }
            
            Field field = entityClass.getDeclaredField(name);
            Class<?> type = field.getType();
            
            if (type.isAnnotationPresent(javax.persistence.Entity.class)) {
                for (String value : values) {
                    manager.find(type, value);
                    beanUtil.setProperty(entity, name, value);
                }
                
            } else {
                Object converted = convertUtils.convert(values, type);
                beanUtil.setProperty(entity, name, converted);
            }
        }
        
        return entity;
    }
    
    protected Object findEntity(EntityManager manager, String entityName, String id) throws Exception {
        Class<?> entityClass = Class.forName(entityName);
        return manager.find(entityClass, id);
    }
}

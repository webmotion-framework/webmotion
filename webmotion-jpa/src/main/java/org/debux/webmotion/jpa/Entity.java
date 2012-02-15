package org.debux.webmotion.jpa;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entity extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Entity.class);

    protected BeanUtilsBean beanUtil;
    protected ConvertUtilsBean convertUtils;
    
    public Entity() {
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

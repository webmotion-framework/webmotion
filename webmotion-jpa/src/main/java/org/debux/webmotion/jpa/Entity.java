package org.debux.webmotion.jpa;

import java.lang.reflect.Field;
import java.util.Enumeration;
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
        Object entity = extractEntity(manager, entityName, request);
        manager.persist(entity);
    }

    public Render read(EntityManager manager, HttpServletRequest request, String entityName) throws Exception {
        Object entity = findEntity(manager, request, entityName, null);
        manager.detach(entity);
        return renderJSON(entity);
    }
    
    public void update(EntityManager manager, HttpServletRequest request, String entityName) throws Exception {
        Object entity = extractEntity(manager, entityName, request);
        manager.merge(entity);
    }
    
    public void delete(EntityManager manager, HttpServletRequest request, String entityName) throws Exception {
        Object entity = findEntity(manager, request, entityName, null);
        manager.remove(entity);
    }
    
    protected Object extractEntity(EntityManager manager, String entityName, HttpServletRequest request) throws Exception {
        Class<?> forName = Class.forName(entityName);
        Object entity = forName.newInstance();
        
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            
            String fieldName = name.split("\\.")[0];
            Field field = forName.getDeclaredField(fieldName);
            Class<?> type = field.getType();
            
            if (type.isAnnotationPresent(javax.persistence.Entity.class)) {
                Object reference = findEntity(manager, request, type.getName(), fieldName);
                beanUtil.setProperty(entity, fieldName, reference);
                
            } else {
                Object converted = convertUtils.convert(value, type);
                beanUtil.setProperty(entity, name, converted);
            }
        }
        
        return entity;
    }
    
    protected Object findEntity(EntityManager manager, HttpServletRequest request, String entityName, String prefix) throws Exception {
        String jql = "select * from " + entityName;
        String separator = " where ";
        
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);

            if (prefix == null || name.startsWith(prefix + ".")) {
                String fieldName = name.split("\\.")[0];
                jql += separator + fieldName + " = :" + fieldName;
                separator = " and ";
            }
        }
        
        Query query = manager.createQuery(jql);
        
        parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            
            if (prefix == null || name.startsWith(prefix + ".")) {
                String fieldName = name.split("\\.")[0];
                query.setParameter(fieldName, value);
            }
        }
        
        return query.getSingleResult();
    }
}

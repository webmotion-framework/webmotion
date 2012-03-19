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
import javax.persistence.EntityManager;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Display extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Display.class);
    
    protected BeanUtilsBean beanUtil;
    
    public Display() {
        beanUtil = BeanUtilsBean.getInstance();
    }
    
    protected Object findEntity(EntityManager manager, String entityName, String id) throws Exception {
        Class<?> entityClass = Class.forName(entityName);
        return manager.find(entityClass, id);
    }
        
    public Render json(EntityManager manager, String entityName, String id) throws Exception {
        Object entity = findEntity(manager, entityName, id);
        manager.detach(entity);
        
        return renderJSON(entity);
    }
    
    public Render content(EntityManager manager, String entityName, String id) throws Exception {
        Object entity = findEntity(manager, entityName, id);
        manager.detach(entity);
        
        String content = "";
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            String value = beanUtil.getProperty(entity, name);
            content += "<div>" + name + " : " + value + "</div>\n";
        }
        
        return renderContent(content, "text/html");
    }
    
}

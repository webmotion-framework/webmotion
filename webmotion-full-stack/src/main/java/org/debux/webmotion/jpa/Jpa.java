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

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.jpa.GenericDAO.Parameters;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class to manage entity with JPA.
 * 
 * @author julien
 */
public class Jpa extends Transactional {

    private static final Logger log = LoggerFactory.getLogger(Jpa.class);

    public Render all(GenericDAO dao, HttpServletRequest request, String action,
            String id, String name, String forward) {
        
        if ("create".equals(action)) {
            return create(dao, request, forward);
            
        } else if ("find".equals(action)) {
            return find(dao, request, forward, id);
            
        } else if ("query".equals(action)) {
            return query(dao, request, forward, name);
            
        } else if ("update".equals(action)) {
            return update(dao, request, forward, id);
            
        } else if ("delete".equals(action)) {
            return delete(dao, request, forward, id);
        }
        
        return null;
    }
    
    public Render create(GenericDAO dao, HttpServletRequest request, String forward) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);
        
        IdentifiableEntity entity = dao.create(parameters);
        return render(forward, "entity", entity);
    }

    public Render find(GenericDAO dao, HttpServletRequest request, String forward, String id) {
        IdentifiableEntity entity = dao.find(id);
        return render(forward, "entity", entity);
    }
    
    public Render query(GenericDAO dao, HttpServletRequest request, String forward, String name) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);

        List query = dao.query(name, parameters);
        return render(forward, "queryResult", query);
    }
    
    public Render update(GenericDAO dao, HttpServletRequest request, String forward, String id) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);
        
        IdentifiableEntity entity = dao.update(id, parameters);
        return render(forward, "entity", entity);
    }
    
    public Render delete(GenericDAO dao, HttpServletRequest request, String forward, String id) {
        boolean deleted = dao.delete(id);
        return render(forward, "deleted", deleted);
    }

    protected Render render(String forward, String resultName, Object resultValue) {
        if (forward != null && !forward.isEmpty()) {
            return renderActionURL(forward, null, new Object[]{resultName, resultValue});
        } else {
            return renderJSON(resultValue);
        }
    }
}

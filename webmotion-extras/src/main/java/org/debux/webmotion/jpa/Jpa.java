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

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.jpa.GenericDAO.Parameters;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This controller manages the entity. All actions can call a callback after 
 * to execute, by default return a JSON object.
 * 
 * @author julien
 */
public class Jpa extends Transactional {

    private static final Logger log = LoggerFactory.getLogger(Jpa.class);

    /**
     * Manage all action. The parameter action is used to determine the action 
     * to execute.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param action action to do (create, find, query, update, delete)
     * @param id entity identifier
     * @param name query name
     * @param callback callback
     * @return callback or JSON
     */
    public Render all(GenericDAO dao, HttpServletRequest request, String action,
            String id, String name, String callback) {
        
        if ("create".equals(action)) {
            return create(dao, request, callback);
            
        } else if ("find".equals(action)) {
            return find(dao, request, callback, id);
            
        } else if ("query".equals(action)) {
            return query(dao, request, callback, name);
            
        } else if ("update".equals(action)) {
            return update(dao, request, callback, id);
            
        } else if ("delete".equals(action)) {
            return delete(dao, request, callback, id);
        }
        
        return null;
    }
    
    /**
     * Create an entity with request parameters.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param callback callback
     * @return callback with attribute entity or JSON
     */
    public Render create(GenericDAO dao, HttpServletRequest request, String callback) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);
        
        IdentifiableEntity entity = dao.create(parameters);
        return render(callback, "entity", entity);
    }

    /**
     * Find an entity by an identifier.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param callback callback
     * @param id identifier
     * @return callback with attribute entity or JSON
     */
    public Render find(GenericDAO dao, HttpServletRequest request, String callback, String id) {
        IdentifiableEntity entity = dao.find(id);
        return render(callback, "entity", entity);
    }
    
    /**
     * Execute the query with as parameter the request parameter and identify by 
     * a name.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param callback callback
     * @param name query name
     * @return callback with attribute queryResult or JSON
     */
    public Render query(GenericDAO dao, HttpServletRequest request, String callback, String name) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);

        List query = dao.query(name, parameters);
        return render(callback, "queryResult", query);
    }
    
    /**
     * Update an entity with request parameter and identify by an identifier.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param callback callback
     * @param id identifier
     * @return callback with attribute entity or JSON
     */
    public Render update(GenericDAO dao, HttpServletRequest request, String callback, String id) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);
        
        IdentifiableEntity entity = dao.update(id, parameters);
        return render(callback, "entity", entity);
    }
    
    /**
     * Delete an entity identify by an identifier.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param callback callback
     * @param id identifier
     * @return callback with attribute deleted or JSON
     */
    public Render delete(GenericDAO dao, HttpServletRequest request, String callback, String id) {
        boolean deleted = dao.delete(id);
        return render(callback, "deleted", deleted);
    }

    /**
     * @param callback url callback
     * @param resultName attribute name to put into the request
     * @param resultValue attribute value to put into the request
     * @return renderActionURL if the callback is not null else renderJSON
     */
    protected Render render(String callback, String resultName, Object resultValue) {
        if (callback != null && !callback.isEmpty()) {
            return renderActionURL(callback, null, new Object[]{resultName, resultValue});
        } else {
            return renderJSON(resultValue);
        }
    }
}

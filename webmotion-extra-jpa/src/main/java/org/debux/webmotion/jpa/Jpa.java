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
            
        } else if ("exec".equals(action)) {
            return exec(dao, request, callback, name);
            
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
     * Execute the query as read with as parameter the request parameter and 
     * identify by a name.
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
     * Execute the query as write with as parameter the request parameter and 
     * identify by  a name.
     * 
     * @param dao generic dao injected
     * @param request http request injected
     * @param callback callback
     * @param name query name
     * @return callback with attribute queryResult or JSON
     */
    public Render exec(GenericDAO dao, HttpServletRequest request, String callback, String name) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Parameters parameters = Parameters.create(parameterMap);

        int query = dao.exec(name, parameters);
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
            return renderForward(callback, null, new Object[]{resultName, resultValue});
        } else {
            return renderJSON(resultValue);
        }
    }
}

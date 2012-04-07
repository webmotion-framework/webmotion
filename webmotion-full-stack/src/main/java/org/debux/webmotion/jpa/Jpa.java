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
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jpa extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Jpa.class);

    public void all(GenericDAO dao, HttpServletRequest request, String action) {
    }
    
    public void create(GenericDAO dao, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        dao.create(parameters);
    }

    public Render read(GenericDAO dao, String id) {
        IdentifiableEntity entity = dao.find(id);
        return renderJSON(entity);
    }
    
    public Render query(GenericDAO dao, String name) {
        List query = dao.query(name);
        return renderJSON(query);
    }
    
    public void update(GenericDAO dao, HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        dao.update(parameters);
    }
    
    public void delete(GenericDAO dao, String id) {
        dao.delete(id);
    }

}

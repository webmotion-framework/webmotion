/*
 * #%L
 * Webmotion in action
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Debux
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
package org.debux.webmotion.server.render;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to call RenderXml or RenderJson depending the accept in request 
 * header.
 * 
 * @author julien
 */
public class RenderAccept extends Render {
    protected Map<String, Object> model;

    public RenderAccept(Map<String, Object> model) {
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        // Get accept in header
        HttpContext context = call.getContext();
        HttpServletRequest request = context.getRequest();
        String accept = request.getHeader("Accept");
        
        Render render = null;
        if (accept.equals("application/json")) {
            // Delegate to renderJSON
            render = new RenderJson(model);
        } else if (accept.equals("text/xml")) {
            // Delegate to renderXML
            render = new RenderXml(model);
        } else {
            throw new IllegalArgumentException("Invalid accept");
        }
        
        // Create the real render
        render.create(mapping, call);
    }
    
}

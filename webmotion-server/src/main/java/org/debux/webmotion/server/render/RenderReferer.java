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
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to reload the current page with the referer in request header.
 * 
 * @author julien
 */
public class RenderReferer extends Render {
    protected Map<String, Object> model;

    public RenderReferer(Map<String, Object> model) {
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        RenderReferer render = (RenderReferer) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String path = context.getHeader(HttpContext.HEADER_REFERER);
        if (path == null) {
            throw new WebMotionException("The header not contains the referer value", call.getRule());
        }
        path = addModel(path, model);
        response.sendRedirect(path);
    }
    
}

/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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

import static org.debux.webmotion.server.WebMotionServer.PATH_DEPLOY;
import static org.debux.webmotion.server.WebMotionServer.PATH_STATIC;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to redirect the user to an absolute url or relatif.
 * 
 * @author julien
 */
public class RenderRedirect extends Render {
    protected String url;
    protected Map<String, Object> model;

    public RenderRedirect(String url, Map<String, Object> model) {
        this.url = url;
        this.model = model;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        if (url.startsWith("/")) {
            if (!url.startsWith(PATH_STATIC) && !url.startsWith(PATH_DEPLOY)) {
                url = context.getExtensionPath() + url;
            }
            url = request.getContextPath() + url;
        }
        
        String path = addModel(url, model);
        response.sendRedirect(path);
    }
    
}

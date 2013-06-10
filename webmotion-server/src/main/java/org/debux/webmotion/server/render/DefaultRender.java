/*
 * #%L
 * Webmotion server
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

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to define a default render for the method in controller which return 
 * an object either a render object. The DefautRender get a model to use the object 
 * returned by the method to make the render. By defaut return the result to json object.
 * 
 * @author julien
 */
public class DefaultRender<T> extends Render {
    
    protected T model;

    public DefaultRender(T model) {
        this.model = model;
    }

    public T getModel() {
        return model;
    }
    
    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();

        response.setContentType("application/json");
        
        Gson gson = new Gson();
        String json = gson.toJson(model);
        PrintWriter out = context.getOut();
        out.print(json);
    }
    
}

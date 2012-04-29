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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * Render use StringTemplate to create the response. Your template must contains
 * a group like <pre>render(model) ::= << ... >></pre>. The delimiters is '$'.
 * 
 * @author julien
 */
public class RenderStringTemplate extends Render {
    protected String fileName;
    protected String mimeType;
    protected Map<String, Object> model;

    public RenderStringTemplate(String fileName, String mimeType, Map<String, Object> model) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.model = model;
    }
    
    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        
        STGroup group = new STGroupFile(fileName, '$', '$');
        ST template = group.getInstanceOf("render");
        template.add("model", model);
        
        response.setContentType(mimeType);
        
        String templateResult = template.render();
        PrintWriter out = context.getOut();
        out.print(templateResult);
    }
    
}

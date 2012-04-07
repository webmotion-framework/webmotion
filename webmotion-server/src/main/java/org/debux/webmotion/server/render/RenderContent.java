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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to return a content with a mime type and encoding.
 * 
 * @author julien
 */
public class RenderContent extends Render {
    protected String content;
    protected String mimeType;
    protected String encoding;

    public RenderContent(String content, String mimeType, String encoding) {
        this.content = content;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    public RenderContent(String content, String mimeType) {
        this(content, mimeType, DEFAULT_ENCODING);
    }

    public String getContent() {
        return content;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        
        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        response.setCharacterEncoding(encoding);
        PrintWriter out = context.getOut();
        out.print(content);
    }
    
}

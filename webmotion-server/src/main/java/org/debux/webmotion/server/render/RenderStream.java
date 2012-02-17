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
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to write a stream in response, like a file, ...
 * 
 * @author julien
 */
public class RenderStream extends Render {
    protected InputStream stream;
    protected String mimeType;
    protected String encoding;
    protected String name;

    public RenderStream(InputStream stream, String name, String mimeType, String encoding) {
        this.stream = stream;
        this.name = name;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        RenderStream render = (RenderStream) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        
        if (encoding == null) {
            response.setCharacterEncoding(encoding);
        } else {
            response.setCharacterEncoding(DEFAULT_ENCODING);
        }
        
        if (name != null) {
            response.setHeader("Content-Transfer-Encoding", mimeType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
        }
        
        InputStream inputStream = render.getStream();
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
}

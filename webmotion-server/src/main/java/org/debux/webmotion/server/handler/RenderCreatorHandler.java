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
package org.debux.webmotion.server.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.FileProgressListener;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.InitContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Render creator do the render for user. Apply the good redirect, foward or 
 * include on response.
 * 
 * @author jruchaud
 */
public class RenderCreatorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(RenderCreatorHandler.class);

    @Override
    public void init(InitContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        try {
            Render render = call.getRender();
            log.info("Render = " + render);
            if(render == null) {
                return;
            }
            render.create(mapping, call);
            
        } catch (IOException ioe) {
            throw new WebMotionException("Error during write the render in response", ioe);
            
        } catch (ServletException se) {
            throw new WebMotionException("Error on server when write the render in response", se);
        }

        if(call.isFileUploadRequest()) {
            HttpContext context = call.getContext();
            HttpSession session = context.getSession();
            if(session != null) {
                session.removeAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
            }
        }
    }
}

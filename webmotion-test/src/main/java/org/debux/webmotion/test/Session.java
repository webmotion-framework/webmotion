/*
 * #%L
 * WebMotion test
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
package org.debux.webmotion.test;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.ClientSession;
import org.debux.webmotion.server.render.Render;

/**
 * Test client session.
 * 
 * @author julien
 */
public class Session extends WebMotionController {
    
    public Render store(ClientSession session, String value) {
        session.setAttribute("name", value);
        return renderURL("/session/read");
    }
    
    public Render read(ClientSession session) {
        String attribute = (String) session.getAttribute("name");
        return renderContent(attribute, "text/html");
    }
}

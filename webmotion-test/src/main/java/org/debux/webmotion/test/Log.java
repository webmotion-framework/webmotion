/*
 * #%L
 * Webmotion website
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
package org.debux.webmotion.test;

import java.io.IOException;
import java.io.PrintWriter;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;

/**
 * Log
 * 
 * @author julien
 */
public class Log extends WebMotionController {
    
    public Render info() throws IOException {
        HttpContext context = getContext();
        PrintWriter out = context.getOut();
        out.println("Action log");
        
        return renderContent("Action render\n", "text/plain");
    }
    
}

/*
 * #%L
 * Webmotion website
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
package org.debux.webmotion.test;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.mapping.Properties;
import org.debux.webmotion.server.render.Render;

/**
 * Read properties.
 * 
 * @author julien
 */
public class Echo extends WebMotionController {
    
    public Render run(Properties config) {
        int number = config.getInt("number");
        String text = config.getString("text");
        
        String echo = "";
        for (int i = 0; i < number; i++) {
            echo += "<strong>" + text + "</strong><br/>\n";
        }
        return renderContent(echo, "text/html");
    }
    
}

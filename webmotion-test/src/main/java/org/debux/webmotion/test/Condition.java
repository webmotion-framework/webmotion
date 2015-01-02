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

import java.io.IOException;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.render.Render;

/**
 * Condition on doProcess
 * 
 * @author julien
 */
public class Condition extends WebMotionFilter {
    
    public Render check(int number) throws IOException {
        if(number >= 5) {
            // continue if the number is great than 5
            doProcess();
            return null;
            
        } else {
            // else stop execution
            return renderContent("Invalid number " + number, "text/plain");
        }
    }
    
}

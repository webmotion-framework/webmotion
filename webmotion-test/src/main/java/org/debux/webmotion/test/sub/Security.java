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
package org.debux.webmotion.test.sub;

import java.io.IOException;
import java.io.PrintWriter;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.convention.WebMotionConventionPackageFilter;
import org.debux.webmotion.server.render.Render;

/**
 * Convention filter in package
 * 
 * @author julien
 */
public class Security extends WebMotionConventionPackageFilter {
    
    @Override
    public Render filter() {
        try {
            
            HttpContext context = getContext();
            PrintWriter out = context.getOut();
            out.println("Package security filter");
            
            doProcess();
            return null;
            
        } catch (IOException ex) {
            throw new WebMotionException("Filter convention", ex);
        }
    }
    
}

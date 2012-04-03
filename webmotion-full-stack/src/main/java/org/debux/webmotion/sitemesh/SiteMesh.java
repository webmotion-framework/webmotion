/*
 * #%L
 * WebMotion full stack
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
package org.debux.webmotion.sitemesh;

import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.WebMotionFilter;

/**
 * Put the layouts in request attribute. Use by the filter to decorate page.
 * 
 * @author julien
 */
public class SiteMesh extends WebMotionFilter {
    
    public static final String LAYOUTS = "sitemesh_layouts";
    
    public void decorate(HttpServletRequest request, String[] layouts) {
        request.setAttribute(LAYOUTS, layouts);
    }
    
}

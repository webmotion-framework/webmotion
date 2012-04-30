/*
 * #%L
 * WebMotion extras
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
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Put the layouts in request attribute. Use by the filter to decorate page.
 * 
 * @author julien
 */
public class SiteMesh extends WebMotionFilter {
    
    /** Attribute name to store the layout to decorate the page */
    public static final String LAYOUTS = "sitemesh_layouts";
    
    /**
     * Set the layout into the request. Pass on the filter or the action. If 
     * the layout is null or empty not layout is passed to SiteMesh.
     * 
     * @param request set layout into the request
     * @param layout the layout to apply on the view
     */
    public void decorate(HttpServletRequest request, String layout) {
        
        if (layout == null || layout.isEmpty()) {
            request.setAttribute(LAYOUTS, null);
            
        } else {
            Mapping mapping = contextable.getMapping();
            Config config = mapping.getConfig();

            String path = "/" + layout;
            String packageName = config.getPackageViews().replaceAll("\\.", "/");
            if (packageName != null && !packageName.isEmpty()) {
                path = "/" + packageName + path;
            }

            request.setAttribute(LAYOUTS, new String[]{path});
        }
        
        doProcess();
    }
    
}

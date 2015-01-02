/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server.convention;

import org.debux.webmotion.server.render.*;
import java.util.Map;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render by convention use to forward a new page, like jsp, html, text, ...
 * 
 * @author julien
 */
public class ConventionRenderView extends RenderView {

    public ConventionRenderView(String view, Map<String, Object> model) {
        super(view, model);
    }

    @Override
    protected String getViewPath(Mapping mapping, Call call, String view) {
        Class<? extends WebMotionController> clazz = call.getExecutor().getClazz();
        String path = "/WEB-INF/";
        
        Package classPackage = clazz.getPackage();
        if (classPackage != null) {
            path += classPackage.getName().replaceAll("\\.", "/");
        }
        
        path += "/" + view;
        
        return path;
    }

}

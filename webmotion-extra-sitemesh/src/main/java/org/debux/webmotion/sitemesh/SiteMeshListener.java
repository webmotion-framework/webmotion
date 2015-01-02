/*
 * #%L
 * WebMotion extra sitemesh
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
package org.debux.webmotion.sitemesh;

import java.io.IOException;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.config.PathBasedDecoratorSelector;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add configuration to use SiteMesh, just add SiteMesh add global filter.
 * 
 * @author julien
 */
public class SiteMeshListener implements WebMotionServerListener {

    private static final Logger log = LoggerFactory.getLogger(SiteMeshListener.class);

    /** Current filter */
    protected static Filter filter = new ConfigurableSiteMeshFilter() {
        @Override
        protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
            builder.setCustomDecoratorSelector(new PathBasedDecoratorSelector<WebAppContext>() {

                @Override
                public String[] selectDecoratorPaths(Content content, WebAppContext siteMeshContext) throws IOException {
                    // Search the filter into the attibutes of the request
                    HttpServletRequest request = siteMeshContext.getRequest();
                    String[] layouts = (String[]) request.getAttribute(SiteMesh.LAYOUTS);
                    if (layouts != null) {
                        return layouts;
                    } else {
                        // Else use SiteMesh in classic mode 
                        String[] selectDecoratorPaths = super.selectDecoratorPaths(content, siteMeshContext);
                        return selectDecoratorPaths;
                    }
                }
            });
        }
    };
            
    @Override
    public void onStart(Mapping mapping, ServerContext context) {
        // Add filter into webapp
        ServletContext servletContext = context.getServletContext();
        FilterRegistration registration = servletContext.addFilter("sitemesh", filter);
        if (registration != null) {
            registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD, DispatcherType.INCLUDE), true, "/*");
        }

        context.addGlobalController(SiteMesh.class);
    }

    @Override
    public void onStop(ServerContext context) {
        // Do nothing
    }

}

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

import java.io.IOException;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.config.PathBasedDecoratorSelector;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.WebAppContext;

/**
 * Redifined a ConfigurableSiteMeshFilter to find decorate layout in request. The
 * value can be pass in request with a WebMotion filter. Use a listener to create 
 * filter to declare after WebMotionFilter with addMappingForUrlPatterns.
 * 
 * @author julien
 */
@WebListener
public class SiteMeshListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        FilterRegistration registration = servletContext.addFilter("sitemesh", 
                new ConfigurableSiteMeshFilter() {
                    @Override
                    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
                        builder.setCustomDecoratorSelector(new PathBasedDecoratorSelector<WebAppContext>() {

                            @Override
                            public String[] selectDecoratorPaths(Content content, WebAppContext siteMeshContext) throws IOException {
                                HttpServletRequest request = siteMeshContext.getRequest();
                                String[] layouts = (String[]) request.getAttribute(SiteMesh.LAYOUTS);
                                if (layouts != null) {
                                    return layouts;

                                } else {

                                    String[] selectDecoratorPaths = super.selectDecoratorPaths(content, siteMeshContext);
                                    return selectDecoratorPaths;
                                }
                            }
                        });
                    }
                });
        registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
    
}
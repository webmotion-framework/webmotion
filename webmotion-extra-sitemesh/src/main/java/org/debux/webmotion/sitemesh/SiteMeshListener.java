package org.debux.webmotion.sitemesh;

import java.io.IOException;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
public class SiteMeshListener implements ServletContextListener {

    /** Current SiteMesh filter */
    protected ConfigurableSiteMeshFilter filter;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (filter == null) {
            // Create the filter
            filter = new ConfigurableSiteMeshFilter() {
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
            
            // Add filter into webapp
            ServletContext servletContext = event.getServletContext();
            FilterRegistration registration = servletContext.addFilter("sitemesh", filter);
            registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD, DispatcherType.INCLUDE), true, "/*");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Do nothing
    }
    
}

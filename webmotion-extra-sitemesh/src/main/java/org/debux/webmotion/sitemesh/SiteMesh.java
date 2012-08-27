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

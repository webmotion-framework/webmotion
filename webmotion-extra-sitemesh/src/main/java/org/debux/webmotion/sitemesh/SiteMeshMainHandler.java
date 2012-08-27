package org.debux.webmotion.sitemesh;

import org.debux.webmotion.server.WebMotionMainHandler;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add configuration to use SiteMesh, just add SiteMesh add global filter.
 * 
 * @author julien
 */
public class SiteMeshMainHandler extends WebMotionMainHandler {

    private static final Logger log = LoggerFactory.getLogger(SiteMeshMainHandler.class);

    @Override
    protected void initHandlers(Mapping mapping, ServerContext context) {
        super.initHandlers(mapping, context);
        
        context.addGlobalController(SiteMesh.class);
    }

}

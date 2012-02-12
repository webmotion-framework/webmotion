package org.debux.webmotion.jpa;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entity extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Entity.class);
    
    public Render index() {
        return renderView("index.jsp");
    }
    
}

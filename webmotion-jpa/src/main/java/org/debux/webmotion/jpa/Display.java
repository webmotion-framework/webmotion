package org.debux.webmotion.jpa;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Display extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Display.class);
    
    public Render index() {
        return renderView("index.jsp");
    }
    
}

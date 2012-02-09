package ${package};

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;

public class Base extends WebMotionController {

    public Render index() {
        return renderView("index.jsp");
    }
    
}

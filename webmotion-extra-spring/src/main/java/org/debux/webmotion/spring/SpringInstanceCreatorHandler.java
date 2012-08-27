package org.debux.webmotion.spring;

import javax.servlet.ServletContext;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.mapping.Mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Create instance with Spring.
 * 
 * @author julien
 */
public class SpringInstanceCreatorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(SpringInstanceCreatorHandler.class);

    @Override
    public void init(Mapping mapping, ServerContext context) {
        // Do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        // Get Spring context
        HttpContext context = call.getContext();
        ServletContext servletContext = context.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        // Search bean in Spring context
        Executor executor = call.getCurrent();
        Class<? extends WebMotionController> actionClass = executor.getClazz();
        WebMotionController instance = applicationContext.getBean(actionClass);
        executor.setInstance(instance);
    }

}

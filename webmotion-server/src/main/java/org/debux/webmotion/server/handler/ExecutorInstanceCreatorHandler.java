/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Debux
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
package org.debux.webmotion.server.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Config.Scope;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.call.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create instance containt in executor. Two modes are available, stateless mode
 * uses the same instance for each user request or statefull mode creates a 
 * instance for each user request.
 * 
 * @author julien
 */
public class ExecutorInstanceCreatorHandler extends AbstractHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorInstanceCreatorHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        Executor executor = call.getCurrent();
        
        // You must test mode here to manage correctly extension, otherwise
        // you risk to have only the factory define in init method.
        SingletonFactory<WebMotionController> factory = getControllerFactory(mapping, call);
        Class<? extends WebMotionController> actionClass = executor.getClazz();
        
        WebMotionController instance = factory.getInstance(actionClass);
        executor.setInstance(instance);
    }
    
    /** Attribute name use to store controller factory in the session or the request */
    public static final String CONTROLLER_FACTORY_ATTRIBUTE = "org.debux.webmotion.server.CONTROLLER_FACTORY";
    
    /**
     * Search the controller factory from mode define in mapping.
     * TODO: 20120124 jru Move to other class, to delete the dependance between handler and the render.
     * 
     * @param mapping mapping
     * @param call call
     * @return controller factory
     */
    public static SingletonFactory<WebMotionController> getControllerFactory(Mapping mapping, Call call) {
        
        HttpContext context = call.getContext();
        Config config = mapping.getConfig();
        Scope scope = config.getControllerScope();
        
        SingletonFactory<WebMotionController> factory = null;
        
        switch (scope) {
            case REQUEST:
                HttpServletRequest request = context.getRequest();
                factory = (SingletonFactory<WebMotionController>) request.getAttribute(CONTROLLER_FACTORY_ATTRIBUTE);
                if (factory == null) {
                    factory = new SingletonFactory<WebMotionController>();
                    request.setAttribute(CONTROLLER_FACTORY_ATTRIBUTE, factory);
                }
                break;
                
            case SESSION:
                HttpSession session = context.getSession();
                factory = (SingletonFactory<WebMotionController>) session.getAttribute(CONTROLLER_FACTORY_ATTRIBUTE);
                if (factory == null) {
                    factory = new SingletonFactory<WebMotionController>();
                    session.setAttribute(CONTROLLER_FACTORY_ATTRIBUTE, factory);
                }
                break;
                
            case SINGLETON:
                ServerContext serverContext = context.getServerContext();
                factory = serverContext.getControllers();
                break;
        }
        
        return factory;
    }
}

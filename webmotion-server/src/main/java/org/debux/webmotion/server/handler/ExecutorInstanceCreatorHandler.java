/*
 * #%L
 * Webmotion in action
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

import java.util.List;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Config.Mode;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
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
public class ExecutorInstanceCreatorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorInstanceCreatorHandler.class);

    protected SingletonFactory<WebMotionController> factory;
    
    @Override
    public void init(Mapping mapping, ServerContext context) {
        factory = context.getControllers();
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        HttpContext context = call.getContext();
        Config config = mapping.getConfig();
        Mode mode = config.getMode();

        List<Executor> executors = call.getExecutors();
        for (Executor executor : executors) {

            Class<? extends WebMotionController> actionClass = executor.getClazz();

            try {
                // You must test mode here to manage correctly extension, otherwise
                // you risk to have only the factory define in init method.
                WebMotionController instance = null;
                if(mode == Mode.STATEFULL) {
                    instance = actionClass.newInstance();

                } else if(mode == Mode.STATELESS) {
                    instance = factory.getInstance(actionClass);
                }
                
                executor.setInstance(instance);

            } catch (InstantiationException ie) {
                throw new WebMotionException("Error during create filter or action instance " + actionClass, ie, call.getRule());
                
            } catch (IllegalAccessException iae) {
                throw new WebMotionException("Error during create filter or action instance " + actionClass, iae, call.getRule());
            }
        }
    }
}

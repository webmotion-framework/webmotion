/*
 * #%L
 * WebMotion full stack
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
 * Create instance with spring.
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
        HttpContext context = call.getContext();
        ServletContext servletContext = context.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        Executor executor = call.getCurrent();
        Class<? extends WebMotionController> actionClass = executor.getClazz();
        WebMotionController instance = applicationContext.getBean(actionClass);
        executor.setInstance(instance);
    }

}

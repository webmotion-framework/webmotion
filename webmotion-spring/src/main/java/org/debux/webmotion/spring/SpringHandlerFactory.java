/*
 * #%L
 * Webmotion extension spring
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

import java.util.List;
import javax.servlet.ServletContext;
import org.debux.webmotion.server.WebMotionHandlerFactory;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Add SpringInstanceCreatorHandler with use bean in Spring
 * @author julien
 */
public class SpringHandlerFactory extends WebMotionHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(SpringHandlerFactory.class);

    @Override
    protected void initHandlers(Mapping mapping, ServerContext context) {
        ServletContext servletContext = context.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

        actionHandlers = webApplicationContext.getBean("springActionHandlers", List.class);
        errorHandlers = webApplicationContext.getBean("springErrorHandlers", List.class);
    }
    
}

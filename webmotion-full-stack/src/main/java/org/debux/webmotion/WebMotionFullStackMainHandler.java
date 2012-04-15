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
package org.debux.webmotion;

import org.debux.webmotion.sitemesh.*;
import java.util.Arrays;
import java.util.List;
import org.debux.webmotion.jpa.Jpa;
import org.debux.webmotion.jpa.JpaMainHandler.EntityManagerInjector;
import org.debux.webmotion.jpa.JpaMainHandler.EntityTransactionManager;
import org.debux.webmotion.jpa.JpaMainHandler.GenericDaoInjector;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionMainHandler;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.handler.ExecutorParametersConvertorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.spring.SpringInstanceCreatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add configuration to use Jpa, SiteMesh and Spring.
 * 
 * @author julien
 */
public class WebMotionFullStackMainHandler extends WebMotionMainHandler {

    private static final Logger log = LoggerFactory.getLogger(WebMotionFullStackMainHandler.class);

    @Override
    protected void initHandlers(Mapping mapping, ServerContext context) {
        super.initHandlers(mapping, context);
        
        context.addGlobalController(SiteMesh.class);
        context.addGlobalController(Jpa.class);
        
        context.addInjector(new GenericDaoInjector());
        context.addInjector(new EntityManagerInjector());
        context.addInjector(new EntityTransactionManager());
    }

    @Override
    public List<Class<? extends WebMotionHandler>> getExecutorHandlers() {
        return Arrays.asList(
                    SpringInstanceCreatorHandler.class,
                    ExecutorParametersConvertorHandler.class,
                    ExecutorParametersInjectorHandler.class,
                    ExecutorParametersValidatorHandler.class
                );
    }
    
}

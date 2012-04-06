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
package org.debux.webmotion.sitemesh;

import java.util.Arrays;
import java.util.List;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionMainHandler;
import org.debux.webmotion.server.handler.ActionExecuteRenderHandler;
import org.debux.webmotion.server.handler.ActionFinderHandler;
import org.debux.webmotion.server.handler.ActionMethodFinderHandler;
import org.debux.webmotion.server.handler.ExecutorInstanceCreatorHandler;
import org.debux.webmotion.server.handler.ExecutorMethodInvokerHandler;
import org.debux.webmotion.server.handler.ExecutorParametersConvertorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler;
import org.debux.webmotion.server.handler.FilterFinderHandler;
import org.debux.webmotion.server.handler.ParametersExtractorHandler;
import org.debux.webmotion.server.handler.ParametersMultipartHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add SiteMeshFilterMethodFinderHandler with use SiteMesh
 * 
 * @author julien
 */
public class SiteMeshMainHandler extends WebMotionMainHandler {

    private static final Logger log = LoggerFactory.getLogger(SiteMeshMainHandler.class);

    @Override
    public List<Class<? extends WebMotionHandler>> getActionHandlers() {
        return Arrays.asList(
                    ParametersMultipartHandler.class,
                    ActionFinderHandler.class,
                    FilterFinderHandler.class,
                    ParametersExtractorHandler.class,
                    ActionExecuteRenderHandler.class,
                    ActionMethodFinderHandler.class,
                    SiteMeshFilterMethodFinderHandler.class, // Add here replace FilterMethodFinderHandler
                    ExecutorInstanceCreatorHandler.class,
                    ExecutorParametersConvertorHandler.class,
                    ExecutorParametersInjectorHandler.class,
                    ExecutorParametersValidatorHandler.class,
                    ExecutorMethodInvokerHandler.class
                );
    }
    
}

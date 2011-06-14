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

import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Action;
import java.lang.reflect.Method;
import org.debux.webmotion.server.WebMotionAction;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.ExecutorAction;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Find the error class reprensent by name given in mapping. If it directly 
 * mapped on view, the executor not contains any information on class or method 
 * to execute.
 *
 * @author julien
 */
public class ErrorMethodFinderHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorMethodFinderHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        ErrorRule errorRule = call.getErrorRule();
        Action action = errorRule.getAction();

        if(action.isView()) {
            Executor executor = new Executor();
            call.setExecutor(executor);

        } else {
            String className = action.getClassName();
            Config config = mapping.getConfig();
            String packageName = config.getPackageErrors();
            String fullQualifiedName = packageName + "." + className;
            
            try {
                Class<WebMotionAction> clazz = (Class<WebMotionAction>) Class.forName(fullQualifiedName);

                String methodName = action.getMethodName();
                Method method = WebMotionUtils.getMethod(clazz, methodName);

                ExecutorAction executor = new ExecutorAction();
                executor.setClazz(clazz);
                executor.setMethod(method);
                call.setExecutor(executor);
                
            } catch (ClassNotFoundException clnfe) {
                throw new WebMotionException("Class not found with name " + fullQualifiedName, clnfe);
            }
        }
    }
}
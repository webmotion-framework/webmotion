/*
 * #%L
 * WebMotion full stack
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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

import java.lang.reflect.Method;
import java.util.List;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.handler.FilterMethodFinderHandler;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Force SiteMesh filter as class when is declared in other mapping package.
 * 
 * @author julien
 */
public class SiteMeshFilterMethodFinderHandler extends FilterMethodFinderHandler {

    @Override
    protected void handle(Mapping mapping, Call call, FilterRule filterRule) throws WebMotionException {
        Action action = filterRule.getAction();
        String className = action.getClassName();
        if (className.equals("SiteMesh")) {
            
            String methodName = action.getMethodName();
            Method method = WebMotionUtils.getMethod(SiteMesh.class, methodName);
            if (method == null) {
                String fullQualifiedName = action.getFullName();
                throw new WebMotionException("Method not found with name " + methodName + " on class " + fullQualifiedName, filterRule);
            }
            
            Executor executor = new Executor();
            executor.setClazz(SiteMesh.class);
            executor.setMethod(method);
            
            List<Executor> filters = call.getFilters();
            filters.add(executor);
            
        } else {
            super.handle(mapping, call, filterRule);
        }
    }
    
}

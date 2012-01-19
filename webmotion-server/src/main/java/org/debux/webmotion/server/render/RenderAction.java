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
package org.debux.webmotion.server.render;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.WebMotionUtils.SingletonFactory;
import org.debux.webmotion.server.WebMotionServerContext;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.handler.ExecutorInstanceCreatorHandler;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to call an action and return the result.
 * 
 * @author julien
 */
public class RenderAction extends Render {
    protected String action;
    protected Object[] model;

    public RenderAction(String action, Object[] model) {
        this.action = action;
        this.model = model;
    }

    public String getAction() {
        return action;
    }

    public Object[] getModel() {
        return model;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        RenderAction render = (RenderAction) call.getRender();
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest request = context.getRequest();
        
        String className = StringUtils.substringBeforeLast(action, ".");
        String methodName = StringUtils.substringAfterLast(action, ".");
        
        // Get instance
        WebMotionServerContext serverContext = context.getServerContext();
        SingletonFactory<WebMotionController> factory = serverContext.getControllers();
        
        Config config = mapping.getConfig();
        String packageName = config.getPackageActions();
        String fullQualifiedName = null;
        if (packageName == null || packageName.isEmpty()) {
            fullQualifiedName = className;
        } else {
            fullQualifiedName = packageName + "." + className;
        }
        
        try {
            WebMotionController instance = factory.getInstance(fullQualifiedName);
            // Invoke method
            Method method = WebMotionUtils.getMethod(instance.getClass(), methodName);
            Render actionRender = (Render) method.invoke(instance, model);
            // Request the render
            call.setRender(actionRender);
            actionRender.create(mapping, call);
            
        } catch (IllegalAccessException iae) {
            throw new WebMotionException("Error render action", iae);
        } catch (IllegalArgumentException iae) {
            throw new WebMotionException("Error render action", iae);
        } catch (InvocationTargetException ite) {
            throw new WebMotionException("Error render action", ite);
        }
    }
    
}

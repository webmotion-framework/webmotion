/*
 * #%L
 * Webmotion website
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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
package org.debux.webmotion.test;

import java.lang.reflect.Type;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler.Injector;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Injector listener.
 * 
 * @author julien
 */
public class InjectorListener implements WebMotionServerListener {
    
    @Override
    public void onStart(Mapping mapping, ServerContext context) {
        // Create config
        Config config = new Config();
        config.setParameter("A value from the config object");
        context.setAttribute("config", config);
        
        // Declare injector
        context.addInjector(new Injector() {
            @Override
            public Object getValue(Mapping mapping, Call call, String name, Class<?> type, Type generic) {
                if (Config.class.isAssignableFrom(type)) {
                    ServerContext serverContext = call.getContext().getServerContext();
                    Object config = serverContext.getAttribute("config");
                    return config;
                }
                return null;
            }
        });
    }

    @Override
    public void onStop(ServerContext context) {
        // Do nothing
    }
    
}

/*
 * #%L
 * WebMotion extra spring
 * 
 * $Id: SpringMainHandler.java 2004 2012-08-27 10:49:23Z tchemit $
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
package org.debux.webmotion.spring;

import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add configuration to use Spring. It changes the main handler in mapping file.
 * 
 * @author julien
 */
public class SpringListener implements WebMotionServerListener {

    private static final Logger log = LoggerFactory.getLogger(SpringListener.class);

    @Override
    public void onStart(Mapping mapping, ServerContext context) {
        Config config = mapping.getConfig();
        config.setMainHandler(SpringMainHandler.class.getName());
    }

    @Override
    public void onStop(ServerContext context) {
        // Do nothing
    }
    
}

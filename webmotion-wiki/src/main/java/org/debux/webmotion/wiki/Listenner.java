/*
 * #%L
 * WebMotion wiki
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
package org.debux.webmotion.wiki;

import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Properties;
import org.debux.webmotion.wiki.service.WikiConfig;

/**
 * Create the WikiConfig instance on start up the server.
 * 
 * @author julien
 */
public class Listenner implements WebMotionServerListener {

    @Override
    public void onStart(Mapping mapping, ServerContext context) {
        Mapping parent = mapping.getParentMapping();
        Properties properties = parent.getProperties();
        WikiConfig.instance = new WikiConfig(properties);
    }

    @Override
    public void onStop(ServerContext context) {
    }
    
}

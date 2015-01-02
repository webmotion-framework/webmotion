/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server;

import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * The class is used to listen start/stop of the server. Thus you can execute 
 * some actions when the server starts or stops. The implementation must contain
 * an empty constructor.
 * 
 * @author julien
 */
public interface WebMotionServerListener {
    
    /**
     * Call when the server start.
     * @param context server context
     * @param mapping current mapping
     */
    public void onStart(Mapping mapping, ServerContext context);
    
    /**
     * Call when the server stop.
     * @param context server context
     */
    public void onStop(ServerContext context);
    
}

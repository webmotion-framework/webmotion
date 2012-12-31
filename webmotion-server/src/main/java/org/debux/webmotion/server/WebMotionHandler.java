/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server;

import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * The handler answers a basic task. It stores computed information in call 
 * object. The mapping is read only.
 * 
 * @author jruchaud
 */
public interface WebMotionHandler {

    /**
     * Call when the handler is created.
     * 
     * @param mapping mapping
     * @param context context
     */
    void handlerCreated(Mapping mapping, ServerContext context);
    
    /**
     * Call when the MainHandler is created or initialized.
     * 
     * @param mapping mapping
     * @param context context
     */
    void handlerInitialized(Mapping mapping, ServerContext context);
    
    /**
     * Call when the handler is destroyed.
     * 
     * @param mapping mapping
     * @param context context
     */
    void handlerDestroyed(Mapping mapping, ServerContext context);
    
    /**
     * Call each request.
     * 
     * @param mapping mapping
     * @param call call
     */
    void handle(Mapping mapping, Call call);

}

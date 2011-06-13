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
package org.debux.webmotion.server;

import org.debux.webmotion.server.call.HttpContext;

/**
 * This class contains HTTP context information. The information are stored in a {@see ThreadLocal}.
 * It is used in any action class and/or filter class in order to manipulate HTTP context information 
 * like cookies, session, etc.
 * 
 * @author jruchaud
 */
public class WebMotionContextable {

    private static final ThreadLocal<HttpContext> context = new ThreadLocal<HttpContext>();
    
    /**
     * Default constructor
     */
    public WebMotionContextable() {
    }

    /**
     * Get the HTTP context.
     * @return HTTP context
     */
    public HttpContext getContext() {
        return context.get();
    }
    
    /**
     * Store the HTTP context in thread local.
     * @param value  HTTP context
     */
    public void setContext(HttpContext value) {
        context.set(value);
    }
    
}

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

import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * This class contains HTTP context information. The information are stored in a {@see ThreadLocal}.
 * It is used in any action class and/or filter class in order to manipulate HTTP context information 
 * like cookies, session, etc.
 * 
 * @author jruchaud
 */
public class WebMotionContextable {

    protected ThreadLocal<Context> context;
    
    public static class Context {
        protected Mapping mapping;
        protected Call call;
        protected WebMotionHandler handler;

        public Call getCall() {
            return call;
        }

        public void setCall(Call call) {
            this.call = call;
        }

        public Mapping getMapping() {
            return mapping;
        }

        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        public WebMotionHandler getHandler() {
            return handler;
        }

        public void setHandler(WebMotionHandler handler) {
            this.handler = handler;
        }
    }
    
    /**
     * Default constructor
     */
    public WebMotionContextable() {
        this.context = new ThreadLocal<Context>();
    }
    
    public void create(WebMotionHandler handler, Mapping mapping, Call call) {
        Context context = getContext();
        if(context == null) {
            context = new Context();
            this.context.set(context);
        }
        context.setMapping(mapping);
        context.setCall(call);
        context.setHandler(handler);
    }
    
    public void remove() {
        context.remove();
    }
    
    protected Context getContext() {
        return context.get();
    }
    
    public Call getCall() {
        return getContext().getCall();
    }

    public Mapping getMapping() {
        return getContext().getMapping();
    }
    
    public WebMotionHandler getHandler() {
        return getContext().getHandler();
    }

}

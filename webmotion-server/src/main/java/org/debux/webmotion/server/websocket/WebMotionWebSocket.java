/*
 * #%L
 * WebMotion server
 * $Id:$
 * $HeadURL:$
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
package org.debux.webmotion.server.websocket;

import javax.servlet.ServletContext;
import org.debux.webmotion.server.call.ServerContext;

/**
 * Simple class to manage the websocket. The outbound object use to send message to 
 * the client.
 * 
 * @author julien
 */
public class WebMotionWebSocket implements WebSocketInbound {

    /**
     * Current servlet context.
     */
    protected ServletContext servletContext;
    
    /**
     * Wrapper used to send message.
     */
    protected WebSocketOutbound outbound;

    @Override
    public void setOutbound(WebSocketOutbound outbound) {
        this.outbound = outbound;
    }

    @Override
    public WebSocketOutbound getOutbound() {
        return outbound;
    }

    @Override
    public void receiveTextMessage(String message) {
    }

    @Override
    public void receiveDataMessage(byte[] bytes) {
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose() {
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServerContext getServerContext() {
        return (ServerContext) servletContext.getAttribute(ServerContext.ATTRIBUTE_SERVER_CONTEXT);
    }

}

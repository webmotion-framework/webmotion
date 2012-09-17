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
package org.debux.webmotion.server.websocket.wrapper;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DataFrame;
import com.sun.grizzly.websockets.DefaultWebSocket;
import com.sun.grizzly.websockets.ProtocolHandler;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;
import com.sun.grizzly.websockets.WebSocketEngine;
import com.sun.grizzly.websockets.WebSocketListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.debux.webmotion.server.websocket.WebSocketFactory;
import org.debux.webmotion.server.websocket.WebSocketInbound;
import org.debux.webmotion.server.websocket.WebSocketOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper on Glassfish servlet to run websocket.
 * 
 * @author julien
 */
public class WebSocketGlassfishWrapper extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(WebSocketGlassfishWrapper.class);
    
    private final WebSocketApplicationWrapper app = new WebSocketApplicationWrapper();

    protected WebSocketFactory factory;

    public WebSocketGlassfishWrapper(WebSocketFactory factory) {
            this.factory = factory;
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        WebSocketEngine.getEngine().register(app);
    }

    public class WebSocketWrapper extends DefaultWebSocket implements WebSocketOutbound {
        
        protected WebSocketInbound inbound;

        public WebSocketWrapper(WebSocketInbound inbound, ProtocolHandler handler, WebSocketListener... listeners) {
            super(handler, listeners);
            this.inbound = inbound;
        }

        @Override
        public void sendTextMessage(String message) {
            send(message);
        }

        public WebSocketInbound getInbound() {
            return inbound;
        }
    }
    
    public class WebSocketApplicationWrapper extends WebSocketApplication {

        @Override
        public WebSocket createWebSocket(ProtocolHandler handler, WebSocketListener... listeners) {
            WebSocketInbound inbound = factory.createSocket();
            WebSocketWrapper wrapper = new WebSocketWrapper(inbound, handler, listeners);
            inbound.setOutbound(wrapper);
            return wrapper;
        }

        @Override
        public boolean isApplicationRequest(Request request) {
            return true;
        }
        
        @Override
        public void onConnect(WebSocket socket) {
            if (socket instanceof WebSocketWrapper) {
                WebSocketWrapper wrapper = (WebSocketWrapper) socket;
                WebSocketInbound inbound = wrapper.getInbound();
                inbound.onOpen();
            }
        }

        @Override
        public void onClose(WebSocket socket, DataFrame frame) {
            if (socket instanceof WebSocketWrapper) {
                WebSocketWrapper wrapper = (WebSocketWrapper) socket;
                WebSocketInbound inbound = wrapper.getInbound();
                inbound.onClose();
            }
        }

        @Override
        public void onMessage(WebSocket socket, String text) {
            if (socket instanceof WebSocketWrapper) {
                WebSocketWrapper wrapper = (WebSocketWrapper) socket;
                WebSocketInbound inbound = wrapper.getInbound();
                inbound.receiveTextMessage(text);
            }
        }
        
    }
}

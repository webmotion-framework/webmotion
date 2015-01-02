/*
 * #%L
 * WebMotion server
 * $Id:$
 * $HeadURL:$
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
package org.debux.webmotion.server.websocket.wrapper;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.websocket.WebSocketInbound;
import org.debux.webmotion.server.websocket.WebSocketOutbound;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.OnBinaryMessage;
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper on Jetty servlet to run websocket.
 * 
 * @author julien
 */
public class WebSocketJettyWrapper extends WebSocketServlet {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketJettyWrapper.class);

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
        WebSocketInbound inbound = (WebSocketInbound) request.getAttribute(WebSocketInbound.ATTRIBUTE_WEBSOCKET);
        WebSocketWrapper wrapper = new WebSocketWrapper(inbound);
        inbound.setOutbound(wrapper);

        ServletContext servletContext = request.getServletContext();
        inbound.setServletContext(servletContext);
            
        return wrapper;
    }
    
    public class WebSocketWrapper implements OnTextMessage, OnBinaryMessage, WebSocketOutbound {
        
        protected Connection connection;
        protected WebSocketInbound inbound;

        public WebSocketWrapper(WebSocketInbound inbound) {
            this.inbound = inbound;
        }
        
        @Override
        public void onMessage(String message) {
            inbound.receiveTextMessage(message);
        }

        @Override
        public void onOpen(Connection connection) {
            this.connection = connection;
            inbound.onOpen();
        }

        @Override
        public void onClose(int code, String message) {
            inbound.onClose();
        }

        @Override
        public void sendTextMessage(String message) {
            try {
                connection.sendMessage(message);
            } catch (IOException ioe) {
                log.error("Error sending message", ioe);
            }
        }

        @Override
        public void sendDataMessage(byte[] bytes) {
            try {
                connection.sendMessage(bytes, 0, bytes.length);
            } catch (IOException ioe) {
                log.error("Error sending message", ioe);
            }
        }

        @Override
        public void onMessage(byte[] bytes, int offset, int length) {
            inbound.receiveDataMessage(bytes);
        }
        
    }
}

package org.debux.webmotion.server.websocket.wrapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.debux.webmotion.server.websocket.WebSocketFactory;
import org.debux.webmotion.server.websocket.WebSocketInbound;
import org.debux.webmotion.server.websocket.WebSocketOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Wrapper on tomcat servlet to run websocket.
 * 
 * @author julien
 */
public class WebSocketTomcatWrapper extends WebSocketServlet {

    private static final Logger log = LoggerFactory.getLogger(WebSocketTomcatWrapper.class);
    
    protected WebSocketFactory factory;

    public WebSocketTomcatWrapper(WebSocketFactory factory) {
            this.factory = factory;
    }

    /**
     * Use since tomcat introduce request in method
     */
    protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
        return createWebSocketInbound(subProtocol);
    }

    /**
     * Use before tomcat introduce request in method
     */
    protected StreamInbound createWebSocketInbound(String string) {
        WebSocketInbound inbound = factory.createSocket();
        WebSocketInboundWrapper wrapper = new WebSocketInboundWrapper(inbound);
        inbound.setOutbound(wrapper);
        return wrapper;
    }
    
    public class WebSocketInboundWrapper extends MessageInbound implements WebSocketOutbound {
        
        protected WebSocketInbound inbound;

        public WebSocketInboundWrapper(WebSocketInbound inbound) {
            this.inbound = inbound;
        }
        
        @Override
        protected void onBinaryMessage(ByteBuffer message)  throws IOException {
            throw new UnsupportedOperationException("Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
            String msg = message.toString();
            inbound.receiveTextMessage(msg);
        }

        @Override
        protected void onOpen(WsOutbound outbound) {
            inbound.onOpen();
        }

        @Override
        protected void onClose(int status) {
            inbound.onClose();
        }
        
        @Override
        public void sendTextMessage(String message) {
            try {
                CharBuffer buffer = CharBuffer.wrap(message);
                getWsOutbound().writeTextMessage(buffer);
            } catch (IOException ioe) {
                log.error("Error sending message", ioe);
            }
        }

    }
}

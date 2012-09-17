package org.debux.webmotion.test;

/*
 * #%L
 * WebMotion test
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

import java.util.ArrayList;
import java.util.List;
import org.debux.webmotion.server.websocket.WebMotionWebSocket;
import org.debux.webmotion.server.websocket.WebSocketInbound;
import org.debux.webmotion.server.websocket.WebSocketOutbound;

/**
 *
 * @author julien
 */
public class EchoChat extends WebMotionWebSocket {

    protected List<WebSocketInbound> connections = new ArrayList<WebSocketInbound>();
    
    @Override
    public WebSocketInbound createSocket() {
        EchoChatWebSocket socket = new EchoChatWebSocket();
        connections.add(socket);
        return socket;
    }

    public class EchoChatWebSocket extends DefaultWebSocket {

        @Override
        public void receiveTextMessage(String message) {
            // Broadcast the message
            for (WebSocketInbound inbound : connections) {
                WebSocketOutbound outbound = inbound.getOutbound();
                outbound.sendTextMessage(message);
            }
        }
        
    }
    
}

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
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderWebSocket;
import org.debux.webmotion.server.websocket.WebMotionWebSocket;

/**
 *
 * @author julien
 */
public class EchoChat extends WebMotionController {

    public Render createSocket() {
        WebMotionWebSocket socket = new EchoChatWebSocket();
        return new RenderWebSocket(socket);
    }

    public class EchoChatWebSocket extends WebMotionWebSocket {

        @Override
        public void onOpen() {
            // Store all connections
            ServerContext serverContext = getServerContext();
            List<WebMotionWebSocket> connections = (List<WebMotionWebSocket>) serverContext.getAttribute("connections");
            if (connections == null) {
                connections = new ArrayList<WebMotionWebSocket>();
                serverContext.setAttribute("connections", connections);
            }
            connections.add(this);
        }

        @Override
        public void receiveTextMessage(String message) {
            // Broadcast the message
            ServerContext serverContext = getServerContext();
            List<WebMotionWebSocket> connections = (List<WebMotionWebSocket>) serverContext.getAttribute("connections");
            for (WebMotionWebSocket socket : connections) {
                socket.sendTextMessage(message);
            }
        }
        
    }
    
}

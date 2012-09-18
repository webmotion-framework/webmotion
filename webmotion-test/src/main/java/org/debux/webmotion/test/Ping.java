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

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.render.RenderWebSocket;
import org.debux.webmotion.server.websocket.WebMotionWebSocket;
import org.debux.webmotion.server.websocket.WebMotionWebSocketJson;

/**
 *
 * @author julien
 */
public class Ping extends WebMotionController {

    public Render createSocket(String who) {
        WebMotionWebSocket socket = new PingWebSocket(who);
        return new RenderWebSocket(socket);
    }

    public class PingWebSocket extends WebMotionWebSocketJson {
        
        protected String who;

        public PingWebSocket(String who) {
            this.who = who;
        }
        
        public String ping(String message) {
            if (who != null) {
                return who + ":" + message;
            } else {
                return message;
            }
        }
        
    }
    
}

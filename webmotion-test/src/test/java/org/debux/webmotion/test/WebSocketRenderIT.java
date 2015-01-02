/*
 * #%L
 * Webmotion in action
 * 
 * $Id$
 * $HeadURL$
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
package org.debux.webmotion.test;

import org.debux.webmotion.server.tools.RequestBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test misc websocket.
 * 
 * @author julien
 */
public class WebSocketRenderIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(WebSocketRenderIT.class);

    @Override
    public RequestBuilder createRequest(String url) {
        return super.createRequest(url).setScheme("ws");
    }
    
    @Test
    public void basic() throws Exception {
        String url = createRequest("/echoChat/ws")
                .toString();

        AsyncHttpClient client = new AsyncHttpClient();
        WebSocket websocket = client.prepareGet(url)
            .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
            new WebSocketTextListener() {
                @Override
                public void onMessage(String message) {
                    log.debug(message);
                    AssertJUnit.assertEquals("test", message);
                }

                @Override
                public void onOpen(WebSocket websocket) {
                }

                @Override
                public void onClose(WebSocket websocket) {
                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onFragment(String string, boolean bln) {
                }
            }).build()).get();
        
        websocket.sendTextMessage("test");
    }

    @Test
    public void json() throws Exception {
        String url = createRequest("/ping/ws")
                .addParameter("who", "test")
                .toString();

        AsyncHttpClient client = new AsyncHttpClient();
        WebSocket websocket = client.prepareGet(url)
            .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
            new WebSocketTextListener() {
                @Override
                public void onMessage(String message) {
                    log.debug(message);
                    AssertJUnit.assertEquals("{\"method\":\"ping\",\"result\":\"test:test\"}", message);
                }

                @Override
                public void onOpen(WebSocket websocket) {
                }

                @Override
                public void onClose(WebSocket websocket) {
                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onFragment(String string, boolean bln) {
                }
            }).build()).get();
        
        websocket.sendTextMessage("{method : \"ping\", params : {message : \"test\"}}");
    }

}

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
import javax.servlet.DispatcherType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mbean.ServerStats;
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

    protected ServletContext servletContext;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        servletContext = config.getServletContext();
        WebSocketEngine.getEngine().register(app);
    }

    public class WebSocketWrapper extends DefaultWebSocket implements WebSocketOutbound {
        
        protected WebSocketInbound inbound;

        public WebSocketWrapper(ProtocolHandler handler, WebSocketListener... listeners) {
            super(handler, listeners);
        }

        @Override
        public void sendTextMessage(String message) {
            send(message);
        }

        @Override
        public void sendDataMessage(byte[] bytes) {
            send(bytes);
        }
        
        public WebSocketInbound getInbound() {
            return inbound;
        }

        public void setInbound(WebSocketInbound inbound) {
            this.inbound = inbound;
        }

    }
    
    public class WebSocketApplicationWrapper extends WebSocketApplication {
        
        @Override
        public WebSocket createWebSocket(ProtocolHandler handler, WebSocketListener... listeners) {
            WebSocketWrapper wrapper = new WebSocketWrapper(handler, listeners);
            
            HttpServletRequest request = wrapper.getRequest();
            HttpServletResponse response = wrapper.getResponse();
            
            // Wrap undefined method
            doAction(new HttpServletRequestWrapper(request) {
                        @Override
                        public DispatcherType getDispatcherType() {
                            return DispatcherType.REQUEST;
                        }
                        @Override
                        public ServletContext getServletContext() {
                            return servletContext;
                        }
                    }, response);
            
            WebSocketInbound inbound = (WebSocketInbound) request.getAttribute(WebSocketInbound.ATTRIBUTE_WEBSOCKET);
            inbound.setOutbound(wrapper);
            inbound.setServletContext(servletContext);
            
            wrapper.setInbound(inbound);
            
            return wrapper;
        }
        
        // Glassfish not pass in WebMotionServerFilter, so it is forced here.
        protected void doAction(HttpServletRequest request, HttpServletResponse response) {
            long start = System.currentTimeMillis();

            // Create call context use in handler to get information on user request
            ServerContext serverContext = (ServerContext) servletContext.getAttribute(ServerContext.ATTRIBUTE_SERVER_CONTEXT);
            Call call = new Call(serverContext, request, response);

            // Execute the main handler
            Mapping mapping = serverContext.getMapping();
            WebMotionHandler mainHandler = serverContext.getMainHandler();
            mainHandler.handle(mapping, call);

            // Register call in mbean
            ServerStats serverStats = serverContext.getServerStats();
            serverStats.registerCallTime(call, start);
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

        @Override
        public void onMessage(WebSocket socket, byte[] bytes) {
            if (socket instanceof WebSocketWrapper) {
                WebSocketWrapper wrapper = (WebSocketWrapper) socket;
                WebSocketInbound inbound = wrapper.getInbound();
                inbound.receiveDataMessage(bytes);
            }
        }
        
    }
}

/*
 * #%L
 * WebMotion server
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
package org.debux.webmotion.server.websocket;

import java.util.List;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test convert on WebMotionWebSocketJson.
 * 
 * @author julien
 */
public class WebMotionWebSocketJsonTest {
    
    private static final Logger log = LoggerFactory.getLogger(WebMotionWebSocketJsonTest.class);
    
    protected WebSocketTester ws;
    
    public static class WebSocketTester extends WebMotionWebSocketJson {
        String sendMessage = null;

        public WebSocketTester() {
            outbound = new WebSocketOutbound() {
                @Override
                public void sendTextMessage(String message) {
                    sendMessage = message;
                }
                @Override
                public void sendDataMessage(byte[] bytes) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        @Override
        public ServerContext getServerContext() {
            return new ServerContext() {
                @Override
                public Mapping getMapping() {
                    return new Mapping();
                }
            };
        }
            
        public String testString(String value) {
            return value;
        }
        
        public List<String> testCollection(List<String> values) {
            return values;
        }

        public String[] testArray(String[] values) {
            return values;
        }

        public int testInt(int value) {
            return value;
        }

        public boolean testBoolean(boolean value) {
            return value;
        }

        public AnObject testObject(AnObject value) {
            return value;
        }

        public String getSendMessage() {
            return sendMessage;
        }
    }
    
    public static class AnObject {
        protected String attributeString;
        protected int attributeInt;
    }
    
    @BeforeMethod
    public void setUp() {
        ws = new WebSocketTester();
    }
    
    @Test
    public void testConvertString() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testString\","
                + "\"params\" : {\"value\" : \"test test\"}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testString\",\"result\":\"test test\"}", sendMessage);
    }
    
    @Test
    public void testConvertInt() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testInt\","
                + "\"params\" : {\"value\" : 42}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testInt\",\"result\":42}", sendMessage);
    }

    @Test
    public void testConvertBoolean() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testBoolean\","
                + "\"params\" : {\"value\" : true}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testBoolean\",\"result\":true}", sendMessage);
    }

    @Test
    public void testConvertObject() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testObject\","
                + "\"params\" : {\"value\" : {\"attributeString\":\"test\",\"attributeInt\":42}}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testObject\",\"result\":{\"attributeString\":\"test\",\"attributeInt\":42}}", sendMessage);
    }
    
    @Test
    public void testConvertCollection() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testCollection\","
                + "\"params\" : {\"values\" : [\"test\", \"test\"]}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testCollection\",\"result\":[\"test\",\"test\"]}", sendMessage);
    }
        
    @Test
    public void testConvertArray() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testArray\","
                + "\"params\" : {\"values\" : [\"test\", \"test\"]}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testArray\",\"result\":[\"test\",\"test\"]}", sendMessage);
    }
    
    @Test
    public void testConvertNull() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testString\","
                + "\"params\" : {\"value\" : null}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testString\"}", sendMessage);
    }

    @Test
    public void testEmptyParams() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testString\","
                + "\"params\" : {}"
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testString\"}", sendMessage);
    }

    @Test
    public void testNoParams() {
        ws.receiveTextMessage("{"
                + "\"method\" : \"testString\""
            + "}");
        
        String sendMessage = ws.getSendMessage();
        log.info("sendMessage = " + sendMessage);
        AssertJUnit.assertEquals("{\"method\":\"testString\"}", sendMessage);
    }

}

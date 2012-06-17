/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
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
package org.debux.webmotion.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on Gson.
 * 
 * @author julien
 */
public class GsonTest {
   
    private static final Logger log = LoggerFactory.getLogger(GsonTest.class);

    public static class Event {

        private String name;
        private String source;

        private Event(String name, String source) {
            this.name = name;
            this.source = source;
        }
    }

    @Test
    public void testMap() {
        Gson gson = new Gson();
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "john");
        map.put("limit", 10);
        map.put("event", new Event("access", "guest"));
        map.put("keys", new String[]{"abc", "def", "ghi"});
        
        String json = gson.toJson(map);
        log.debug("json = " + json);
        
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(json).getAsJsonObject();
        
        JsonElement name = object.get("name");
        String nameValue = gson.fromJson(name, String.class);
        AssertJUnit.assertEquals("john", nameValue);
        
        JsonElement limit = object.get("limit");
        int limitValue = gson.fromJson(limit, Integer.class);
        AssertJUnit.assertEquals(10, limitValue);
        
        JsonElement event = object.get("event");
        Event eventValue = gson.fromJson(event, Event.class);
        AssertJUnit.assertEquals("access", eventValue.name);
        AssertJUnit.assertEquals("guest", eventValue.source);
        
        JsonElement keys = object.get("keys");
        String[] keysValue = gson.fromJson(keys, String[].class);
        AssertJUnit.assertEquals("ghi", keysValue[2]);
    }
    
    @Test
    public void testArrayClass() throws ClassNotFoundException {
        Class stringArrayClass = Array.newInstance(String.class, 0).getClass();
        AssertJUnit.assertEquals(String[].class, stringArrayClass);
        
        Class integerArrayClass = Class.forName("[Ljava.lang.Integer;");
        AssertJUnit.assertEquals(Integer[].class, integerArrayClass);
        
        Class intArrayClass = Class.forName("[I");
        AssertJUnit.assertEquals(int[].class, intArrayClass);
    }
    
    @Test
    public void testArray() {
        Gson gson = new Gson();
        Class arrayClass = Array.newInstance(Integer.class, 0).getClass();
        Integer[] fromJson = (Integer[]) gson.fromJson("[1,2,3,4,5]", arrayClass);
        AssertJUnit.assertEquals((Integer) 3, fromJson[2]);
    }
    
}

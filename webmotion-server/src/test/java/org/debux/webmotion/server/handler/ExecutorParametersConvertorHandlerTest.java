/*
 * #%L
 * Webmotion in action
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Debux
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
package org.debux.webmotion.server.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test on convert parameters to object to call the method.
 * 
 * @author julien
 */
public class ExecutorParametersConvertorHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersConvertorHandlerTest.class);

    protected ExecutorParametersConvertorHandler handler;
    
    @BeforeMethod
    public void createHandler() {
        handler = new ExecutorParametersConvertorHandler();
        handler.init(null);
    }
    
    public <T> T[] toArray(T ... values) {
        return values;
    }
    
    /**
     * Replace dot by a map
     * @param values
     * @return 
     */
    public Map<String, Object> toMap(String ... values) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        for (String value : values) {
            
            Map<String, Object> map = result;
            String[] split = value.split("\\.");

            for (int position = 0; position < split.length; position++) {

                if(position == split.length - 1) {
                    map.put(split[position], "value");

                } else {
                    String name = split[position];

                    Map<String, Object> next = (Map<String, Object>) map.get(name);
                    if(next == null) {
                        next = new LinkedHashMap<String, Object>();
                        map.put(name, next);
                    }

                    map = next;
                }
            }
        }
        
        return result;
    }

    @Test
    public void testConvertString() throws Exception {
        Object convert = handler.convert(
                toArray("test"),
                String.class, null);
        
        AssertJUnit.assertEquals(String.class, convert.getClass());
        AssertJUnit.assertEquals("test", convert);
    }
    
    @Test
    public void testConvertInteger() throws Exception {
        Object convert = handler.convert(
                toArray(10),
                Integer.class, null);
        
        AssertJUnit.assertEquals(Integer.class, convert.getClass());
        AssertJUnit.assertEquals(10, convert);
    }
    
    @Test
    public void testConvertArrayString() throws Exception {
        Object convert = handler.convert(
                toArray("test", "other"),
                String[].class, null);
        
        AssertJUnit.assertEquals(String[].class, convert.getClass());
        AssertJUnit.assertEquals(2, ((String[]) convert).length);
    }
    
    @Test
    public void testConvertMap() throws Exception {
        Object convert = handler.convert(
                toMap("key1", "key2"),
                Map.class, String.class);
        
        AssertJUnit.assertEquals(HashMap.class, convert.getClass());
        AssertJUnit.assertEquals(2, ((Map) convert).size());
    }
    
    public static class ClassExemple {
        public String attribute1;
        public String attribute2;

        public void setAttribute1(String attribute1) {
            this.attribute1 = attribute1;
        }

        public void setAttribute2(String attribute2) {
            this.attribute2 = attribute2;
        }
    }
    
    @Test
    public void testConvertObject() throws Exception {
        Object convert = handler.convert(
                toMap("attribute1", "attribute2"),
                ClassExemple.class, null);
        
        AssertJUnit.assertEquals(ClassExemple.class, convert.getClass());
        AssertJUnit.assertEquals("value", ((ClassExemple) convert).attribute1);
        AssertJUnit.assertEquals("value", ((ClassExemple) convert).attribute2);
    }
    
    @Test
    public void testConvertListObject(List<ClassExemple> list) throws Exception {
        Object convert = handler.convert(
                toArray(toMap("obj1.attribute1", "obj1.attribute2"), toMap("obj2.attribute1", "obj2.attribute2")),
                list.getClass(), null);
        
        AssertJUnit.assertEquals(ArrayList.class, convert.getClass());
        AssertJUnit.assertEquals(2, ((ArrayList) convert).size());
    }
    
    public static class ComplexClassExemple {
        public ClassExemple example;

        public void setExample(ClassExemple example) {
            this.example = example;
        }
    }
    
    @Test
    public void testConvertComplexObject() throws Exception {
        Object convert = handler.convert(
                toMap("example.attribute1", "example.attribute2"),
                ComplexClassExemple.class, null);
        
        AssertJUnit.assertEquals(ComplexClassExemple.class, convert.getClass());
        AssertJUnit.assertEquals("value", ((ComplexClassExemple) convert).example.attribute1);
        AssertJUnit.assertEquals("value", ((ComplexClassExemple) convert).example.attribute2);
    }
    
}

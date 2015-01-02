/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test basic.
 * 
 * @author julien
 */
public class BasicTest {
   
    private static final Logger log = LoggerFactory.getLogger(BasicTest.class);
    
    @Test
    public void testSplit() {
        String[] result = "test".split(",");
        AssertJUnit.assertEquals(1, result.length);
        
        result = "test,".split(",");
        AssertJUnit.assertEquals(1, result.length);
        
        result = "test,test".split(",");
        AssertJUnit.assertEquals(2, result.length);
        
        result = ",".split(",");
        AssertJUnit.assertEquals(0, result.length);
        
        result = "".split(",");
        AssertJUnit.assertEquals(1, result.length);
    }
    
    @Test
    public void testIterator() {
        List<String> list = new ArrayList<String>();
        list.add("value 1");
        list.add("value 2");
        list.add("value 3");
        
        List<String> result = new ArrayList<String>();
        for (ListIterator<String> iterator = list.listIterator(); iterator.hasNext();) {
            String value = iterator.next();
            if (value.equals("value 2")) {
                iterator.add("value");
                iterator.previous();
            }
            result.add(value);
        }
        
        AssertJUnit.assertArrayEquals(
                new String[]{
                    "value 1",
                    "value 2",
                    "value",
                    "value 3"
                },
                result.toArray());
    }
    
}

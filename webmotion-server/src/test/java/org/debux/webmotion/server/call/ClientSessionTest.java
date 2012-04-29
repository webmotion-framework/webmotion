/*
 * #%L
 * WebMotion server
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
package org.debux.webmotion.server.call;

import java.util.Enumeration;
import org.apache.commons.collections.EnumerationUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test on ClientSession.
 * 
 * @author julien
 */
public class ClientSessionTest {
    
    protected ClientSession session;
    
    @BeforeMethod
    public void setUp() {
        session = new ClientSession();
    }
    
    @Test
    public void testCreateSession() {
        AssertJUnit.assertNotNull(session.getId());
        AssertJUnit.assertNotNull(session.getCreationTime());
        AssertJUnit.assertEquals(session.getCreationTime(), session.getLastAccessedTime());
        AssertJUnit.assertTrue(session.isNew());
    }
    
    @Test
    public void testExpiredSession() {
        long lastAccessedTime = session.getLastAccessedTime();
        session.setMaxInactiveInterval(10);
        
        AssertJUnit.assertFalse(session.isExpired(lastAccessedTime));
        AssertJUnit.assertTrue(session.isExpired(lastAccessedTime + 2* 10 * 1000));
        
        session.setMaxInactiveInterval(0);
        
        AssertJUnit.assertFalse(session.isExpired(lastAccessedTime));
        AssertJUnit.assertFalse(session.isExpired(lastAccessedTime + 2* 10 * 1000));
    }
    
    @Test
    public void testSetBasicAttribute() {
        session.setAttribute("name", "value");
        
        AssertJUnit.assertEquals("value", session.getAttribute("name"));
        AssertJUnit.assertEquals("value", session.getAttribute("name", String.class));
    }
    
    public static class MyObject {
        protected String name;

        public MyObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    
    @Test
    public void testSetObjectAttribute() {
        session.setAttribute("name", new MyObject("value"));
        
        AssertJUnit.assertNotNull(session.getAttribute("name"));
        AssertJUnit.assertEquals("value", session.getAttribute("name", MyObject.class).getName());
    }
    
    @Test
    public void testSetNullAttribute() {
        session.setAttribute("name", null);
        
        AssertJUnit.assertNotNull(session.getAttribute("name"));
        AssertJUnit.assertNull(session.getAttribute("name", MyObject.class));
    }
    
    @Test
    public void testRemoveAttribute() {
        session.setAttribute("name", "value");
        session.removeAttribute("name");
        
        AssertJUnit.assertNull(session.getAttribute("name"));
        AssertJUnit.assertNull(session.getAttribute("name", String.class));
        AssertJUnit.assertNull(session.getAttributes("name", String.class));
    }
    
    @Test
    public void testGetAttributeNames() {
        Enumeration<String> attributeNames = session.getAttributeNames();
        AssertJUnit.assertFalse(attributeNames.hasMoreElements());
        
        session.setAttribute("name1", "value");
        session.setAttribute("name2", "value");
        session.setAttribute("name3", "value");
        
        attributeNames = session.getAttributeNames();
        AssertJUnit.assertEquals(3, EnumerationUtils.toList(attributeNames).size());
    }
}

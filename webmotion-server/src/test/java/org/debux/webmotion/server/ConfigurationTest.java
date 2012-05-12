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
package org.debux.webmotion.server;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on common-configuration
 * 
 * @author julien
 */
public class ConfigurationTest {
    
    @Test
    public void testString() throws ConfigurationException {
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        configuration.load(new StringReader("name=value"));
        AssertJUnit.assertEquals("value", configuration.getString("name"));
    }
    
    @Test
    public void testMap() throws ConfigurationException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test", "10");
        map.put("var", "${test}");
        
        MapConfiguration configuration = new MapConfiguration(map);
        AssertJUnit.assertEquals(10, configuration.getInt("test"));
        AssertJUnit.assertEquals(10, configuration.getInt("var"));
    }
    
    @Test
    public void testComposite() throws ConfigurationException {
        PropertiesConfiguration local = new PropertiesConfiguration();
        local.load(new StringReader("name=value"));
        
        PropertiesConfiguration file = new PropertiesConfiguration();
        file.load(new StringReader("name=other"));
        
        CompositeConfiguration composite = new CompositeConfiguration();
        composite.addConfiguration(file);
        composite.addConfiguration(local);
        
        AssertJUnit.assertEquals("other", composite.getString("name"));
    }
    
    @Test
    public void testData() throws ConfigurationException {
        PropertiesConfiguration test = new PropertiesConfiguration();
        test.load(new StringReader("color=#0000FF"));
        
        DataConfiguration configuration = new DataConfiguration(test);
        AssertJUnit.assertNotNull(configuration.getColor("color"));
    }
    
    @Test
    public void testLoad() throws ConfigurationException {
        ClassLoader classLoader = ConfigurationTest.class.getClassLoader();
        
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        configuration.load(classLoader.getResource("properties/etc.properties"));
        configuration.load(classLoader.getResource("properties/home.properties"));
        configuration.load(classLoader.getResource("properties/local.properties"));
        
        AssertJUnit.assertEquals("etc", configuration.getString("key1"));
        AssertJUnit.assertEquals("home", configuration.getString("key2"));
        AssertJUnit.assertEquals("local", configuration.getString("key3"));
    }
            
}

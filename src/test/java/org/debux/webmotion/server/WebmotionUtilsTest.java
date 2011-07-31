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
package org.debux.webmotion.server;

import java.lang.reflect.Method;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test on utility.
 * 
 * @author julien
 */
public class WebmotionUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(WebmotionUtilsTest.class);
    
    protected Mapping mapping;
    
    @BeforeMethod
    public void createMapping() {
        mapping = new Mapping();
        
        Config config = mapping.getConfig();
        config.setReloadable(true);
    }
    
    @Test
    public void testGetParameterNames() throws NoSuchMethodException {
        Method method = WebMotionUtils.class.getMethod("getParameterNames", Mapping.class, Method.class);
        String[] parameterNames = WebMotionUtils.getParameterNames(mapping, method);
        
        AssertJUnit.assertEquals(2, parameterNames.length);
        AssertJUnit.assertEquals("mapping", parameterNames[0]);
        AssertJUnit.assertEquals("method", parameterNames[1]);
    }
    
    @Test
    public void testGetMethod() {
        Method method = WebMotionUtils.getMethod(WebMotionUtils.class, "getMethod");
        AssertJUnit.assertNotNull(method);
    }
    
    @Test(expectedExceptions={WebMotionException.class})
    public void testGetMethodNotFound() {
        WebMotionUtils.getMethod(WebMotionUtils.class, "notFound");
    }

    @Test
    public void testCapitalizeClass() {
        String result = WebMotionUtils.capitalizeClass("org.webmotion.myclass");
        AssertJUnit.assertEquals("org.webmotion.Myclass", result);
    }
    
    @Test
    public void testUnCapitalizeClass() {
        String result = WebMotionUtils.unCapitalizeClass("org.webmotion.Myclass");
        AssertJUnit.assertEquals("org.webmotion.myclass", result);
    }
    
}

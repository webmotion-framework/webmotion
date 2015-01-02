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
package org.debux.webmotion.server.tools;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import org.debux.webmotion.server.call.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on utility.
 * 
 * @author julien
 */
public class HttpUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(HttpUtilsTest.class);
    
    @Test
    public void testSplitPath() {
        List<String> result = HttpUtils.splitPath("/");
        AssertJUnit.assertEquals(1, result.size());
        
        result = HttpUtils.splitPath("/deploy/test/run");
        AssertJUnit.assertEquals(6, result.size());
        
        result = HttpUtils.splitPath("/deploy/test/run/");
        AssertJUnit.assertEquals(7, result.size());
    }

    @Test
    public void testGenerateSecret() throws IOException, URISyntaxException {
        String generateSecret = HttpUtils.generateSecret();
        AssertJUnit.assertNotNull(generateSecret);
        AssertJUnit.assertFalse(generateSecret.isEmpty());
    }
    
    @Test
    public void testreplaceDynamicName() {
        Map<String, Object> raw = new LinkedHashMap<String, Object>();
        
        Object value = new String[]{"value"};
        raw.put("test", value);
        
        String result = HttpUtils.replaceDynamicName("{test}", raw);
        AssertJUnit.assertEquals("value", result);
    }

}

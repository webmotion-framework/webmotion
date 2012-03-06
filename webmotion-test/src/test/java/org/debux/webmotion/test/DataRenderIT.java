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
package org.debux.webmotion.test;

import java.io.IOException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test data render.
 * 
 * @author julien
 */
public class DataRenderIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(DataRenderIT.class);
    
    @Test
    public void xml() throws IOException {
        String url = getAbsoluteUrl("xml");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("John"));
        AssertJUnit.assertTrue(result.contains("azerty"));
        AssertJUnit.assertTrue(result.contains("77"));
    }
    
    @Test
    public void json() throws IOException {
        String url = getAbsoluteUrl("json");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("{\"name\":\"John\",\"passwd\":\"azerty\",\"age\":77}"));
    }
    
    @Test
    public void jsonp() throws IOException {
        String url = getAbsoluteUrl("jsonp");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("test({\"name\":\"John\",\"passwd\":\"azerty\",\"age\":77});"));
    }
    
    @Test
    public void stringTemplate() throws IOException {
        String url = getAbsoluteUrl("template");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("bla bla bla ..."));
    }
    
}

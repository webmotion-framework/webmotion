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
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * First IT.
 * 
 * @author julien
 */
public class HelloWorldMappingIT {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldMappingIT.class);
    
    @Test
    public void testGetResourcesDirectory() throws IOException {
        HttpGet request = new HttpGet("http://localhost:8080/webmotion-test/hello");
        HttpClient client = new DefaultHttpClient();
        
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        String value = IOUtils.toString(content);
        
        AssertJUnit.assertTrue(value.contains("Hello world !"));
    }
    
}

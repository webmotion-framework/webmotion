/*
 * #%L
 * Webmotion in action
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
package org.debux.webmotion.test;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test misc action.
 * 
 * @author julien
 */
public class ServerMiscIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(ServerMiscIT.class);
    
    @Test
    public void serverListenner() throws IOException, URISyntaxException {
        Request request = createRequest("/context")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("key = value"));
    }
    
    @Test
    public void globalListenner() throws IOException, URISyntaxException {
        Request request = createRequest("/global/hello")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello from logger !"));
    }
        
    @Test
    public void injectorListenner() throws IOException, URISyntaxException {
        Request request = createRequest("/config")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("A value from the config object"));
    }
        
    @Test
    public void converterListenner() throws IOException, URISyntaxException {
        Request request = createRequest("/jsonelement")
                .addParameter("element", "{test=\"value\"}")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("value"));
    }

}

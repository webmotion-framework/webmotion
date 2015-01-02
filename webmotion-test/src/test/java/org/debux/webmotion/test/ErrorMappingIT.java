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

import org.debux.webmotion.server.tools.StringResponseHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on mapping section error.
 * 
 * @author julien
 */
public class ErrorMappingIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(ErrorMappingIT.class);
    
    @Test
    public void codeHttp() throws IOException, URISyntaxException {
        Request request = createRequest("/parse")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Error server with code 500"));
    }
    
    @Test
    public void exception() throws IOException, URISyntaxException {
        Request request = createRequest("/service")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Error during the call"));
    }
    
    @Test
    public void all() throws IOException, URISyntaxException {
        Request request = createRequest("/notfound")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Error server with code 404"));
    }
    
    @Test
    public void view() throws IOException, URISyntaxException {
        Request request = createRequest("/npe")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Error on the server"));
    }
    
}

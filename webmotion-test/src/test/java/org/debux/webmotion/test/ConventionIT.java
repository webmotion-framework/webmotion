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
import java.net.URISyntaxException;
import org.apache.http.client.fluent.Request;
import org.debux.webmotion.server.tools.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test convention action.
 * 
 * @author julien
 */
public class ConventionIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(ConventionIT.class);
    
    @Override
    public RequestBuilder createRequest(String url) {
        RequestBuilder builder = super.createRequest(url);
        builder.setPath("/webmotion-test" + url);
        return builder;
    }
    
    @Test
    public void hello() throws IOException, URISyntaxException {
        Request request = createRequest("/hello/convention/says")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello by convention !"));
    }

}

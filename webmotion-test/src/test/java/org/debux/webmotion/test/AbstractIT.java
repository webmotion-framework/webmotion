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
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.DefaultHttpClient;
import org.debux.webmotion.server.tools.RequestBuilder;
import org.debux.webmotion.server.tools.StringResponseHandler;

/**
 * Define the basic information to create et execute a request for all tests.
 * 
 * @author julien
 */
public class AbstractIT {
    
    public RequestBuilder createRequest(String url) {
        RequestBuilder builder = new RequestBuilder()
                .setScheme("http")
                .setHost("localhost")
                .setPort(8090)
//                .setPort(8080)
//                .setPort(9080)
                .setPath("/webmotion-test/test" + url);
//                .setPath("/webmotion-website/test" + url);
        return builder;
    }
    
    /**
     * Guarantee separation between each test because multiple tests are in same
     * time.
     */
    public String executeRequest(Request request) throws IOException {
        return Executor.newInstance(new DefaultHttpClient())
                .execute(request)
                .handleResponse(new StringResponseHandler());
    }
    
}

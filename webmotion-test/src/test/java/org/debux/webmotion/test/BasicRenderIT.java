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
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test basic render.
 * 
 * @author julien
 */
public class BasicRenderIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(BasicRenderIT.class);
    
    @Test
    public void view() throws IOException {
        String url = getAbsoluteUrl("index");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Lorem ipsum"));
    }
    
    @Test
    public void model() throws IOException {
        String url = getAbsoluteUrl("helloModel");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Hello WebMotion !"));
    }
    
    @Test
    public void url() throws IOException {
        String url = getAbsoluteUrl("save");
        HttpGet request = new HttpGet(url);
        
        HttpParams params = new BasicHttpParams();
        HttpClientParams.setRedirecting(params, false);

        HttpClient client = new DefaultHttpClient(params);
        HttpResponse response = client.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        AssertJUnit.assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, statusCode);
        
        String location = response.getLastHeader("Location").getValue();
        AssertJUnit.assertTrue(location.endsWith("?id=373736"));
    }
    
    @Test
    public void action() throws IOException {
        String url = getAbsoluteUrl("first");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Next action with value"));
        AssertJUnit.assertTrue(result, result.contains("test"));
    }
    
    @Test
    public void actionForward() throws IOException {
        String url = getAbsoluteUrl("internal");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Execute internal"));
    }
    
    @Test
    public void absoluteRedirect() throws IOException {
        String url = getAbsoluteUrl("redirect");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Hello internal !"));
    }
    
    @Test
    public void content() throws IOException {
        String url = getAbsoluteUrl("content");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Content"));
    }
    
    @Test
    public void stream() throws IOException {
        String url = getAbsoluteUrl("stream");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertFalse(result, result.isEmpty());
    }
    
    @Test
    public void download() throws IOException {
        String url = getAbsoluteUrl("application/download");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertFalse(result, result.isEmpty());
    }
    
    @Test
    public void reload() throws IOException {
        String url = getAbsoluteUrl("reload");
        HttpGet request = new HttpGet(url);
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Error server with code 500"));
    }
    
    @Test
    public void error() throws IOException {
        String url = getAbsoluteUrl("forbidden");
        HttpGet request = new HttpGet(url);
        
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        AssertJUnit.assertEquals(HttpServletResponse.SC_FORBIDDEN, statusCode);
    }
    
    @Test
    public void status() throws IOException {
        String url = getAbsoluteUrl("nocontent");
        HttpGet request = new HttpGet(url);
        
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        AssertJUnit.assertEquals(HttpServletResponse.SC_NO_CONTENT, statusCode);
    }
    
}

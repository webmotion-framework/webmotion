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
import java.util.List;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test misc action.
 * 
 * @author julien
 */
public class ActionMiscIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(ActionMiscIT.class);
    
    @Test
    public void include() throws IOException {
        String url = getAbsoluteUrl("page");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Title lorem ipsum"));
        AssertJUnit.assertTrue(result.contains("Author lorem ipsum"));
        AssertJUnit.assertTrue(result.contains("Powered by WikiMotion and WebMotion"));
    }
    
    @Test
    public void flashMessage() throws IOException {
        String url = getAbsoluteUrl("message");
        HttpGet request = new HttpGet(url);
        
        DefaultHttpClient client = new DefaultHttpClient();
        client.execute(request);
        
        CookieStore cookieStore = client.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if ("wm_flash_infos_my_message".equals(name)) {
                AssertJUnit.assertEquals("\"bla bla bla bla\"", value);
                return;
            }
        }
        throw new RuntimeException("Invalid cookie");
    }
    
    @Test
    public void validationBean() throws IOException {
        String url = getAbsoluteUrl("create?book.isbn=007&book.title=James%20Bond");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Book created"));
    }
    
    @Test
    public void validationInvalidBean() throws IOException {
        String url = getAbsoluteUrl("create?book.isbn=007&book.title=James");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Shop#create(arg0).title :"));
    }
    
    @Test
    public void validationGroup() throws IOException {
        String url = getAbsoluteUrl("comment?book.isbn=007&book.comment=cool");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Comment saved"));
    }
    
    @Test
    public void validationInvalidGroup() throws IOException {
        String url = getAbsoluteUrl("comment?book.isbn=007");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Shop#comment(arg0).comment :"));
    }
    
    @Test
    public void validation() throws IOException {
        String url = getAbsoluteUrl("search?query=007");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Not found"));
    }
    
    @Test
    public void validationInvalid() throws IOException {
        String url = getAbsoluteUrl("search");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Shop#search(arg0) :"));
    }
    
    @Test
    public void noValidation() throws IOException {
        String url = getAbsoluteUrl("info?book.isbn=007");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Not found"));
    }
    
    @Test
    public void async() throws IOException {
        String url = getAbsoluteUrl("async");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("Hello world !"));
    }
    
    @Test
    public void serverListenner() throws IOException {
        String url = getAbsoluteUrl("context");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result.contains("key = value"));
    }
    
}

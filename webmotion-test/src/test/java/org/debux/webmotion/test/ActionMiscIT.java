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
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
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
        AssertJUnit.assertTrue(result, result.contains("Title lorem ipsum"));
        AssertJUnit.assertTrue(result, result.contains("Author lorem ipsum"));
        AssertJUnit.assertTrue(result, result.contains("Powered by WikiMotion and WebMotion"));
    }
    
    @Test
    public void includeAsync() throws IOException {
        String url = getAbsoluteUrl("include");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Title lorem ipsum"));
        AssertJUnit.assertTrue(result, result.contains("Author lorem ipsum"));
        AssertJUnit.assertTrue(result, result.contains("Powered by WikiMotion and WebMotion"));
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
    public void cookieManagerCreate() throws IOException {
        String url = getAbsoluteUrl("cookie/create?secured=true");
        HttpGet request = new HttpGet(url);
        
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        String result = IOUtils.toString(content);
        AssertJUnit.assertTrue(result, result.contains("Value = a_value"));
        
        CookieStore cookieStore = client.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if ("secured_name".equals(name)) {
                AssertJUnit.assertTrue(value.startsWith("me|-1"));
                return;
            }
        }
        throw new RuntimeException("Invalid cookie");
    }
    
    @Test
    public void cookieManagerRead() throws IOException {
        String url = getAbsoluteUrl("cookie/read?secured=false");
        HttpGet request = new HttpGet(url);
        
        DefaultHttpClient client = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        client.setCookieStore(cookieStore);
        
        BasicClientCookie initialCookie = new BasicClientCookie("name", "test");
        initialCookie.setVersion(1);
        initialCookie.setDomain("localhost");
        initialCookie.setPath("/webmotion-test/test/cookie");
        cookieStore.addCookie(initialCookie);
        
        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        String result = IOUtils.toString(content);
        AssertJUnit.assertTrue(result, result.contains("Value = test"));
    }
    
    @Test
    public void cookieManagerObjectCreate() throws IOException {
        String url = getAbsoluteUrl("cookie/object/create?value=test");
        HttpGet request = new HttpGet(url);
        
        DefaultHttpClient client = new DefaultHttpClient();
        client.execute(request);
        
        CookieStore cookieStore = client.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if ("user_cookie".equals(name)) {
                value = value.replaceAll("\\\\", "");
                AssertJUnit.assertEquals("{\"value\":\"test\"}", value);
                return;
            }
        }
        throw new RuntimeException("Invalid cookie");
    }
    
    @Test
    public void cookieManagerObjectRemove() throws IOException {
        String url = getAbsoluteUrl("cookie/object/remove");
        HttpGet request = new HttpGet(url);
        
        DefaultHttpClient client = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        client.setCookieStore(cookieStore);
        
        BasicClientCookie initialCookie = new BasicClientCookie("user_cookie", "{}");
        initialCookie.setVersion(1);
        initialCookie.setDomain("localhost");
        initialCookie.setPath("/webmotion-test/test/cookie/object");
        cookieStore.addCookie(initialCookie);
        
        client.execute(request);

        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if ("user_cookie".equals(name)) {
                throw new RuntimeException("Invalid cookie");
            }
        }
    }
    
    @Test
    public void cookieManagerObjectRead() throws IOException {
        String url = getAbsoluteUrl("cookie/object/read");
        HttpGet request = new HttpGet(url);
        
        DefaultHttpClient client = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        client.setCookieStore(cookieStore);
        
        BasicClientCookie initialCookie = new BasicClientCookie("user_cookie", "{\\\"value\\\":\\\"test\\\"}");
        initialCookie.setVersion(1);
        initialCookie.setDomain("localhost");
        initialCookie.setPath("/webmotion-test/test/cookie/object");
        cookieStore.addCookie(initialCookie);
        
        HttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        String result = IOUtils.toString(content);
        AssertJUnit.assertTrue(result, result.contains("Current value = test"));
    }
    
    @Test
    public void cookieManagerNullObjectRead() throws IOException {
        String url = getAbsoluteUrl("cookie/object/read");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Current value is empty"));
    }
        
    @Test
    public void validationBean() throws IOException {
        String url = getAbsoluteUrl("create?book.isbn=007&book.title=James%20Bond");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Book created"));
    }
    
    @Test
    public void validationInvalidBean() throws IOException {
        String url = getAbsoluteUrl("create?book.isbn=007&book.title=James");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Shop#create(arg0).title :"));
    }
    
    @Test
    public void validationGroup() throws IOException {
        String url = getAbsoluteUrl("comment?book.isbn=007&book.comment=cool");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Comment saved"));
    }
    
    @Test
    public void validationInvalidGroup() throws IOException {
        String url = getAbsoluteUrl("comment?book.isbn=007");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Shop#comment(arg0).comment :"));
    }
    
    @Test
    public void validation() throws IOException {
        String url = getAbsoluteUrl("search?query=007");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Not found"));
    }
    
    @Test
    public void validationInvalid() throws IOException {
        String url = getAbsoluteUrl("search");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Shop#search(arg0) :"));
    }
    
    @Test
    public void noValidation() throws IOException {
        String url = getAbsoluteUrl("info?book.isbn=007");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Not found"));
    }
    
    @Test
    public void async() throws IOException {
        String url = getAbsoluteUrl("async");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Hello world !"));
    }
        
    @Test
    public void session() throws IOException {
        String url = getAbsoluteUrl("session/store?value=test");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("test"));
    }
    
}

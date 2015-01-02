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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on mapping section extension.
 * 
 * @author julien
 */
public class ExtrasMiscIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(ExtrasMiscIT.class);
    
    @Test
    public void spring() throws IOException, URISyntaxException {
        Request request = createRequest("/spring")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello Spring !"));
    }
    
    @Test
    public void sitemesh() throws IOException, URISyntaxException {
        Request request = createRequest("/sitemesh/content")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("SiteMesh example site"));
    }
    
    @Test
    public void noteCreate() throws IOException, URISyntaxException {
        Request request = createRequest("/note/create")
                .addParameter("content", "test")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("test"));
        
        String id = StringUtils.substringBetween(result, "?id=", "\"");
        request = createRequest("/note/incLike")
                .addParameter("id", id)
                .Get();
        
        result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("<td>1</td>"));
        
        request = createRequest("/note/delete")
                .addParameter("id", id)
                .Get();
        
        result = executeRequest(request);
        AssertJUnit.assertFalse(result, result.contains(id));
    }
    
    @Test
    public void shiroLogin() throws IOException, URISyntaxException {
        Request request = createRequest("/auth/admin/index")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Login"));
    }
    
    @Test
    public void shiroError() throws IOException, URISyntaxException {
        Request request = createRequest("/auth/admin/index")
                .addParameter("username", "aa")
                .addParameter("password", "aa")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("The username or password is incorrect !"));
    }
    
    @Test
    public void shiroForbidden() throws IOException, URISyntaxException {
        Request request = createRequest("/auth/admin/index")
                .addParameter("username", "guest")
                .addParameter("password", "guest")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Access denied"));
    }
    
    @Test
    public void shiroAuthorized() throws IOException, URISyntaxException {
        Request request = createRequest("/auth/guest/index")
                .addParameter("username", "guest")
                .addParameter("password", "guest")
                .Get();
        
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Guest page"));
    }

}

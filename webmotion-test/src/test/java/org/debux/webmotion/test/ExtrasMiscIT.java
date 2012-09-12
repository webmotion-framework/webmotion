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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
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
    public void spring() throws IOException {
        String url = getAbsoluteUrl("spring");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Hello Spring !"));
    }
    
    @Test
    public void sitemesh() throws IOException {
        String url = getAbsoluteUrl("sitemesh/content");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("SiteMesh example site"));
    }
    
    @Test
    public void noteCreate() throws IOException {
        String url = getAbsoluteUrl("note/create?content=test");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("test"));
        
        String id = StringUtils.substringBetween(result, "?id=", "\"");
        url = getAbsoluteUrl("note/incLike?id=" + id);
        request = new HttpGet(url);
     
        result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("<td>1</td>"));
        
        url = getAbsoluteUrl("note/delete?id=" + id);
        request = new HttpGet(url);
     
        result = execute(request);
        AssertJUnit.assertFalse(result, result.contains(id));
    }
    
    @Test
    public void shiroLogin() throws IOException {
        String url = getAbsoluteUrl("auth/admin/index");
        HttpGet request = new HttpGet(url);
        
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Login"));
    }
    
    @Test
    public void shiroError() throws IOException {
        String url = getAbsoluteUrl("auth/admin/index?username=aa&password=aa");
        HttpGet request = new HttpGet(url);
     
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("The username or password is incorrect !"));
    }
    
    
    @Test
    public void shiroForbidden() throws IOException {
        String url = getAbsoluteUrl("auth/admin/index?username=guest&password=guest");
        HttpGet request = new HttpGet(url);
     
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Access denied"));
    }
    
    @Test
    public void shiroAuthorized() throws IOException {
        String url = getAbsoluteUrl("auth/guest/index?username=guest&password=guest");
        HttpGet request = new HttpGet(url);
     
        String result = execute(request);
        AssertJUnit.assertTrue(result, result.contains("Guest page"));
    }

}

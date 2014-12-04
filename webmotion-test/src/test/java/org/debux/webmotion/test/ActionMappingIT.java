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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on mapping section action.
 * 
 * @author julien
 */
public class ActionMappingIT extends AbstractIT {

    private static final Logger log = LoggerFactory.getLogger(ActionMappingIT.class);
    
    @Test
    public void action() throws IOException, URISyntaxException {
        Request request = createRequest("/act")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Execute action"));
    }
    
    @Test
    public void simulate() throws IOException, URISyntaxException {
        Request request = createRequest("/simulate")
                .Post()
                .addHeader("X-HTTP-Method-Override", "DELETE");
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Execute action"));
    }
    
    @Test
    public void view() throws IOException, URISyntaxException {
        Request request = createRequest("/view")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello world !"));
    }
    
    @Test
    public void url() throws IOException, URISyntaxException {
        Request request = createRequest("/url")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("WebMotion"));
    }
    
    @Test
    public void redirectRelativeUrl() throws IOException, URISyntaxException {
        Request request = createRequest("/redirect/relative")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello world !"));
    }
    
    @Test
    public void redirectAbsoluteUrl() throws IOException, URISyntaxException {
        Request request = createRequest("/redirect/absolute")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello world !"));
    }
    
    @Test
    public void forwardRelativeUrl() throws IOException, URISyntaxException {
        Request request = createRequest("/forward")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello world !"));
    }
    
    @Test
    public void forwardAbsoluteUrl() throws IOException, URISyntaxException {
        Request request = createRequest("/forward/absolute")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello world !"));
    }
    
    @Test
    public void dynamicForward() throws IOException, URISyntaxException {
        Request request = createRequest("/forward/dynamic/get")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Execute get action"));
    }
    
    @Test
    public void helloParametersYou() throws IOException, URISyntaxException {
        Request request = createRequest("/helloParameters")
                .addParameter("who", "you")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello you !"));
    }
    
    @Test
    public void helloParametersMe() throws IOException, URISyntaxException {
        Request request = createRequest("/helloParameters")
                .addParameter("who", "me")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello me !"));
    }
    
    @Test
    public void helloDefaultParameters() throws IOException, URISyntaxException {
        Request request = createRequest("/helloDefaultParameters")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello default !"));
    }
    
    @Test
    public void helloDefaultParametersOther() throws IOException, URISyntaxException {
        Request request = createRequest("/helloDefaultParameters")
                .addParameter("who", "other")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello other !"));
    }
    
    @Test
    public void patternOnlyA() throws IOException, URISyntaxException {
        Request request = createRequest("/pattern/aaaa")
                .addParameter("value", "aaaa")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("The who aaaa and value aaaa contains only letter a"));
    }
    
    @Test
    public void patternNotOnlyAPath() throws IOException, URISyntaxException {
        Request request = createRequest("/pattern/baaa")
                .addParameter("value", "aaaaa")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("The who baaa and value aaaaa NOT contains only letter a"));
    }
    
    @Test
    public void patternNotOnlyAParam() throws IOException, URISyntaxException {
        Request request = createRequest("/pattern/aaaa")
                .addParameter("value", "baaaa")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("The who aaaa and value baaaa NOT contains only letter a"));
    }
    
    @Test
    public void loginParameterNamed() throws IOException, URISyntaxException {
        Request request = createRequest("/login")
                .addParameter("user.name", "john")
                .addParameter("user.passwd", "azerty")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Login with user name : john and passwd azerty"));
    }
    
    @Test
    public void loginParameterRenamed() throws IOException, URISyntaxException {
        Request request = createRequest("/login")
                .addParameter("username", "john")
                .addParameter("userpasswd", "azerty")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Login with user name : john and passwd azerty"));
    }
    
    @Test
    public void loginParameterExtended() throws IOException, URISyntaxException {
        Request request = createRequest("/auth")
                .addParameter("name", "john")
                .addParameter("passwd", "azerty")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Login with user name : john and passwd azerty"));
    }
    
    @Test
    public void login() throws IOException, URISyntaxException {
        Request request = createRequest("/login")
                .addParameter("name", "john")
                .addParameter("passwd", "azerty")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Login with user name : john and passwd azerty"));
    }
    
    @Test
    public void selectStatic() throws IOException, URISyntaxException {
        Request request = createRequest("/select")
                .addParameter("param", "value")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Select with parameter !"));
    }
    
    @Test
    public void select() throws IOException, URISyntaxException {
        Request request = createRequest("/select")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Select without parameter !"));
    }
    
    @Test
    public void dynamicActionGet() throws IOException, URISyntaxException {
        Request request = createRequest("/dynamic/get")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Execute get action"));
    }
    
    @Test
    public void dynamicActionSet() throws IOException, URISyntaxException {
        Request request = createRequest("/dynamic/set")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Execute set action"));
    }
    
    @Test
    public void dynamicViewReadme() throws IOException, URISyntaxException {
        Request request = createRequest("/text")
                .addParameter("file", "readme")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("It is the readme"));
    }
    
    @Test
    public void dynamicViewChangelog() throws IOException, URISyntaxException {
        Request request = createRequest("/text")
                .addParameter("file", "changelog")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("It is the changelog"));
    }
    
    @Test
    public void dynamicViewJohn() throws IOException, URISyntaxException {
        Request request = createRequest("/helloView")
                .addParameter("name", "John")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello John !"));
    }
    
    @Test
    public void dynamicViewJack() throws IOException, URISyntaxException {
        Request request = createRequest("/helloView")
                .addParameter("name", "Jack")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Hello Jack !"));
    }
    
    @Test
    public void dynamicUrlTutu() throws IOException, URISyntaxException {
        Request request = createRequest("/wikipedia/tutu")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Tutu"));
    }
    
    @Test
    public void dynamicUrlTata() throws IOException, URISyntaxException {
        Request request = createRequest("/wikipedia/tata")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Tata"));
    }
    
    @Test
    public void methodGet() throws IOException, URISyntaxException {
        Request request = createRequest("/person")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Return the person"));
    }
    
    @Test
    public void methodPost() throws IOException, URISyntaxException {
        Request request = createRequest("/person")
                .Post();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Save the person"));
    }
    
    @Test
    public void multiMethodGet() throws IOException, URISyntaxException {
        Request request = createRequest("/video")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Get the media"));
    }
    
    @Test
    public void multiMethodPost() throws IOException, URISyntaxException {
        Request request = createRequest("/video")
                .Post();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Get the media"));
    }
    
    @Test
    public void staticResourceText() throws IOException, URISyntaxException {
        Request request = createRequest("/readme")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertTrue(result, result.contains("Dummy readme"));
    }
    
    @Test
    public void staticResourceImg() throws IOException, URISyntaxException {
        Request request = createRequest("/img")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertFalse(result, result.isEmpty());
    }
    
    @Test
    public void hellos() throws IOException, URISyntaxException {
        Request request = createRequest("/hellos?names[0]=me&names[1]=you")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertFalse(result, result.contains("Hello me&you !"));
    }
    
    @Test
    public void helloNames() throws IOException, URISyntaxException {
        Request request = createRequest("/helloNames?names.values[0]=me&names.values[1]=you")
                .Get();
                
        String result = executeRequest(request);
        AssertJUnit.assertFalse(result, result.contains("Hello me&you !"));
    }
    
}

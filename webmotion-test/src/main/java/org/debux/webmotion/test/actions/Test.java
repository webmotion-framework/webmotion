/*
 * #%L
 * Webmotion in test
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
package org.debux.webmotion.test.actions;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.test.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Test extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public Render index() {
        log.info("index");
        return renderView("test/hello.jsp", 
                "key1", "value1",
                "key2", "value2"
            );
    }

    public Render indexWithMessage() {
        HttpContext context = getContext();
        context.addInfoMessage("hello", "Hello world !");
        return renderView("test/hello.jsp", 
                "key1", "value1",
                "key2", "value2"
            );
    }

    public Render create() {
        return renderContent("create", "text/html");
    }

    public Render action() {
        return renderAction("Test.hello", 
                "number", 4,
                "value", "aaaaaaaa"
            );
    }

    public Render run() {
        return renderContent("run", "text/html");
    }

    public Render hello(String value, int number) {
        String render = ">>> " + value + " / " + number + " ";
        for (int index = 0; index < number; index ++) {
            render += "hello " + value + "! ";
        }
        return renderContent(render, "text/html");
    }

    public Render all() {
        return renderContent("all", "text/html");
    }

    public static class MyObject {
        protected String aString = "a string";
        protected long aLong = 15564157894l;
    }
    
    public Render json() {
        return renderJSON("test", new MyObject());
    }
    
    public Render jsonp() {
        return renderJSONP("test", new MyObject());
    }
    
    public Render xml() {
        return renderXML("test", new MyObject());
    }
    
    public Render jsons() {
        return renderJSON(
                "MyObject1", new MyObject(),
                "MyObject2", new MyObject(),
                "MyObject3", new MyObject()
            );
    }
    
    public Render npe() {
        throw new NullPointerException("Test");
    }
    
    public Render echo(String echo) {
        return renderContent(echo, "text/html");
    }
    
    public Render echoUser(User user) {
        return renderContent(user.toString(), "text/html");
    }
    
    public Render echoList(List<Integer> list) {
        return renderContent(list.toString(), "text/html");
    }
    
    public Render echoListUser(List<User> users) {
        return renderContent(users.toString(), "text/html");
    }
    
    public Render echoMap(Map<String, Integer> map) {
        return renderContent(map.toString(), "text/html");
    }
    
    public Render echoMapUser(Map<String, User> users) {
        return renderContent(users.toString(), "text/html");
    }
    
    public Render echoArray(String[] array) {
        return renderContent(Arrays.toString(array), "text/html");
    }
    
    public Render echoArrayUser(User[] users) {
        return renderContent(Arrays.toString(users), "text/html");
    }
    
    public Render echoDate(Timestamp date) {
        return renderContent(date.toString(), "text/html");
    }
    
    public Render reload() {
        HttpContext context = getContext();
        context.addInfoMessage("hello", "Hello world !");
        return renderLastPage();
    }
    
    public Render template() {
        return renderView("test/template.jsp", 
                "key1", "value1",
                "key2", "value2"
            );
    }
    
    public Render error(int code) {
        return renderError(code, "Test erreur");
    }
    
    public Render moduleView() {
        return renderContent("The module view", "text/html");
    }
    
    public void moduleNull() {
        log.info("Pass in module");
    }
    
    public Render status() {
        return renderStatus(302);
    }
    
}

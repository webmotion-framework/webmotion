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
package org.debux.webmotion.server.handler;

import java.util.HashMap;
import java.util.Map;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 * Test on find url in mapping
 * 
 * @author julien
 */
public class ActionFinderHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(ActionFinderHandlerTest.class);

    @Factory
    public Object[] testFactory() {
        return new Object[]{
            new RunHandler("*", "/", "action",
                           "*    /    action"),
            
            new RunHandler("*", "/root", "action",
                           "*    /root    action"),
            
            new RunHandler("*", "/test", "action2",
                           "*    /other   action1",
                           "*    /test    action2"),
                
            new RunHandler("GET", "/test", "action1",
                           "GET    /test    action1",
                           "*      /test    action2"),
            
            new RunHandler("*", "/test?param=test", "action",
                           "*    /test               action"),
            
//            new RunHandler("*", "/test?param=test", "action",
//                           "*    /test?other={value} action"),
            
            new RunHandler("*", "/test?param=test", "action1",
                           "*    /test?param={value} action1",
                           "*    /test               action2"),
                
            new RunHandler("*", "/test?param",      "action",
                           "*    /test?param         action"),
            
            new RunHandler("*", "/test/run",   "action2",
                           "*    /test          action1",
                           "*    /test/{action} action2"),
                
            new RunHandler("*", "/test", "action1",
                           "*    /test    action1",
                           "*    /test/   action2"),
                
            new RunHandler("*", "/test/", "action2",
                           "*    /test     action1",
                           "*    /test/    action2"),
                
            new RunHandler("*", "/test?param", "action2",
                           "*    /test/?param   action1",
                           "*    /test?param    action2"),
                
            new RunHandler("*", "/test/?param", "action1",
                           "*    /test/?param    action1",
                           "*    /test?param     action2"),
            
        };
    }
    
    public class RunHandler {
        
        protected String method;
        protected String url;
        protected String result;
        protected String[] mappingContent;
        
        protected ActionFinderHandler handler;

        public RunHandler(String method, String url, String result, String... mappingContent) {
            this.method = method;
            this.url = url;
            this.result = result;
            this.mappingContent = mappingContent;
            this.handler = new ActionFinderHandler();
        }
        
        @Test
        public void match() {
            Mapping mapping = new Mapping();
            for (String line : mappingContent) {
                line = line.trim();
                line = line.replaceAll(" +", " ");
                mapping.extractSectionActions(line);
            }
            
            Call call = new CallWrapper(method, url);
            
            ActionRule actionRule = handler.getActionRule(mapping, call);
            AssertJUnit.assertNotNull(actionRule);
            AssertJUnit.assertEquals(result, actionRule.getAction().getFullName());
        }
    }
    
    public static class CallWrapper extends Call {

        protected String method;
        protected String url;
        protected Map<String, Object> parameters;
        
        public class ContextWrapper extends HttpContext {
            public ContextWrapper() {
            }
        
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public String getMethod() {
                return method;
            }
        }

        public CallWrapper(String method, String url) {
            this.method = method;
            
            String[] urlSeparated = url.split("\\?");
            this.url = urlSeparated[0];
            
            parameters = new HashMap<String, Object>();
            if(urlSeparated.length == 2) {
                String[] querySearch = urlSeparated[1].split("&|=");
                for (int index = 0; index < querySearch.length; index += 2) {
                    String name = querySearch[index];
                    String[] value = null;
                    if(index + 1 < querySearch.length) {
                        value = new String[] {querySearch[index + 1]};
                    } else {
                        value = new String[0];
                    }
                    parameters.put(name, value);
                }
            }
        }

        @Override
        public HttpContext getContext() {
            return new ContextWrapper();
        }

        @Override
        public Map<String, Object> getExtractParameters() {
            return parameters;
        }
    }
}

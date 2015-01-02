/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.tools.HttpUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.FragmentUrl;
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

    /** Allowed characted are all alphanumeric characters, all punctuation characters exception following: <pre>!*'();:@&=+$,/?#[]</pre> */
    protected static final String ALLOWED_CHARACTERS = "[\\p{Alnum}\\p{Punct}&&[^!\\*'\\(\\);:@&=+$,\\/\\?#\\[\\]]]";
    protected static Pattern patternParam = Pattern.compile("^(" + ALLOWED_CHARACTERS + "*)=\\{(" + ALLOWED_CHARACTERS + "*)(:)?(.*)?\\}$");
    protected static Pattern patternStaticParam = Pattern.compile("^(" + ALLOWED_CHARACTERS + "*)=(" + ALLOWED_CHARACTERS + "*)$");
    protected static Pattern patternPath = Pattern.compile("^\\{(\\p{Alnum}*)(:)?(.*)?\\}$");
    
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
            List<ActionRule> actionRules = mapping.getActionRules();
            for (String line : mappingContent) {
                line = line.trim();
                line = line.replaceAll(" +", " ");
                
                ActionRule actionRule = extractSectionActions(line);
                actionRules.add(actionRule);
            }
            
            Call call = new CallWrapper(method, url);
            
            ActionRule actionRule = handler.getActionRule(mapping, call);
            AssertJUnit.assertNotNull(actionRule);
            AssertJUnit.assertEquals(result, actionRule.getAction().getFullName());
        }
        
        public ActionRule extractSectionActions(String line) {
            String[] splitRule = line.split(" ");
            ActionRule actionRule = new ActionRule();

            List<String> methods = Arrays.asList(splitRule[0]);
            actionRule.setMethods(methods);

            List<FragmentUrl> url = extractUrl(splitRule[1]);
            actionRule.setRuleUrl(url);

            List<FragmentUrl> parameters = extractParameters(splitRule[1]);
            actionRule.setRuleParameters(parameters);

            Action action = extractAction(splitRule[2]);
            actionRule.setAction(action);

            return actionRule;
        }
        
        protected List<FragmentUrl> extractUrl(String fragment) {
            log.debug("fragment = " + fragment);
            List<FragmentUrl> ruleUrl = new ArrayList<FragmentUrl>();
            String baseUrl = StringUtils.substringBefore(fragment, "?");
            if(!baseUrl.isEmpty()) {

                List<String> splitBaseUrl = HttpUtils.splitPath(baseUrl);
                log.debug("splitBaseUrl = " + splitBaseUrl);

                for(String item : splitBaseUrl) {
                    FragmentUrl expression = extractExpression(item, false);
                    ruleUrl.add(expression);
                }
            }
            return ruleUrl;
        }
        
        protected List<FragmentUrl> extractParameters(String fragment) {
            List<FragmentUrl> ruleParameters = new ArrayList<FragmentUrl>();
            String queryString = StringUtils.substringAfter(fragment, "?");
            if (!queryString.isEmpty()) {

                String[] splitQueryString = StringUtils.splitPreserveAllTokens(queryString, "&");
                log.debug("splitQueryString = " + Arrays.toString(splitQueryString));

                for (String item : splitQueryString) {
                    FragmentUrl expression = extractExpression(item, true);
                    ruleParameters.add(expression);
                }
            }
            return ruleParameters;
        }

        protected FragmentUrl extractExpression(String value, boolean isParam) {
            FragmentUrl expression = new FragmentUrl();

            Matcher matcherPath = patternPath.matcher(value);
            Matcher matcherParam = patternParam.matcher(value);
            Matcher matcherStaticParam = patternStaticParam.matcher(value);

            String pattern = null;
            if (matcherPath.find()) {
                expression.setName(matcherPath.group(1));
                pattern = matcherPath.group(3);

            } else if (matcherParam.find()) {
                expression.setParam(matcherParam.group(1));
                expression.setName(matcherParam.group(2));
                pattern = matcherParam.group(4);

            } else if (matcherStaticParam.find()) {
                expression.setParam(matcherStaticParam.group(1));
                pattern = matcherStaticParam.group(2);

            } else if (isParam) {
                expression.setParam(value);

            } else {
                pattern = value;
            }

            if (pattern != null && !pattern.isEmpty()) {
                expression.setPattern(Pattern.compile("^" + pattern + "$"));
            }

            return expression;
        }
        
        protected Action extractAction(String ruleAction) {
            Action action = new Action();

            String[] split = ruleAction.split(":");
            action.setFullName(split[0]);
            action.setType(Action.Type.ACTION);

            return action;
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

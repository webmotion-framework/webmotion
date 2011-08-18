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
package org.debux.webmotion.server.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a action for an url. The action is executed when the url matched 
 * and http method.
 * 
 * @author julien
 */
public class ActionRule {

    private static final Logger log = LoggerFactory.getLogger(ActionRule.class);
    
    /** Allowed characted are all alphanumeric characters, all punctuation characters exception following: <pre>!*'();:@&=+$,/?#[]</pre> */
    protected static final String ALLOWED_CHARACTERS = "[\\p{Alnum}\\p{Punct}&&[^!\\*'\\(\\);:@&=+$,\\/\\?#\\[\\]]]";
    protected static Pattern patternParam = Pattern.compile("^(" + ALLOWED_CHARACTERS + "*)=\\{(" + ALLOWED_CHARACTERS + "*)(:)?(.*)?\\}$");
    protected static Pattern patternStaticParam = Pattern.compile("^(" + ALLOWED_CHARACTERS + "*)=(" + ALLOWED_CHARACTERS + "*)$");
    protected static Pattern patternPath = Pattern.compile("^(" + ALLOWED_CHARACTERS + "*)\\{(" + ALLOWED_CHARACTERS + "*)(:)?(.*)?\\}(" + ALLOWED_CHARACTERS + "*)$");

    protected String method;
    protected List<URLPattern> ruleUrl;
    protected List<URLPattern> ruleParameters;
    protected Action action;
    protected Map<String, String[]> defaultParameters = new LinkedHashMap<String, String[]>();

    public ActionRule() {
        ruleUrl = new ArrayList<URLPattern>();
        ruleParameters = new ArrayList<URLPattern>();
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<URLPattern> getRuleParameters() {
        return ruleParameters;
    }

    public void setRuleParameters(List<URLPattern> ruleParameters) {
        this.ruleParameters = ruleParameters;
    }

    public List<URLPattern> getRuleUrl() {
        return ruleUrl;
    }

    public void setRuleUrl(List<URLPattern> ruleUrl) {
        this.ruleUrl = ruleUrl;
    }

    public Map<String, String[]> getDefaultParameters() {
        return defaultParameters;
    }

    /**
     * Extract the http method. * represents all http method else you can put 
     * value GET, POST, UPDATE, DELETE, HEADER and PUT.
     * @param method 
     */
    public void extractMethod(String method) {
        this.method = method;
    }

    /**
     * Extract the url pattern
     * @param url 
     */
    public void extractURLPattern(String urlPattern) {
        log.info("urlPattern = " + urlPattern);
        
        String baseUrl = StringUtils.substringBefore(urlPattern, "?");
        if(!baseUrl.isEmpty()) {
            
            String[] splitBaseUrl = StringUtils.splitPreserveAllTokens(baseUrl, "/");
            log.info("splitBaseUrl = " + Arrays.toString(splitBaseUrl));

            for(String item : splitBaseUrl) {
                URLPattern expression = extractExpression(item, false);
                ruleUrl.add(expression);
            }
        }
        
        String queryString = StringUtils.substringAfter(urlPattern, "?");
        if(!queryString.isEmpty()) {
            
            String[] splitQueryString = StringUtils.splitPreserveAllTokens(queryString, "&");
            log.info("splitQueryString = " + Arrays.toString(splitQueryString));

            for(String item : splitQueryString) {
                URLPattern expression = extractExpression(item, true);
                ruleParameters.add(expression);
            }
        }
    }

    /**
     * Extract a fragment of url patern
     * @param value the fragment
     * @return memory representation
     */
    protected URLPattern extractExpression(String value, boolean isParam) {
        URLPattern expression = new URLPattern();

        Matcher matcherPath = patternPath.matcher(value);
        Matcher matcherParam = patternParam.matcher(value);
        Matcher matcherStaticParam = patternStaticParam.matcher(value);

        String pattern = null;
        if(matcherPath.find()) {
            expression.setName(matcherPath.group(1));
            pattern = matcherPath.group(3);
            
        } else if(matcherParam.find()) {
            expression.setParam(matcherParam.group(1));
            expression.setName(matcherParam.group(2));
            pattern = matcherParam.group(4);
            
        } else if(matcherStaticParam.find()) {
            expression.setParam(matcherStaticParam.group(1));
            pattern = matcherStaticParam.group(2);

        } else if (isParam) {
            expression.setParam(value);
            
        } else {
            pattern = value;
        }

        if(pattern != null && !pattern.isEmpty()) {
            expression.setPattern(Pattern.compile("^" + pattern + "$"));
        }
        
        return expression;
    }

    /**
     * Extract the action to execute
     * @param ruleAction 
     */
    public void extractAction(String ruleAction) {
        int typeSeparatorIndex = ruleAction.indexOf(":");
        action = new Action();
        
        String value;
        if(typeSeparatorIndex == -1) {
            value = ruleAction;
        } else {
            action.setType(ruleAction.substring(0, typeSeparatorIndex));
            value = ruleAction.substring(typeSeparatorIndex + 1);
        }
        
        int packageSeparatorIndex = value.lastIndexOf(".");
        if(packageSeparatorIndex != -1) {
            action.setClassName(value.substring(0, packageSeparatorIndex));
            action.setMethodName(value.substring(packageSeparatorIndex + 1));
        }
        action.setFullName(value);
    }

    /**
     * Extract default parameters if not found the parameter in url
     * @param ruleParameters 
     */
    public void extractDefaultParameters(String ruleParameters) {
        String[] split = ruleParameters.split("=|,");
        for (int position = 0; position < split.length; position += 2) {
            String key = split[position];
            String[] value = {split[position + 1]};
            defaultParameters.put(key, value);
        }
    }

}

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

    public void setDefaultParameters(Map<String, String[]> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }

}

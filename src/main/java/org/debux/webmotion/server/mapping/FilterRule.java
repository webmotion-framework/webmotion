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

import java.util.regex.Pattern;

/**
 * Represents an filter for an url. The action is executed when the url and http
 * method matches. The url syntax likes the servlet filter. 
 * 
 * @author julien
 */
public class FilterRule {

    protected String method;
    protected Pattern pattern;
    protected Action action;

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

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Extract the http method
     * @param method 
     */
    public void extractMethod(String method) {
        this.method = method;
    }

    /**
     * Extract the url
     * @param rulePattern 
     */
    public void extractPattern(String rulePattern) {
        String regex = rulePattern.replaceAll("/\\*/", "/[^/]*/");
        regex = rulePattern.replaceAll("/\\*", "/.*");
        
        regex = "^" + regex + "$";
        pattern = Pattern.compile(regex);
    }

    /**
     * Extract the action to execute
     * @param ruleAction 
     */
    public void extractAction(String ruleAction) {
        action = new Action();
        int packageSeparatorIndex = ruleAction.lastIndexOf(".");
        action.setClassName(ruleAction.substring(0, packageSeparatorIndex));
        action.setMethodName(ruleAction.substring(packageSeparatorIndex + 1));
    }

}

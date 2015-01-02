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
package org.debux.webmotion.server.mapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents an filter for an url. The action is executed when the url and http
 * method matches. The url syntax likes the servlet filter. 
 * 
 * @author julien
 */
public class FilterRule extends Rule {

    protected List<String> methods;
    protected Pattern pattern;
    protected Map<String, String[]> defaultParameters;

    public FilterRule() {
        methods = new ArrayList<String>();
        defaultParameters = new LinkedHashMap<String, String[]>();
    }
    
    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, String[]> getDefaultParameters() {
        return defaultParameters;
    }

    public void setDefaultParameters(Map<String, String[]> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }
}

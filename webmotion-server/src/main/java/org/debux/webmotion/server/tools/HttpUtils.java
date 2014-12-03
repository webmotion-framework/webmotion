/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server.tools;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Call.ParameterTree;

/**
 * Util class.
 * 
 * @author jruchaud
 */
public class HttpUtils {
    
    /**
     * Cut the path like to list for example is /path/path to {/,path,/path}
     * @param path path to split
     * @return list represents the path
     */
    public static Pattern splitPathPattern = Pattern.compile("/|[^/$]+");
    public static List<String> splitPath(String path) {
        List<String> list = new ArrayList<String>();
        
        Matcher matcher = splitPathPattern.matcher(path);
        while(matcher.find()) {
            String group = matcher.group();
            list.add(group);
        }
        
        return list;
    }

    /** Pattern use for @see replaceDynamicName */
    protected static Pattern dynamicNamePattern = Pattern.compile("\\{(\\p{Alnum}*)\\}");
    
    /**
     * Replace all parameters like {paramName} by real value in request parameters
     * @param name name contain a param
     * @param parameters request parameters
     * @return name with parameter values
     */
    public static String replaceDynamicName(String name, Map<String, Object> rawParameters) {
        Matcher matcher = dynamicNamePattern.matcher(name);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            
            Object values = rawParameters.get(paramName);
            
            if (values.getClass().isArray()) {
                values = ((Object[]) values)[0];
            }
            
            if (values instanceof String) {
                String value = (String) values;
                name = name.replace("{" + paramName + "}", value);
            }
        }
        return name;
    }
    
    /**
     * Find the regex in input
     * @return true if the regex is found
     */
    public static boolean find(String regex, CharSequence input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
    
    /**
     * @return true if webmotion in Tomcat container.
     */
    public static boolean isTomcatContainer(ServletContext context) {
        String serverInfo = context.getServerInfo();
        return StringUtils.containsIgnoreCase(serverInfo, "tomcat");
    }

    /**
     * @return true if webmotion in Glassfish container.
     */
    public static boolean isGlassfishContainer(ServletContext context) {
        String serverInfo = context.getServerInfo();
        return StringUtils.containsIgnoreCase(serverInfo, "glassfish");
    }

    /**
     * @return true if webmotion in Jetty container.
     */
    public static boolean isJettyContainer(ServletContext context) {
        String serverInfo = context.getServerInfo();
        return StringUtils.containsIgnoreCase(serverInfo, "jetty");
    }
    
    /**
     * Generate a new secret key.
     * 
     * @return secret key;
     */
    public static String generateSecret() {
        return RandomStringUtils.random(31, true, true);
    }

}

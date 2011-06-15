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
package org.debux.webmotion.server;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.lang.reflect.Method;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Util class.
 * 
 * @author jruchaud
 */
public class WebMotionUtils {
    
    /**
     * Gets the names of the parameters for the specified method.
     * @param mapping The current loaded mapping configuration. It defines the way to access parameter names.
     * @param method The method for which the parameter names are looked up. 
     * @return The parameter names of the specified method.
     */
    public static String[] getParameterNames(Mapping mapping, Method method) {
        Config config = mapping.getConfig();
        Paranamer paranamer;
        
        // Test is reloadable, i.e. if java is compile to debug mode (-g)
        if(config.isReloadable()) {
            paranamer = new CachingParanamer(new BytecodeReadingParanamer());
        } else {
            paranamer = new CachingParanamer();
        }
        
        String[] parameterNames = paranamer.lookupParameterNames(method, false);
        return parameterNames;
    }
    
    /**
     * Get the first method corresponding with <code>name</code> parameter in the <code>clazz</code> class.
     * @param clazz The class to browse.
     * @param name The method name to search.
     * @return The first found method which name is <code>name</code>.
     * @throws WebMotionException If not any method corresponding with <code>name</code> 
     * parameter has been found in the <code>clazz</code> class.
     */
    public static Method getMethod(Class<WebMotionAction> clazz, String name) {
        Method[] all = clazz.getDeclaredMethods();

        for (Method method : all) {
            String methodName = method.getName();
            if(methodName.equals(name)) {
                return method;
            }
        }
        
        throw new WebMotionException("Method not found with name " + name + " on class " + clazz.getName());
    }
    
    /**
     * Capitalizes a full qualified class name.
     * Example:
     * <code>WebMotionUtils.capitalizeClass("org.webmotion.myclass")</code> will return 
     * <code>"org.webmotion.Myclass"</code>
     * @param className The class name to capitalize.
     * @return A capitalized representation for the given <code>className</code> class name.
     */
    public static String capitalizeClass(String className) {
        StringBuilder builder = new StringBuilder(className.length());
        
        // Search the class name in package
        int packageIndex = className.lastIndexOf(".");
        if(packageIndex != -1) {
            builder.append(className.substring(0, packageIndex + 1));
        }
        
        builder.append(Character.toUpperCase(className.charAt(packageIndex + 1)));
        builder.append(className.substring(packageIndex + 2));

        className = builder.toString();
        return className;
    }
    
    /**
     * Uncapitalizes a full qualified class name.
     * Example:
     * <code>WebMotionUtils.unCapitalizeClass("org.webmotion.MyClass")</code> will return 
     * <code>"org.webmotion.myClass"</code>
     * @param className The class name to uncapitalize.
     * @return A uncapitalized representation for the given <code>className</code> class name.
     */
    public static String unCapitalizeClass(String className) {
        StringBuilder builder = new StringBuilder(className.length());
        
        // Search the class name in package
        int packageIndex = className.lastIndexOf(".");
        if(packageIndex != -1) {
            builder.append(className.substring(0, packageIndex + 1));
        }
        
        builder.append(Character.toLowerCase(className.charAt(packageIndex + 1)));
        builder.append(className.substring(packageIndex + 2));

        className = builder.toString();
        return className;
    }
}

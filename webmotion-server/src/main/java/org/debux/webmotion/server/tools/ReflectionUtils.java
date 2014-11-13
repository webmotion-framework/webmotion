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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.scanners.ResourcesScanner;

/**
 * Util class.
 * 
 * @author jruchaud
 */
public class ReflectionUtils {
    
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
        if(config.isJavacDebug()) {
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
     * @return The first found method which name is <code>name</code> otherwise null.
     */
    public static Method getMethod(Class<?> clazz, String name) {
        Method[] all = clazz.getMethods();

        for (Method method : all) {
            String methodName = method.getName();
            if(methodName.equals(name)) {
                return method;
            }
        }
        
        return null;
    }
    
    /**
     * Determine if the class is primitive type even if the class is the wrapper 
     * on a primitive type.
     * @param clazz class to verify
     * @return true is a primitive type or wrapper on primitive type else false
     */    
    public static boolean isPrimitiveType(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz.equals(Boolean.class) || 
               clazz.equals(Integer.class) ||
               clazz.equals(Character.class) ||
               clazz.equals(Byte.class) ||
               clazz.equals(Short.class) ||
               clazz.equals(Double.class) ||
               clazz.equals(Long.class) ||
               clazz.equals(Float.class);
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
     * <code>WebMotionUtils.unCapitalizeClass("org.webmotion.Myclass")</code> will return 
     * <code>"org.webmotion.myclass"</code>
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

    /**
     * @return the system configuration path for os
     */
    public static String getSystemConfigurationPath() {
        String osName = System.getProperty("os.name");
        
        // For windows
        String systemDirectory = System.getenv("SystemDirectory");
        if (osName.toLowerCase().contains("windows") 
                && systemDirectory != null && !systemDirectory.isEmpty()) {
            
            return systemDirectory;
        }
        
        String systemRoot = System.getenv("SystemRoot");
        if (osName.toLowerCase().contains("windows") 
                && systemRoot != null && !systemRoot.isEmpty()) {
            
            return systemRoot + "\\System32";
        }
        
        // By default
        if (osName.toLowerCase().contains("windows")) {
            return "C:\\Windows\\System32";
            
        } else { // For unix
            return "/etc";
        }
    }
    
    /**
     * @return the user configuration path for os
     */
    public static String getUserConfigurationPath() {
        String osName = System.getProperty("os.name");
        String userHome = System.getProperty("user.home");
        
        // For windows
        String appData = System.getenv("APPDATA");
        if (osName.toLowerCase().contains("windows") 
                && appData != null && !appData.isEmpty()) {
            
            return appData;
        }
        
        if (osName.toLowerCase().contains("mac os x")) {
            return userHome + "/Library/Application Support";
        }
                
        // By default
        return userHome + File.separator + ".config";
    }

    /**
     * @return all ressource with pattern
     */
    public static Collection<String> getResources(String regex) {
        Reflections reflections = new Reflections("", new ResourcesScanner(), new WrapClassLoader());
        
        Store store = reflections.getStore();
        Multimap<String, String> mmap = store.get(ResourcesScanner.class.getSimpleName());
        
        final Pattern pattern = Pattern.compile(regex);
        Predicate predicate = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return pattern.matcher(input).matches();
            }
        };
        Collection<String> resources = Collections2.filter(mmap.values(), predicate);
        return resources;
    }
    
    
    /**
     * Scanner use to get classes as ressources
     */
    public static class ClassesScanner extends ResourcesScanner {
        @Override
        public boolean acceptsInput(String file) {
            return file.endsWith(".class"); //is a class file
        }
    }
    
    /**
     * @return all classes with pattern
     */
    public static Collection<String> getClasses(String regex) {
        Reflections reflections = new Reflections("", new ClassesScanner(), new WrapClassLoader());
        
        Store store = reflections.getStore();
        Multimap<String, String> mmap = store.get(ClassesScanner.class.getSimpleName());
        
        final Pattern pattern = Pattern.compile(".*" + regex + "\\.class$");
        Predicate predicate = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return pattern.matcher(input).matches();
            }
        };
        Collection<String> resources = Collections2.filter(mmap.values(), predicate);
        return resources;
    }
    
    /**
     * Wrap the current class loader to avoid null in resources for Tomcat8.
     */
    public static class WrapClassLoader extends ClassLoader {
        
        public ClassLoader classLoader;
        
        public WrapClassLoader() {
            this.classLoader = WrapClassLoader.class.getClassLoader();
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            Enumeration<URL> resources = this.classLoader.getResources(name);
            List list = EnumerationUtils.toList(resources);
            CollectionUtils.filter(list, NotNullPredicate.getInstance());
            
            return Collections.enumeration(list);
        }
        
    }

}

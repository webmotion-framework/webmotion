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
package org.debux.webmotion.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Call.ParameterTree;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

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
    
    /**
     * Simple singleton factory, maybe that two threads creates the instance, but 
     * it is not a problem.
     */
    public static class SingletonFactory<T> {
        protected Map<Class<? extends T>, T> singletons;

        public SingletonFactory() {
            singletons = new HashMap<Class<? extends T>, T>();
        }
        
        public T get(Class<? extends T> clazz) {
            T instance = singletons.get(clazz);
            return instance;
        }
        
        public T get(String clazzName) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(clazzName);
                return get(clazz);
                
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
            }
        }
                
        public T remove(Class<? extends T> clazz) {
            return singletons.remove(clazz);
        }
        
        public T remove(String clazzName) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(clazzName);
                return singletons.remove(clazz);
                
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
            }
        }
        
        public T createInstance(String clazzName) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(clazzName);
                return createInstance(clazz);
                
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
            }
        }
        
        public T createInstance(Class<? extends T> clazz) {
            try {
                T instance = clazz.newInstance();
                singletons.put(clazz, instance);
                return instance;
                
            } catch (IllegalAccessException iae) {
                throw new WebMotionException("Error during create handler factory " + clazz, iae);
                
            } catch (InstantiationException ie) {
                throw new WebMotionException("Error during create handler factory " + clazz, ie);
            }
        }

        public T getInstance(String clazzName) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(clazzName);
                return getInstance(clazz);
                
            } catch (ClassNotFoundException cnfe) {
                throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
            }
        }
                
        public T getInstance(Class<? extends T> clazz) {
            T instance = get(clazz);
            if (instance == null) {
                instance = createInstance(clazz);
            }
            return instance;
        }
    }

    /** Pattern use for @see replaceDynamicName */
    protected static Pattern dynamicNamePattern = Pattern.compile("\\{(\\p{Alnum}*)\\}");
    
    /**
     * Replace all parameters like {paramName} by real value in request parameters
     * @param name name contain a param
     * @param parameters request parameters
     * @return name with parameter values
     */
    public static String replaceDynamicName(String name, Call.ParameterTree parameterTree) {
        Matcher matcher = dynamicNamePattern.matcher(name);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            
            Map<String, ParameterTree> tree = parameterTree.getTree();
            ParameterTree treeValue = tree.get(paramName);
            Object values = treeValue.getValue();
            
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
     * Basic implementation LRU cache.
     * @param <K> key type
     * @param <V> value type
     */
    public static class LruCache<K, V> extends LinkedHashMap<K, V> {

        /** Max key in cache */
        protected int max;

        public LruCache(int maxEntries) {
            super(maxEntries + 1, 1.0f, true);
            this.max = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return super.size() > max;
        }
    }
    
    /**
     * Generate a new secret key.
     * 
     * @return secret key;
     */
    public static String generateSecret() {
        return RandomStringUtils.random(31, true, true);
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
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(""))
                .setScanners(new ResourcesScanner()));
        
        Store store = reflections.getStore();
        Multimap<String, String> mmap = store.get(ResourcesScanner.class);
        
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

}

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
package org.debux.webmotion.server.convention;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.FragmentUrl;
import org.debux.webmotion.server.mapping.Mapping;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.LoggerFactory;

/**
 * Create a mapping by convention.
 * 
 * @author julien
 */
public class ConventionMappingScanner {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConventionMappingScanner.class);

    public List<Mapping> scan() {
        // Search class extends WebMotionConventionController
        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(""))
            .setScanners(new SuperClassScanner()));
        
        Store store = reflections.getStore();
        Multimap<String, String> mmap = store.get(SuperClassScanner.class);
        
        final String classConvention = WebMotionConventionController.class.getName();
        Multimap<String, String> mmapFilter = Multimaps.filterValues(mmap, Predicates.equalTo(classConvention));
        
        Set<String> classes = mmapFilter.keySet();
        Map<String, Mapping> mappings = new HashMap<String, Mapping>();
        
        for (String className : classes) {
            Class klass = null;
            try {
                klass = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                throw new WebMotionException("Class name not found " + className, ex);
            }
            
            // Create mapping
            String packageName = klass.getPackage().getName();
            Mapping mapping = mappings.get(packageName);
            if (mapping == null) {
                mapping = new Mapping();
                mapping.setExtensionPath("/");
                
                Config config = mapping.getConfig();
                config.setPackageBase(packageName);
                
                mappings.put(packageName, mapping);
            }
            
            // Base url based on class name
            String simpleName = klass.getSimpleName();
            List<FragmentUrl>  fragmentUrlsByClassName = createFragmentUrlList(simpleName);
            
            // Look the methods
            Method[] methods = klass.getDeclaredMethods();
            for (Method method : methods) {
                
                ActionRule actionRule = new ActionRule();
                String methodName = method.getName();
                List<FragmentUrl>  fragmentUrlsByMethodName = createFragmentUrlList(methodName);
                
                // Link method
                Action action = new Action();
                action.setFullName(className + "." + methodName);
                action.setType(Action.Type.ACTION);
                actionRule.setAction(action);
                
                // Search HTTP method
                boolean removedFirst = true;
                String first = fragmentUrlsByMethodName.get(0).getValue();
                if ("create".equals(first)) {
                    actionRule.setMethods(Arrays.asList(HttpContext.METHOD_PUT));
                } else if ("get".equals(first)) {
                    actionRule.setMethods(Arrays.asList(HttpContext.METHOD_GET));
                } else if ("delete".equals(first)) {
                    actionRule.setMethods(Arrays.asList(HttpContext.METHOD_DELETE));
                } else if ("update".equals(first)) {
                    actionRule.setMethods(Arrays.asList(HttpContext.METHOD_POST));
                } else {
                    removedFirst = false;
                    actionRule.setMethods(Arrays.asList(HttpContext.METHOD_POST));
                }
                
                // Create URL
                if (removedFirst) {
                    fragmentUrlsByMethodName.remove(0);
                }
                
                List<FragmentUrl>  fragmentUrls = new ArrayList<FragmentUrl>();
                fragmentUrls.addAll(fragmentUrlsByClassName);
                fragmentUrls.addAll(fragmentUrlsByMethodName);
                actionRule.setRuleUrl(fragmentUrls);
                
                List<ActionRule> actionRules = mapping.getActionRules();
                actionRules.add(actionRule);
            }
        }
        
        return new ArrayList<Mapping>(mappings.values());
    }
    
    protected List<FragmentUrl> createFragmentUrlList(String name) {
        String[] values = StringUtils.splitByCharacterTypeCamelCase(name);
        List<FragmentUrl>  fragmentUrls = new ArrayList<FragmentUrl>(values.length);
        
        for (String value : values) {
            FragmentUrl fragmentUrl = new FragmentUrl();
            fragmentUrl.setValue(value.toLowerCase());
            fragmentUrls.add(fragmentUrl);
        }
                    
        return fragmentUrls;
    }
    
}

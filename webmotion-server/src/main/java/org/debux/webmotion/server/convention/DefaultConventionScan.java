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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.FragmentUrl;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.tools.ReflectionUtils;
import org.slf4j.LoggerFactory;

/**
 * Create a mapping by convention.
 * 
 * @author julien
 */
public class DefaultConventionScan extends ConventionScan {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DefaultConventionScan.class);

    @Override
    public Mapping scan() {
        Mapping mapping = new Mapping();
        
        mapping.setName("default-convention");
        mapping.setExtensionPath("/");
        
        mapping.setActionRules(scanControllers(mapping));
        mapping.setFilterRules(scanFilters(mapping));
        
        return mapping;
    }
    
    public List<ActionRule> scanControllers(Mapping mapping) {
        Collection<Class<?>> controllers = ReflectionUtils.getClassesBySuperClass(WebMotionConventionController.class);
        List<ActionRule> rules = new ArrayList<ActionRule>(controllers.size());
        
        for (Class<?> controller : controllers) {
            Method[] methods = controller.getMethods();
            for (Method method : methods) {
                Class<?> declaringClass = method.getDeclaringClass();
                if (!declaringClass.equals(Object.class) && !declaringClass.equals(WebMotionController.class)) {
                    
                    ActionRule rule = new ActionRule();
                    rule.setMapping(mapping);
                    rule.setLine(-1);
                    rules.add(rule);
                    
                    String className = controller.getName();
                    String methodName = method.getName();
                    
                    // Create action
                    Action action = new Action();
                    action.setFullName(className + "." + methodName);
                    action.setType(Action.Type.ACTION);
                    rule.setAction(action);
                    
                    // Search http method
                    String httpMethod = HttpContext.METHOD_GET;
                    if (methodName.startsWith("create")) {
                        httpMethod = HttpContext.METHOD_PUT;
                        methodName = methodName.replaceFirst("create", "");
                        
                    } else if (methodName.startsWith("get")) {
                        httpMethod = HttpContext.METHOD_GET;
                        methodName = methodName.replaceFirst("get", "");
                        
                    } else if (methodName.startsWith("delete")) {
                        httpMethod = HttpContext.METHOD_DELETE;
                        methodName = methodName.replaceFirst("delete", "");
                        
                    } else if (methodName.startsWith("update")) {
                        httpMethod = HttpContext.METHOD_POST;
                        methodName = methodName.replaceFirst("update", "");
                    }
                    rule.setMethods(Arrays.asList(httpMethod));
                    
                    // Create path
                    List<FragmentUrl> url = new ArrayList<FragmentUrl>();
                    
                    String simpleClassName = controller.getSimpleName();
                    Package controllerPackage = controller.getPackage();
                    
                    if (controllerPackage != null) {
                        String packageName = controllerPackage.getName();
                        String subPackageName = StringUtils.substringAfterLast(packageName, ".");
                        
                        if (!StringUtils.startsWithIgnoreCase(simpleClassName, subPackageName)) {
                            url.addAll(createFragmentUrlList(subPackageName));
                        }
                    }
                    url.addAll(createFragmentUrlList(simpleClassName));
                    url.addAll(createFragmentUrlList(methodName));
                    rule.setRuleUrl(url);
                }
            }
        }
        
        return rules;
    }
    
    public List<FilterRule> scanFilters(Mapping mapping) {
        Pattern allPattern = Pattern.compile("/*");
        
        Collection<Class<?>> filters = ReflectionUtils.getClassesBySuperClass(WebMotionConventionAllFilter.class);
        List<FilterRule> rules = new ArrayList<FilterRule>(filters.size());
        
        for (Class<?> filter : filters) {
            FilterRule rule = new FilterRule();
            rule.setMapping(mapping);
            rule.setLine(-1);
            rules.add(rule);
            
            rule.setMethods(Arrays.asList("*"));
            rule.setPattern(allPattern);
            
            Action action = new Action();
            action.setType(Action.Type.ACTION);
            action.setFullName(filter.getName() + ".filter");
            rule.setAction(action);
        }
        
        filters = ReflectionUtils.getClassesBySuperClass(WebMotionConventionPackageFilter.class);
        for (Class<?> filter : filters) {
            FilterRule rule = new FilterRule();
            rule.setMapping(mapping);
            rule.setLine(-1);
            rules.add(rule);
            
            Package filterPackage = filter.getPackage();
            if (filterPackage != null) {
                String packageName = filterPackage.getName();
                String subPackageName = StringUtils.substringAfterLast(packageName, ".");
                
                String regexp = "/" + subPackageName.toLowerCase() + "/*";
                rule.setPattern(Pattern.compile(regexp));
                
            } else {
                rule.setPattern(allPattern);
            }
            rule.setMethods(Arrays.asList("*"));
            
            
            Action action = new Action();
            action.setType(Action.Type.ACTION);
            action.setFullName(filter.getName() + ".filter");
            rule.setAction(action);
        }
        
        return rules;
    }
    
    protected List<FragmentUrl> createFragmentUrlList(String name) {
        String[] values = StringUtils.splitByCharacterTypeCamelCase(name);
        List<FragmentUrl>  fragmentUrls = new ArrayList<FragmentUrl>(values.length);
        
        for (String value : values) {
            // Add slash before each value
            FragmentUrl fragmentSlash = new FragmentUrl();
            fragmentSlash.setValue("/");
                    
            Pattern patternSlash = Pattern.compile("^/$");
            fragmentSlash.setPattern(patternSlash);
                    
            fragmentUrls.add(fragmentSlash);
            
            // Add value
            String valueLowerCase = value.toLowerCase();
            FragmentUrl fragmentUrl = new FragmentUrl();
            fragmentUrl.setValue(valueLowerCase);
            
            Pattern pattern = Pattern.compile("^" + valueLowerCase + "$");
            fragmentUrl.setPattern(pattern);
                    
            fragmentUrls.add(fragmentUrl);
        }
                    
        return fragmentUrls;
    }
    
}

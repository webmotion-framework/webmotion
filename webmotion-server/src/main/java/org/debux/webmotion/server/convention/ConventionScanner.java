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
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.FragmentUrl;
import org.debux.webmotion.server.tools.HttpUtils;
import org.debux.webmotion.server.tools.ReflectionUtils;
import org.slf4j.LoggerFactory;

/**
 * Create a mapping by convention.
 * 
 * @author julien
 */
public class ConventionScanner {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConventionScanner.class);

    public ActionRule scan(Call call) {
        try {
            HttpContext context = call.getContext();
            String url = context.getUrl();
            String httpMethod = context.getMethod();
            
            List<String> path = HttpUtils.splitPath(url);
            String methodName = path.remove(path.size() - 1);
            
            String className = "";
            for (String fragment : path) {
                if (!"/".equals(fragment)) {
                    className += StringUtils.capitalize(fragment);
                }
            }
            
            Collection<String> classes = ReflectionUtils.getClasses(className);
            if (classes.isEmpty()) {
                return null;
            }
            String classPath = classes.iterator().next();
            Class<?> klass = getClass().getClassLoader().loadClass(classPath.replaceAll("/", ".").replaceAll("\\.class$", ""));

            String methodNameWithMethodHttp = StringUtils.capitalize(methodName);
            if (HttpContext.METHOD_PUT.equals(httpMethod)) {
                methodNameWithMethodHttp = "create" + methodNameWithMethodHttp;
            } else if (HttpContext.METHOD_GET.equals(httpMethod)) {
                methodNameWithMethodHttp = "get" + methodNameWithMethodHttp;
            } else if (HttpContext.METHOD_DELETE.equals(httpMethod)) {
                methodNameWithMethodHttp = "delete" + methodNameWithMethodHttp;
            } else if (HttpContext.METHOD_POST.equals(httpMethod)) {
                methodNameWithMethodHttp = "update" + methodNameWithMethodHttp;
            }

            Method method = ReflectionUtils.getMethod(klass, methodNameWithMethodHttp);
            if (method == null) {
                method = ReflectionUtils.getMethod(klass, methodName);
                if (method == null) {
                    return null;
                }
                methodNameWithMethodHttp = methodName;
            }
            
            // Base url based on class name
            List<FragmentUrl> fragmentUrlsByClassName = createFragmentUrlList(className);
            
            // Look the methods
            ActionRule actionRule = new ActionRule();
            List<FragmentUrl> fragmentUrlsByMethodName = createFragmentUrlList(methodName);
            
            // Link method
            Action action = new Action();
            action.setFullName(klass.getName() + "." + methodNameWithMethodHttp);
            action.setType(Action.Type.ACTION);
            actionRule.setAction(action);
            
            // Search HTTP method
            actionRule.setMethods(Arrays.asList(httpMethod));
            
            List<FragmentUrl>  fragmentUrls = new ArrayList<FragmentUrl>();
            fragmentUrls.addAll(fragmentUrlsByClassName);
            fragmentUrls.addAll(fragmentUrlsByMethodName);
            actionRule.setRuleUrl(fragmentUrls);
            actionRule.setMapping(null);
            actionRule.setLine(-1);
            
            return actionRule;
            
        } catch (ClassNotFoundException ex) {
            log.warn("Invalid class", ex);
            return null;
        }
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

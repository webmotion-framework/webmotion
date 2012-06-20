/*
 * #%L
 * WebMotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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
package org.debux.webmotion.server.parser;

import java.lang.reflect.Method;
import java.net.URL;
import org.debux.webmotion.server.WebMotionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.Rule;

/**
 * check :
 * <ul>
 * <li>class name</li>
 * <li>method name</li>
 * </ul>
 * 
 * @author julien
 */
public class MappingChecker {

    private static final Logger log = LoggerFactory.getLogger(MappingChecker.class);
    
    public static boolean isNotVariable(String value) {
        return !isVariable(value);
    }
    
    public static boolean isVariable(String value) {
        return value.contains("{") && value.contains("}");
    }
    
    public static boolean checkClassName(String className) {
        try {
            Class.forName(className);
            return true;
            
        } catch (ClassNotFoundException ex) {
            log.warn("Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
            return false;
        }
    }
    
    public static boolean checkMethodName(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);

            Method method = WebMotionUtils.getMethod(clazz, methodName);
            if (method == null) {
                log.warn("Invalid method name " + methodName + "for class name " + className);
                log.debug("Invalid method name " + methodName + "for class name " + className);
                return false;
            } else {
                return true;
            }
        } catch (ClassNotFoundException ex) {
            log.warn("Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
            return false;
        }
    }
    
    public static boolean checkFile(String fileName) {
        ClassLoader classLoader = MappingChecker.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            log.warn("Invalid file " + fileName);
            return false;
        }
        return true;
    }
    
    public static boolean checkPattern(String regex) {
        try {
            Pattern.compile(regex);
            return true;
            
        } catch (PatternSyntaxException ex) {
            log.warn("Invalid pattern " + regex);
            log.debug("Invalid pattern " + regex, ex);
            return false;
        }
    }
    
    public static boolean checkActionRule(Rule rule, String packageTarget) {
        Action action = rule.getAction();
        if (action != null && action.isAction()) {
            String className = action.getClassName();
            if (packageTarget != null && !packageTarget.isEmpty()) {
                className = packageTarget + "." + className;
            }
            String methodName = action.getMethodName();
            
            if (MappingChecker.isNotVariable(className)) {
                if (MappingChecker.isNotVariable(methodName)) {
                    return MappingChecker.checkMethodName(className, methodName);

                } else {
                    return MappingChecker.checkClassName(className);
                }
            }
        }
        return true;
    }
    
    public static boolean checkViewRule(Rule rule, String packageTarget) {
        Action action = rule.getAction();
        if (action != null && action.isView()) {
            
            String fullName = action.getFullName();
            if (packageTarget != null && !packageTarget.isEmpty()) {
                fullName = packageTarget.replaceAll("\\.", "/") + "/" + fullName;
            }
            
            if (MappingChecker.isNotVariable(fullName)) {
                return MappingChecker.checkFile(fullName);
            }
        }
        return true;
    }
    
}

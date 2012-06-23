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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.debux.webmotion.server.WebMotionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Mapping;
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
    
    protected List<Warning> warnings;

    public static class Warning {
        protected Mapping mapping;
        protected int line;
        protected String message;

        public Warning(Mapping mapping, int line, String message) {
            this.mapping = mapping;
            this.line = line;
            this.message = message;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public Mapping getMapping() {
            return mapping;
        }

        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public MappingChecker() {
        this.warnings = new ArrayList<Warning>();
    }

    public List<Warning> getWarnings() {
        return warnings;
    }
    
    public void addWarning(Mapping mapping, int line, String message) {
        Warning warning = new Warning(mapping, line, message);
        warnings.add(warning);
    }
    
    public void addWarning(Rule rule, String message) {
        Mapping mapping = rule.getMapping();
        int line = rule.getLine();
        Warning warning = new Warning(mapping, line, message);
        warnings.add(warning);
    }
    
    public void print() {
        for (Warning warning : warnings) {
            log.warn(warning.getMessage() + " " + warning.getMapping().getName() + ":" + warning.getLine());
        }
    }
    
    public boolean isNotVariable(String value) {
        return !isVariable(value);
    }
    
    public boolean isVariable(String value) {
        return value.contains("{") && value.contains("}");
    }
    
    protected boolean checkClassName(Rule rule, String className) {
        try {
            Class.forName(className);
            return true;
            
        } catch (ClassNotFoundException ex) {
            addWarning(rule, "Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
            return false;
        }
    }
    
    protected boolean checkMethodName(Rule rule, String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            return checkMethodName(rule, clazz, methodName);

        } catch (ClassNotFoundException ex) {
            addWarning(rule, "Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
            return false;
        }
    }
    
    protected boolean checkMethodName(Rule rule, Class<?> clazz, String methodName) {
        Method method = WebMotionUtils.getMethod(clazz, methodName);
        if (method == null) {
            addWarning(rule, "Invalid method name " + methodName + "for class name " + clazz.getSimpleName());
            log.debug("Invalid method name " + methodName + "for class name " + clazz.getSimpleName());
            return false;
        } else {
            return true;
        }
    }
    
    protected boolean checkFile(Rule rule, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            addWarning(rule, "Invalid file " + fileName);
            return false;
        }
        return true;
    }
    
    protected boolean checkPattern(Rule rule, String regex) {
        try {
            Pattern.compile(regex);
            return true;
            
        } catch (PatternSyntaxException ex) {
            addWarning(rule, "Invalid pattern " + regex);
            log.debug("Invalid pattern " + regex, ex);
            return false;
        }
    }
    
    public boolean checkAction(Rule rule, Class<? extends WebMotionController> clazz) {
        Action action = rule.getAction();
        if (action != null && action.isAction()) {
            String methodName = action.getMethodName();
            if (isNotVariable(methodName)) {
                return checkMethodName(rule, clazz, methodName);
            }
        }
        return true;
    }
    
    public boolean checkAction(Rule rule, String packageTarget) {
        Action action = rule.getAction();
        if (action != null && action.isAction()) {
            String className = action.getClassName();
            if (packageTarget != null && !packageTarget.isEmpty()) {
                className = packageTarget + "." + className;
            }
            String methodName = action.getMethodName();
            
            if (isNotVariable(className)) {
                if (isNotVariable(methodName)) {
                    return checkMethodName(rule, className, methodName);

                } else {
                    return checkClassName(rule, className);
                }
            }
        }
        return true;
    }
    
    public boolean checkView(Rule rule, String packageTarget) {
        Action action = rule.getAction();
        if (action != null && action.isView()) {
            
            String fullName = action.getFullName();
            if (packageTarget != null && !packageTarget.isEmpty()) {
                fullName = packageTarget.replaceAll("\\.", "/") + "/" + fullName;
            }
            
            if (isNotVariable(fullName)) {
                return checkFile(rule, fullName);
            }
        }
        return true;
    }
        
    public void checkError(ErrorRule rule) {
        String error = rule.getError();
        if (error != null && !error.startsWith(ErrorRule.PREFIX_CODE)) {
            checkClassName(rule, error);
        }
    }
    
}

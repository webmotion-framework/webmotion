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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.debux.webmotion.server.WebMotionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.*;
import org.debux.webmotion.server.parser.MappingVisit.Visitor;
import org.debux.webmotion.server.websocket.WebMotionWebSocket;

/**
 * Uses to check the mapping file after the initialization of the server. 
 * This warnings are displayed at the startup, as warning log with the indication 
 * on the mapping path, the line number and the message.
 * This class checks :
 * <ul>
 * <li>class name</li>
 * <li>method name</li>
 * <li>variable</li>
 * <li>empty mapping</li>
 * <li>view file</li>
 * <li>pattern</li>
 * <li>super class</li>
 * <li>class modifer</li>
 * <li>mathod modifer</li>
 * </ul>
 * 
 * @author julien
 */
public class MappingChecker {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(MappingChecker.class);
    
    /** Pattern to get all variable like {var} in String */
    public static Pattern VARIABLE_PATTERN = Pattern.compile("(^|[^\\\\])\\{((\\p{Alnum}|\\.)+)\\}");
    
    /** Visitor uses to look the mapping */
    protected MappingVisit visitor;
    
    /** All alerts found in the mapping */
    protected List<Warning> warnings;

    /**
     * Saves warning information.
     */
    public static class Warning {
        /** Current mapping */
        protected Mapping mapping;
        
        /** Current line number in the mapping */
        protected int line;
        
        /** Alert message on the line of the mapping */
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

        @Override
        public String toString() {
            return message + " " + mapping.getName() + ":" + line;
        }
    }

    /** Default constructor */
    public MappingChecker() {
        this.warnings = new ArrayList<Warning>();
        this.visitor = new MappingVisit();
    }

    /**
     * @return all warnings.
     */
    public List<Warning> getWarnings() {
        return warnings;
    }
    
    /**
     * Add a warning
     * 
     * @param mapping current mapping
     * @param line current line number
     * @param message warning message
     */
    protected void addWarning(Mapping mapping, int line, String message) {
        Warning warning = new Warning(mapping, line, message);
        warnings.add(warning);
    }
    
    /**
     * Add a warning on rule.
     * 
     * @param rule current rule
     * @param message warning message
     */
    protected void addWarning(Rule rule, String message) {
        Mapping mapping = rule.getMapping();
        int line = rule.getLine();
        addWarning(mapping, line, message);
    }
    
    /**
     * Log the warning
     */
    public void print() {
        for (Warning warning : warnings) {
            log.warn(warning.toString());
        }
    }
    
    /**
     * Verify the mapping and the extensions.
     * 
     * @param context server context
     * @param mapping mapping to verify
     */
    public void checkMapping(ServerContext context, Mapping mapping) {
        Visitor mappingVisitor = getMappingVisitor(context, mapping);
        visitor.visit(mapping, mappingVisitor);
    }

    /**
     * Return the visitor use to check the mapping.
     * 
     * @param context server context
     * @param mapping mapping to verify
     * @return a visitor use tto ckeck the mapping
     */
    protected MappingVisit.Visitor getMappingVisitor(final ServerContext context, Mapping mapping) {
        return new MappingVisit.Visitor() {
            protected String packageViews;
            protected String packageFilters;
            protected String packageActions;
            protected String packageErrors;
            
            protected Map<String, Class<? extends WebMotionController>> globalControllers;

            @Override
            public void accept(Mapping mapping) {
                Config config = mapping.getConfig();
                packageViews = context.getWebappPath() + File.separatorChar + config.getPackageViews();
                packageFilters = config.getPackageFilters();
                packageActions = config.getPackageActions();
                packageErrors = config.getPackageErrors();
                
                globalControllers = context.getGlobalControllers();
                
                List<ActionRule> actionRules = mapping.getActionRules();
                List<ErrorRule> errorRules = mapping.getErrorRules();
                List<FilterRule> filterRules = mapping.getFilterRules();
                List<Mapping> extensionsRules = mapping.getExtensionsRules();
                
                if (actionRules.isEmpty() && errorRules.isEmpty() &&
                        filterRules.isEmpty() && extensionsRules.isEmpty()) {
                    addWarning(mapping, 0, "Mapping empty");
                }
            }

            @Override
            public void accept(Mapping mapping, FilterRule filterRule) {
                checkAction(filterRule, globalControllers, packageFilters, WebMotionFilter.class);
            }

            @Override
            public void accept(Mapping mapping, ActionRule actionRule) {
                List<FragmentUrl> fragments = new ArrayList<FragmentUrl>();
                fragments.addAll(actionRule.getRuleUrl());
                fragments.addAll(actionRule.getRuleParameters());
                
                checkFragments(actionRule, fragments);
                checkVariables(actionRule, fragments);
                
                checkAction(actionRule, globalControllers, packageActions, WebMotionController.class);
                checkView(actionRule, packageViews);
            }

            @Override
            public void accept(Mapping mapping, ErrorRule errorRule) {
                checkError(errorRule);
                checkAction(errorRule, globalControllers, packageErrors, WebMotionController.class);
                checkView(errorRule, packageViews);
            }
        };
    }
    
    /**
     * Test if the value not contains a variable like "{var"}.
     * @param value value to test
     * @return true if the string not contains a variable otherwise false
     */
    protected boolean isNotVariable(String value) {
        return !isVariable(value);
    }
    
    /**
     * Test if the value contains a variable like "{var"}.
     * @param value value to test
     * @return true if the string contains a variable otherwise false
     */
    protected boolean isVariable(String value) {
        Matcher matcher = VARIABLE_PATTERN.matcher(value);
        return matcher.find();
    }

    /**
     * Check class name
     * @param rule current rule tested
     * @param superClass super class to check
     * @param packageTarget package contains the class
     * @param className class name to check
     */
    protected void checkClassName(Rule rule, Class superClass, String packageTarget, String className) {
        if (packageTarget != null && !packageTarget.isEmpty()) {
            className = packageTarget + "." + className;
        }

        checkClassName(rule, superClass, className);
    }

    /**
     * Check class name
     * @param rule current rule tested
     * @param superClass super class to check
     * @param className class name to check
     */
    protected void checkClassName(Rule rule, Class superClass, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            checkModfiers(rule, clazz);
            checkSuperClass(rule, superClass, clazz);
            
        } catch (ClassNotFoundException ex) {
            addWarning(rule, "Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
        }
    }

    /**
     * Check super class for a class
     * @param rule current rule tested
     * @param superClass super class to check
     * @param clazz  class to ckeck
     */
    protected void checkSuperClass(Rule rule, Class superClass, Class clazz) {
        if (!superClass.isAssignableFrom(clazz)) {
            addWarning(rule, "Requires super class " + superClass.getSimpleName() + " for " + clazz.getSimpleName());
        }
    }
    
    /**
     * Check modifiers for the class
     * @param rule current rule tested
     * @param clazz class to check
     */
    protected void checkModfiers(Rule rule, Class clazz) {
        String className = clazz.getSimpleName();
        
        int modifiers = clazz.getModifiers();
        
        if (Modifier.isAbstract(modifiers)) {
            addWarning(rule, "The class is abstract " + className);
        }
        if (!Modifier.isPublic(modifiers)) {
            addWarning(rule, "The class is not public " + className);
        }
    }
    
    /**
     * Check modifiers for the class
     * @param rule current rule tested
     * @param method method to check
     */
    protected void checkModfiers(Rule rule, Method method) {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        
        int modifiers = method.getModifiers();

        if (Modifier.isAbstract(modifiers)) {
            addWarning(rule, "The method is abstract " + methodName + " for class name " + className);
        }
        if (Modifier.isStatic(modifiers)) {
            addWarning(rule, "The method is static " + methodName + " for class name " + className);
        }
        if (!Modifier.isPublic(modifiers)) {
            addWarning(rule, "The method is not public " + methodName + " for class name " + className);
        }
    }
    
    /**
     * Check class name and method name.
     * @param rule current rule tested
     * @param superClass super class to check
     * @param className class name to check
     * @param methodName method name to check
     */
    protected void checkMethodName(Rule rule, Class superClass, String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            checkModfiers(rule, clazz);
            checkSuperClass(rule, superClass, clazz);
            checkMethodName(rule, clazz, methodName);

        } catch (ClassNotFoundException ex) {
            addWarning(rule, "Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
        }
    }
    
    /**
     * Check method name in class
     * @param rule current rule tested
     * @param clazz class to search method
     * @param methodName method name to check
     */
    protected void checkMethodName(Rule rule, Class<?> clazz, String methodName) {
        Method method = WebMotionUtils.getMethod(clazz, methodName);
        if (method == null) {
            addWarning(rule, "Invalid method name " + methodName + " for class name " + clazz.getSimpleName());
        } else {
            checkModfiers(rule, method);
        }
    }
    
    /**
     * Check file name on the file system.
     * @param rule current rule tested
     * @param fileName file name to check
     */
    protected void checkFile(Rule rule, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            addWarning(rule, "Invalid file " + fileName);
        }
    }
    
    /**
     * Check action is valid in rule.
     * @param rule current rule tested
     * @param controllers controllers outside the package
     * @param packageTarget package name found action
     * @param superClass super class for action
     */
    protected void checkAction(Rule rule, Map<String, Class<? extends WebMotionController>> controllers, 
            String packageTarget, Class superClass) {
        
        Action action = rule.getAction();
        if (action != null && action.isAction()) {
            String className = action.getClassName();
            String methodName = action.getMethodName();
            
            Class<? extends WebMotionController> clazz = controllers.get(className);
            if (clazz != null) {
                if (isNotVariable(methodName)) {
                    checkMethodName(rule, clazz, methodName);
                }
                
            } else {
                if (packageTarget != null && !packageTarget.isEmpty()) {
                    className = packageTarget + "." + className;
                }
            
                if (isNotVariable(className)) {
                    if (isNotVariable(methodName)) {
                        checkMethodName(rule, superClass, className, methodName);
                    } else {
                        checkClassName(rule, superClass, className);
                    }
                }
            }
        }
    }
    
    /**
     * Check view is valid in rule.
     * @param rule current rule tested
     * @param packageTarget package name found view
     */
    protected void checkView(Rule rule, String packageTarget) {
        Action action = rule.getAction();
        if (action != null && action.isView()) {
            
            String fullName = action.getFullName();
            if (packageTarget != null && !packageTarget.isEmpty()) {
                fullName = packageTarget.replaceAll("\\.", "/") + "/" + fullName;
            }
            
            if (isNotVariable(fullName)) {
                checkFile(rule, fullName);
            }
        }
    }
        
    /**
     * Ckeck exception is valid in rule.
     * @param rule current rule tested
     */
    protected void checkError(ErrorRule rule) {
        String error = rule.getError();
        if (error != null && !error.startsWith(ErrorRule.PREFIX_CODE)) {
            checkClassName(rule, Exception.class, error);
        }
    }
    
    /**
     * Check fragments contains valid patterns in rule.
     * @param rule current rule tested
     * @param fragments fragments to check
     */
    protected void checkFragments(Rule rule, List<FragmentUrl> fragments) {
        for (FragmentUrl fragment : fragments) {
            String value = fragment.getValue();
            Pattern pattern = fragment.getPattern();
            if (value != null && pattern == null) {
                addWarning(rule, "Invalid pattern " + value);
            }
        }
    }
    
    /**
     * Check action contains valid variables in rule.
     * @param rule current rule tested
     * @param fragments fragments contains variables
     */
    protected void checkVariables(Rule rule, List<FragmentUrl> fragments) {
        List<String> availableVariables = new ArrayList<String>();
        for (FragmentUrl fragment : fragments) {
            String name = fragment.getName();
            String param = fragment.getParam();
            if (name != null) {
                availableVariables.add(name);
            } else if (param != null) {
                availableVariables.add(param);
            }
        }
        
        Action action = rule.getAction();
        if (action != null) {
            String fullName = action.getFullName();
            Matcher matcher = VARIABLE_PATTERN.matcher(fullName);
            while (matcher.find()) {
                String variable = matcher.group(2);
                if (!availableVariables.contains(variable)) {
                    addWarning(rule, "Invalid variable " + variable);
                }
            }
        }
    }
}

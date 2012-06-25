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
import java.util.Map;
import java.util.regex.Matcher;
import org.debux.webmotion.server.WebMotionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.*;
import org.debux.webmotion.server.parser.MappingVisit.Visitor;

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
    
    public static Pattern VARIABLE_PATTERN = Pattern.compile("(^|[^\\\\])\\{((\\p{Alnum}|\\.)+)\\}");
    
    protected MappingVisit visitor;
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
        this.visitor = new MappingVisit();
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
        addWarning(mapping, line, message);
    }
    
    public void print() {
        for (Warning warning : warnings) {
            log.warn(warning.getMessage() + " " + warning.getMapping().getName() + ":" + warning.getLine());
        }
    }
    
    public void checkMapping(ServerContext context, Mapping mapping) {
        Visitor mappingVisitor = getMappingVisitor(context, mapping);
        visitor.visit(mapping, mappingVisitor);
    }
    
    /**
     * Check the mapping and extensions
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
                checkAction(filterRule, globalControllers, packageFilters);
            }

            @Override
            public void accept(Mapping mapping, ActionRule actionRule) {
                List<FragmentUrl> fragments = new ArrayList<FragmentUrl>();
                fragments.addAll(actionRule.getRuleUrl());
                fragments.addAll(actionRule.getRuleParameters());
                
                checkFragments(actionRule, fragments);
                checkVariables(actionRule, fragments);
                
                checkAction(actionRule, globalControllers, packageActions);
                checkView(actionRule, packageViews);
            }

            @Override
            public void accept(Mapping mapping, ErrorRule errorRule) {
                checkError(errorRule);
                checkAction(errorRule, globalControllers, packageErrors);
                checkView(errorRule, packageViews);
            }
        };
    }
    
    protected boolean isNotVariable(String value) {
        return !isVariable(value);
    }
    
    protected boolean isVariable(String value) {
        Matcher matcher = VARIABLE_PATTERN.matcher(value);
        return matcher.find();
    }

    protected void checkClassName(Rule rule, String className) {
        try {
            Class.forName(className);
            
        } catch (ClassNotFoundException ex) {
            addWarning(rule, "Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
        }
    }
    
    protected void checkMethodName(Rule rule, String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            checkMethodName(rule, clazz, methodName);

        } catch (ClassNotFoundException ex) {
            addWarning(rule, "Invalid class name " + className);
            log.debug("Invalid class name " + className, ex);
        }
    }
    
    protected void checkMethodName(Rule rule, Class<?> clazz, String methodName) {
        Method method = WebMotionUtils.getMethod(clazz, methodName);
        if (method == null) {
            addWarning(rule, "Invalid method name " + methodName + " for class name " + clazz.getSimpleName());
        }
    }
    
    protected void checkFile(Rule rule, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            addWarning(rule, "Invalid file " + fileName);
        }
    }
    
    protected void checkAction(Rule rule, Map<String, Class<? extends WebMotionController>> controllers, String packageTarget) {
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
                        checkMethodName(rule, className, methodName);
                    } else {
                        checkClassName(rule, className);
                    }
                }
            }
        }
    }
    
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
        
    protected void checkError(ErrorRule rule) {
        String error = rule.getError();
        if (error != null && !error.startsWith(ErrorRule.PREFIX_CODE)) {
            checkClassName(rule, error);
        }
    }
    
    protected void checkFragments(Rule rule, List<FragmentUrl> fragments) {
        for (FragmentUrl fragment : fragments) {
            String value = fragment.getValue();
            Pattern pattern = fragment.getPattern();
            if (value != null && pattern == null) {
                addWarning(rule, "Invalid pattern " + value);
            }
        }
    }
    
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

package org.debux.webmotion.server.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    public static void check(Action action) {
        if (action.isAction()) {
            checkAction(action);
        }
    }
    
    public static void checkAction(Action action) {
        String className = action.getClassName();
        if (isNotVariable(className)) {
            try {
                Class<?> clazz = Class.forName(className);

                String methodName = action.getMethodName();
                if (isNotVariable(methodName)) {
                    Method method = WebMotionUtils.getMethod(clazz, methodName);
                    if (method == null) {
                        log.warn("Invalid method name " + methodName + "for class name " + className);
                    }
                }
            } catch (ClassNotFoundException ex) {
                log.warn("Invalid class name " + className, ex);
            }
        }
    }
    
    public static void check(Action action, List<FragmentUrl> fragments) {
        List<String> variables = new ArrayList<String>();
        for (FragmentUrl fragment : fragments) {
            String param = fragment.getParam();
            String name = fragment.getName();
            
            if (name != null && !name.isEmpty()) {
                variables.add(name);
                
            } else if (param != null && !param.isEmpty()) {
                variables.add(param);
            }
        }
        
        String fullName = action.getFullName();
        Pattern pattern = Pattern.compile("\\{(.+):(.+)\\}");
        Matcher matcher = pattern.matcher(fullName);
        while (matcher.find()) {
            String variable = matcher.group(0);
            if (!variables.contains(variable)) {
                log.warn("Invalid variable " + variable);
            }
        }
    }
    
    public void check(Mapping mapping) {
        List<ActionRule> actionRules = mapping.getActionRules();
        for (ActionRule actionRule : actionRules) {
            Action action = actionRule.getAction();
            check(action);
            check(action, actionRule.getRuleUrl());
            check(action, actionRule.getRuleParameters());
        }
        
        List<ErrorRule> errorRules = mapping.getErrorRules();
        for (ErrorRule errorRule : errorRules) {
            Action action = errorRule.getAction();
            check(action);
        }
        
        List<FilterRule> filterRules = mapping.getFilterRules();
        for (FilterRule filterRule : filterRules) {
            Action action = filterRule.getAction();
            check(action);
        }
    }
}

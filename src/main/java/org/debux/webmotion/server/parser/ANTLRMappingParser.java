/*
 * #%L
 * Webmotion in action
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
package org.debux.webmotion.server.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.FragmentUrl;
import org.debux.webmotion.server.parser.MappingLanguageParser.mapping_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ANTLR implementation of parser the file mapping.
 * 
 * @author jruchaud
 */
public class ANTLRMappingParser implements MappingParser {

    private static final Logger log = LoggerFactory.getLogger(ANTLRMappingParser.class);
    
    /** Mapping generate */
    protected Mapping mapping;
    
    /** Context during visit tree */
    protected Deque<Object> stack;
    
    /** Rule to vist tree */
    protected Map<String, Visit> visitors;
    
    /**
     * Report error during the parsing.
     */
    public class MappingErrorReporter implements ErrorReporter {
        protected List<String> errors;

        public MappingErrorReporter() {
            errors = new ArrayList<String>();
        }
        
        @Override
        public void reportError(String error) {
            errors.add(error);
        }

        public List<String> getErrors() {
            return errors;
        }
    }
        
    /**
     * Visit on type of element.
     */
    public class Visit {
        /**
         * Accept visit before explore child, by default do nothing.
         * @param value value of element
         */
        public void acceptBefore(String value) {
        }
        
        /**
         * Accept visit after explore child, by default do nothing.
         * @param value value of element
         */
        public void acceptAfter(String value) {
        }
    }

    /** Implement tree visitor */
    protected TreeVisitorAction visitorAction = new TreeVisitorAction() {
        
        /** Store the current path */
        protected String path = "";

        @Override
        public Object pre(Object t) {
            CommonTree tree = (CommonTree) t;

            Token token = tree.getToken();
            int type = token.getType();
            String text = token.getText();

            if(type == MappingLanguageParser.DOLLAR && !"$".equals(text)) {
                path += "/" + text;
            } else {
                path += "/*";
            }
            
            log.info("Before " + path + " = " + text);
            Visit visit = visitors.get(path);
            if(visit != null) {
                visit.acceptBefore(text);
            }
                
            return t;
        }
        
        @Override
        public Object post(Object t) {
            CommonTree tree = (CommonTree) t;

            Token token = tree.getToken();
            String text = token.getText();
            
            log.info("After " + path + " = " + text);
            Visit visit = visitors.get(path);
            if(visit != null) {
                visit.acceptAfter(text);
            }
            
            path = StringUtils.substringBeforeLast(path, "/");
            
            return t;
        }
    };

    /**
     * Default contructor to initialize the attributes.
     */
    public ANTLRMappingParser() {
        mapping = new Mapping();
        stack = new LinkedList<Object>();
        visitors = new HashMap<String, Visit>();
        initVisit();
    }
    
    @Override
    public Mapping parse(InputStream stream) {
        try {
            String content = IOUtils.toString(stream);
            ANTLRStringStream input = new ANTLRStringStream(content);
            
            MappingLanguageLexer lexer = new MappingLanguageLexer(input);
            
            MappingErrorReporter reporter = new MappingErrorReporter();
            lexer.setErrorReporter(reporter);

            CommonTokenStream tokens = new CommonTokenStream();
            tokens.setTokenSource(lexer);
            
            // Parse
            MappingLanguageParser parser = new MappingLanguageParser(tokens);
            parser.setErrorReporter(reporter);
            mapping_return result = parser.mapping();
            
            // Verify parsing errors
            List<String> errors = reporter.getErrors();
            if(!errors.isEmpty()) {
                throw new WebMotionException(errors.toString());
            }
            
            // Visit tree
            CommonTree tree = result.tree;
            TreeVisitor treeVisitor = new TreeVisitor();
            treeVisitor.visit(tree, visitorAction);
            return mapping;

        } catch (RecognitionException re) {
            throw new WebMotionException("Error to parse the file mapping", re);
            
        } catch (IOException ioe) {
            throw new WebMotionException("Error to read the file mapping", ioe);
        }
    }

    /**
     * Init the visit with expression to get the action.
     */
    protected void initVisit() {
        visitors.put("/CONFIG", new Visit() {
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/CONFIG/NAME/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                stack.addLast(value);
            }
        });
        
        visitors.put("/CONFIG/VALUE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Config config = mapping.getConfig();
                String name = (String) stack.peekLast();
                value = value.substring(1);
                
                if(Config.MODE.equals(name)) {
                    config.setMode(value);
                } else if(Config.PACKAGE_ACTIONS.equals(name)) {
                    config.setPackageActions(value);
                } else if(Config.PACKAGE_ERRORS.equals(name)) {
                    config.setPackageErrors(value);
                } else if(Config.PACKAGE_FILTERS.equals(name)) {
                    config.setPackageFilters(value);
                } else if(Config.PACKAGE_VIEWS.equals(name)) {
                    config.setPackageViews(value);
                } else if(Config.RELOADABLE.equals(name)) {
                    config.setReloadable(Boolean.valueOf(value));
                } else if(Config.REQUEST_ENCODING.equals(name)) {
                    config.setRequestEncoding(value);
                }
            }
        });
        
        visitors.put("/ERROR", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ErrorRule errorRule = new ErrorRule();
                stack.addLast(errorRule);
                
                List<ErrorRule> errorRules = mapping.getErrorRules();
                errorRules.add(errorRule);
            }
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ERROR/CODE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                errorRule.setError("code:" + value);
            }
        });
        
        visitors.put("/ERROR/EXCEPTION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                String error = errorRule.getError();
                if(error == null) {
                    error = value;
                } else {
                    error += value;
                }
                errorRule.setError(error);
            }
        });
        
        visitors.put("/ERROR/ACTION", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_ACTION);
                
                stack.addLast(action);
                errorRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ERROR/ACTION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                
                String fullName = action.getFullName();
                if(fullName == null) {
                    fullName = value;
                } else {
                    fullName += value;
                }
                action.setFullName(fullName);
            }
        });
        
        visitors.put("/ERROR/VIEW", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_VIEW);
                
                stack.addLast(action);
                errorRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ERROR/VIEW/EXTENSION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                String type = action.getType();
                action.setType(type + "." + value);
            }
        });
        
        visitors.put("/ERROR/VIEW/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                action.setFullName(value);
            }
        });
        
        visitors.put("/ERROR/URL", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_URL);
                
                stack.addLast(action);
                errorRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ERROR/URL/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                value = StringUtils.substringAfter(value, "url:");
                action.setFullName(value);
            }
        });
        
        visitors.put("/FILTER", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FilterRule filterRule = new FilterRule();
                stack.addLast(filterRule);
                
                List<FilterRule> filterRules = mapping.getFilterRules();
                filterRules.add(filterRule);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/FILTER/METHOD/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FilterRule filterRule = (FilterRule) stack.peekLast();
                filterRule.setMethod(value);
            }
        });
        
        visitors.put("/FILTER/PATH/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                value = value.replaceAll("/\\*/", "/[^/]*/");
                value = value.replaceAll("/\\*", "/.*");
                
                FilterRule filterRule = (FilterRule) stack.peekLast();
                Pattern pattern = filterRule.getPattern();
                if(pattern == null) {
                    pattern = Pattern.compile(value);
                } else {
                    pattern = Pattern.compile(pattern.pattern() + value);
                }
                filterRule.setPattern(pattern);
            }
        });
        
        visitors.put("/FILTER/ACTION", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FilterRule filterRule = (FilterRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_ACTION);
                
                stack.addLast(action);
                filterRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/FILTER/ACTION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                
                String fullName = action.getFullName();
                if(fullName == null) {
                    fullName = value;
                } else {
                    fullName += value;
                }
                action.setFullName(fullName);
            }
        });
        
        visitors.put("/FILTER/VIEW", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FilterRule filterRule = (FilterRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_VIEW);
                
                stack.addLast(action);
                filterRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/FILTER/VIEW/EXTENSION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                String type = action.getType();
                action.setType(type + "." + value);
            }
        });
        
        visitors.put("/FILTER/VIEW/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                action.setFullName(value);
            }
        });
        
        visitors.put("/FILTER/URL", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FilterRule filterRule = (FilterRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_URL);
                
                stack.addLast(action);
                filterRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/FILTER/URL/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                value = StringUtils.substringAfter(value, "url:");
                action.setFullName(value);
            }
        });
        
        visitors.put("/ACTION", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = new ActionRule();
                stack.addLast(actionRule);
                
                List<ActionRule> actionRules = mapping.getActionRules();
                actionRules.add(actionRule);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ACTION/METHOD/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = (ActionRule) stack.peekLast();
                actionRule.setMethod(value);
            }
        });
        
        visitors.put("/ACTION/PATH/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FragmentUrl fragment = new FragmentUrl();
                Pattern pattern = Pattern.compile(value);
                fragment.setPattern(pattern);
                
                ActionRule actionRule = (ActionRule) stack.peekLast();
                List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
                ruleUrl.add(fragment);
            }
        });
        
        visitors.put("/ACTION/PATH/VARIABLE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FragmentUrl fragment = new FragmentUrl();
                fragment.setName(value);
                
                ActionRule actionRule = (ActionRule) stack.peekLast();
                List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
                ruleUrl.add(fragment);
            }
        });
        
        visitors.put("/ACTION/ACTION", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = (ActionRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_ACTION);
                
                stack.addLast(action);
                actionRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ACTION/ACTION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                
                String fullName = action.getFullName();
                if(fullName == null) {
                    fullName = value;
                } else {
                    fullName += value;
                }
                action.setFullName(fullName);
            }
        });

        visitors.put("/ACTION/ACTION/VARIABLE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                value = "{" + value + "}";
                
                Action action = (Action) stack.peekLast();
                
                String fullName = action.getFullName();
                if(fullName == null) {
                    fullName = value;
                } else {
                    fullName += value;
                }
                action.setFullName(fullName);
            }
        });

        visitors.put("/ACTION/VIEW", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = (ActionRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_VIEW);
                
                stack.addLast(action);
                actionRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ACTION/VIEW/EXTENSION/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                String type = action.getType();
                action.setType(type + "." + value);
            }
        });
        
        visitors.put("/ACTION/VIEW/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                action.setFullName(value);
            }
        });
        
        visitors.put("/ACTION/URL", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = (ActionRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_URL);
                
                stack.addLast(action);
                actionRule.setAction(action);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ACTION/URL/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                Action action = (Action) stack.peekLast();
                value = StringUtils.substringAfter(value, "url:");
                action.setFullName(value);
            }
        });
        
        visitors.put("/ACTION/DEFAULT_PARAMETERS/PARAMETER", new Visit() {
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ACTION/DEFAULT_PARAMETERS/PARAMETER/NAME/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = (ActionRule) stack.peekLast();
                Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                defaultParameters.put(value, null);
                stack.addLast(value);
            }
        });
        
        visitors.put("/ACTION/DEFAULT_PARAMETERS/PARAMETER/VALUE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                value = value.substring(1); // Remove "="
                
                String key = (String) stack.peekLast();
                ActionRule actionRule = (ActionRule) stack.getFirst();
                Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                defaultParameters.put(key, new String[]{value});
            }
        });
        
        visitors.put("/ACTION/PARAMETERS/PARAMETER", new Visit() {
            @Override
            public void acceptBefore(String value) {
                ActionRule actionRule = (ActionRule) stack.peekLast();
                List<FragmentUrl> ruleParameters = actionRule.getRuleParameters();
                
                FragmentUrl fragment = new FragmentUrl();
                stack.addLast(fragment);
                ruleParameters.add(fragment);
            }
            
            @Override
            public void acceptAfter(String value) {
                stack.removeLast();
            }
        });
        
        visitors.put("/ACTION/PARAMETERS/PARAMETER/NAME/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                fragment.setParam(value);
            }
        });
        
        visitors.put("/ACTION/PARAMETERS/PARAMETER/VARIABLE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                fragment.setName(value);
            }
        });
        
        visitors.put("/ACTION/PARAMETERS/PARAMETER/VALUE/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                Pattern pattern = Pattern.compile(value);
                fragment.setPattern(pattern);
            }
        });
        
        visitors.put("/ACTION/PARAMETERS/PARAMETER/PATTERN/*", new Visit() {
            @Override
            public void acceptBefore(String value) {
                FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                Pattern pattern = Pattern.compile(value);
                fragment.setPattern(pattern);
            }
        });
    }
        
}

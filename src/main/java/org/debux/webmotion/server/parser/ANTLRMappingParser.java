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
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.parser.MappingLanguageParser.mapping_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ANTLR implementation of parser.
 * 
 * @author jruchaud
 */
public class ANTLRMappingParser implements MappingParser {

    private static final Logger log = LoggerFactory.getLogger(ANTLRMappingParser.class);
    
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
            
            // Create mapping
            CommonTree tree = result.tree;
            TreeVisitor treeVisitor = new TreeVisitor();
            
            initVisit();
            treeVisitor.visit(tree, visitorAction);
            return mapping;

        } catch (RecognitionException re) {
            throw new WebMotionException("Error to parse the file mapping", re);
            
        } catch (IOException ioe) {
            throw new WebMotionException("Error to read the file mapping", ioe);
        }
        
    }

    public interface Visit {
        public void accept(String value);
    }
    
    protected Mapping mapping = new Mapping();
    protected Deque<Object> stack;
    
    protected Map<String, Visit> visitors = new HashMap<String, Visit>();
    protected void initVisit() {
        visitors.put("/CONFIG", new Visit() {
            @Override
            public void accept(String value) {
                stack = new LinkedList<Object>();
            }
        });
        
        visitors.put("/CONFIG/NAME/*", new Visit() {
            @Override
            public void accept(String value) {
                stack.addLast(value);
            }
        });
        
        visitors.put("/CONFIG/VALUE/*", new Visit() {
            @Override
            public void accept(String value) {
                Config config = mapping.getConfig();
                String name = (String) stack.pollLast();
                value = value.substring(1);
                
                if(Config.MODE.equals(name)) {
                    config.setMode(name);
                } else if(Config.PACKAGE_ACTIONS.equals(name)) {
                    config.setPackageActions(value);
                } else if(Config.PACKAGE_ERRORS.equals(name)) {
                    config.setPackageErrors(value);
                } else if(Config.PACKAGE_FILTERS.equals(name)) {
                    config.setPackageFilters(value);
                } else if(Config.PACKAGE_VIEWS.equals(name)) {
                    config.setPackageViews(value);
                } else if(Config.RELOADABLE.equals(name)) {
                    config.setReloadable(Boolean.valueOf(name));
                } else if(Config.REQUEST_ENCODING.equals(name)) {
                    config.setRequestEncoding(value);
                }
            }
        });
        
        visitors.put("/ERROR", new Visit() {
            @Override
            public void accept(String value) {
                stack = new LinkedList<Object>();
                
                ErrorRule errorRule = new ErrorRule();
                stack.addLast(errorRule);
                
                List<ErrorRule> errorRules = mapping.getErrorRules();
                errorRules.add(errorRule);
            }
        });
        
        visitors.put("/ERROR/CODE/*", new Visit() {
            @Override
            public void accept(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                errorRule.setError("code:" + value);
            }
        });
        
        visitors.put("/ERROR/EXCEPTION/*", new Visit() {
            @Override
            public void accept(String value) {
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
            public void accept(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_ACTION);
                
                stack.addLast(action);
                errorRule.setAction(action);
            }
        });
        
        visitors.put("/ERROR/ACTION/*", new Visit() {
            @Override
            public void accept(String value) {
                Action action = (Action) stack.peekLast();
                String fullName = action.getFullName();
                String className = action.getClassName();
                String methodName = action.getMethodName();
                
                if(fullName == null) {
                    fullName = value;
                } else {
                    fullName += value;
                }
                action.setFullName(fullName);
                
                if(className == null) {
                    className = methodName;
                } else {
                    className += methodName;
                }
                
                action.setClassName(className);
                methodName = value;
                action.setMethodName(methodName);
            }
        });
        
        visitors.put("/ERROR/VIEW", new Visit() {
            @Override
            public void accept(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_VIEW);
                
                stack.addLast(action);
                errorRule.setAction(action);
            }
        });
        
        visitors.put("/ERROR/VIEW/EXTENSION/*", new Visit() {
            @Override
            public void accept(String value) {
                Action action = (Action) stack.peekLast();
                String type = action.getType();
                action.setType(type + "." + value);
            }
        });
        
        visitors.put("/ERROR/VIEW/*", new Visit() {
            @Override
            public void accept(String value) {
                Action action = (Action) stack.peekLast();
                action.setFullName(value);
            }
        });
        
        visitors.put("/ERROR/URL", new Visit() {
            @Override
            public void accept(String value) {
                ErrorRule errorRule = (ErrorRule) stack.peekLast();
                
                Action action = new Action();
                action.setType(Action.TYPE_URL);
                
                stack.addLast(action);
                errorRule.setAction(action);
            }
        });
        
        visitors.put("/ERROR/URL/*", new Visit() {
            @Override
            public void accept(String value) {
                Action action = (Action) stack.peekLast();
                value = StringUtils.substringAfter(value, "url:");
                action.setFullName(value);
            }
        });
    }
    
    protected TreeVisitorAction visitorAction = new TreeVisitorAction() {
        
        protected String path = "";

        @Override
        public Object pre(Object t) {
            CommonTree tree = (CommonTree) t;

            Token token = tree.getToken();
            int type = token.getType();
            String text = token.getText();

            if(type == MappingLanguageParser.DOLLAR 
                            && !"$".equals(text)) {
                path += "/" + text;
                
                log.info(">>" + path);
                Visit visit = visitors.get(path);
                if(visit != null) {
                    visit.accept(null);
                }
            } else {
                log.info(">>" + path + " = " + text);
                Visit visit = visitors.get(path + "/*");
                if(visit != null) {
                    visit.accept(text);
                }
            }
            
            return t;
        }

        @Override
        public Object post(Object t) {
            CommonTree tree = (CommonTree) t;

            Token token = tree.getToken();
            int type = token.getType();
            String text = token.getText();

            if(type == MappingLanguageParser.DOLLAR 
                            && !"$".equals(text)) {
                path = StringUtils.substringBeforeLast(path, "/");
            }
            
            return t;
        }
    };

}

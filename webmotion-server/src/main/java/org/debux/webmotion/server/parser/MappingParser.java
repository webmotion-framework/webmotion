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
package org.debux.webmotion.server.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.FragmentUrl;
import org.nuiton.util.Resource;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ANTLR implementation of parser the file mapping.
 * 
 * @author jruchaud
 */
public class MappingParser {

    private static final Logger log = LoggerFactory.getLogger(MappingParser.class);
    
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

    /**
     * Implement tree visitor to excute the parsing with rules based on path.
     */
    public class TreeVisitor {

        /** Current mapping parsed */
        protected Mapping mapping;
        
        /** Rules uses to parse */
        protected Map<String, Visit> rules;
        
        /** Stack during the parsing */
        protected Deque<Object> stack;
        
        public TreeVisitor(Mapping m) {
            this.mapping = m;
            this.stack = new LinkedList<Object>();
            this.rules = new HashMap<String, Visit>();

            rules.put("/configRule", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/configRule/configName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    stack.addLast(value);
                }
            });

            rules.put("/configRule/configValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Config config = mapping.getConfig();

                    String name = (String) stack.peekLast();
                    config.set(name, value);
                }
            });

            rules.put("/errorRule", new Visit() {
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

            rules.put("/errorRule/errorCode", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setError(value);
                }

                @Override
                public void acceptAfter(String value) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setMapping(mapping);
//                    errorRule.setLine(token.getLine());
                }
            });

            rules.put("/errorRule/errorException", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setError(value);
                }

                @Override
                public void acceptAfter(String valuen) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setMapping(mapping);
//                    errorRule.setLine(token.getLine());
                }
            });

            rules.put("/errorRule/errorJava/errorJavaValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.ACTION);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/errorRule/errorView/errorViewValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.VIEW);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/errorRule/errorUrl/errorUrlValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.URL);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/filterRule", new Visit() {
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

            rules.put("/filterRule/Method/MethodValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    List<String> methods = filterRule.getMethods();
                    methods.add(value);
                }

                @Override
                public void acceptAfter(String value) {
                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setMapping(mapping);
//                    filterRule.setLine(token.getLine());
                }
            });

            rules.put("/filterRule/filterPath", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    value = value.replaceAll("/\\*/", "/[^/]*/");
                    value = value.replaceAll("/\\*", "/.*");
                    value = "^" + value + "$";

                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setPattern(Pattern.compile(value));
                }
            });

            rules.put("/filterRule/filterJava/filterJavaValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.ACTION);
                    action.setFullName(value);

                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setAction(action);
                }
            });

            rules.put("/filterRule/filterView", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.VIEW);
                    action.setFullName(value);

                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setAction(action);
                }
            });

            rules.put("/filterRule/filterUrl", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.URL);
                    action.setFullName(value);

                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setAction(action);
                }
            });

            rules.put("/filterRule/filterParameters/filterParameter", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/filterRule/filterParameters/filterParameter/filterName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    Map<String, String[]> defaultParameters = filterRule.getDefaultParameters();
                    defaultParameters.put(value, null);
                    stack.addLast(value);
                }
            });

            rules.put("/filterRule/filterParameters/filterParameter/filterValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String key = (String) stack.peekLast();
                    FilterRule actionRule = (FilterRule) stack.getFirst();
                    Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                    defaultParameters.put(key, new String[]{value});            }
            });

            rules.put("/actionRule", new Visit() {
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

            rules.put("/actionRule/Method/MethodValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    List<String> methods = actionRule.getMethods();
                    methods.add(value);
                }

                @Override
                public void acceptAfter(String value) {
                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setMapping(mapping);
//                    actionRule.setLine(token.getLine());
                }
            });

            rules.put("/actionRule/actionPath/actionPathSlash", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = new FragmentUrl();
                    Pattern pattern = Pattern.compile("^/$");
                    fragment.setPattern(pattern);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
                    ruleUrl.add(fragment);
                }
            });

            rules.put("/actionRule/actionPath/actionPathStatic", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = new FragmentUrl();
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();
                    ruleUrl.add(fragment);
                }
            });

            rules.put("/actionRule/actionPath/actionVariable", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    List<FragmentUrl> ruleUrl = actionRule.getRuleUrl();

                    FragmentUrl fragment = new FragmentUrl();
                    ruleUrl.add(fragment);

                    stack.addLast(fragment);
                }

                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/actionRule/actionPath/actionVariable/QualifiedIdentifier", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    fragment.setName(value);
                }
            });

            rules.put("/actionRule/actionPath/actionVariable/actionPattern", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    value = value.replaceAll("\\\\\\{", "{");
                    value = value.replaceAll("\\\\\\}", "}");
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);
                }
            });

            rules.put("/actionRule/actionJava", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.ACTION);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setAction(action);

                    stack.addLast(action);
                }

                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/actionRule/actionJava/actionJavaValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = (Action) stack.peekLast();
                    action.setFullName(value);
                }
            });

            rules.put("/actionRule/actionJava/actionJavaType", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = (Action) stack.peekLast();
                    if (value == null || value.isEmpty()) {
                        action.setAsync(null);
                    } else if (value.equalsIgnoreCase("ASYNC:")) {
                        action.setAsync(true);
                    } else if (value.equalsIgnoreCase("SYNC:")) {
                        action.setAsync(false);
                    }
                }
            });

            rules.put("/actionRule/actionView/actionViewValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.VIEW);
                    action.setFullName(value);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setAction(action);
                }
            });

            rules.put("/actionRule/actionUrl/actionUrlValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.URL);
                    action.setFullName(value);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setAction(action);
                }
            });

            rules.put("/actionRule/actionDefaultParameters/actionDefaultParameter", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/actionRule/actionDefaultParameters/actionDefaultParameter/actionDefaultName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                    defaultParameters.put(value, null);
                    stack.addLast(value);
                }
            });

            rules.put("/actionRule/actionDefaultParameters/actionDefaultParameter/actionDefaultValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String key = (String) stack.peekLast();
                    ActionRule actionRule = (ActionRule) stack.getFirst();
                    Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                    defaultParameters.put(key, new String[]{value});
                }
            });

            rules.put("/actionRule/actionParameters/actionParameter", new Visit() {
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

            rules.put("/actionRule/actionParameters/actionParameter/actionName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    fragment.setParam(value);
                }
            });

            rules.put("/actionRule/actionParameters/actionParameter/actionValue/actionVariable/QualifiedIdentifier", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    fragment.setName(value);
                }
            });

            rules.put("/actionRule/actionParameters/actionParameter/actionValue/actionVariable/actionPattern", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    value = value.replaceAll("\\\\\\{", "{");
                    value = value.replaceAll("\\\\\\}", "}");
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);
                }
            });

            rules.put("/actionRule/actionParameters/actionParameter/actionValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);
                }
            });

            rules.put("/extensionRule/extensionPath", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    stack.addLast(value);
                }
            });

            rules.put("/extensionRule/extensionFile", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String path = (String) stack.removeLast();

                    List<Mapping> extensionsRules = mapping.getExtensionsRules();

                    try {
                        ClassLoader classLoader = getClass().getClassLoader();
                        List<URL> resources = Resource.getResources(value, classLoader);
                        for (URL resource : resources) {
                            MappingParser parser = new MappingParser();

                            Mapping extensionMapping = parser.parse(resource);
                            extensionMapping.setExtensionPath(path);
                            extensionsRules.add(extensionMapping);
                        }

                    } catch (IOException ex) {
                        throw new WebMotionException("Error during read the file mapping with path in " + value, ex);
                    }
                }
            });
        }
        
        public void visitTree(Node node, InputBuffer inputBuffer, String path) {
            String label = node.getLabel();
            boolean notSkip = !label.startsWith("'") &&
                    !label.equals("mapping") &&
                    !label.startsWith("section") &&
                    !label.equals("Sequence") &&
                    !label.equals("Optional") &&
                    !label.equals("FirstOf") &&
                    !label.equals("ZeroOrMore") &&
                    !label.equals("OneOrMore");
            
            if (notSkip) {
                String text = org.parboiled.common.StringUtils.escape(ParseTreeUtils.getNodeText(node, inputBuffer));
                path += "/" + label;
                
                log.info("Before " + path + " = " + text);
                Visit visit = rules.get(path);
                if(visit != null) {
                    visit.acceptBefore(text);
                }
            }
            
            for (Object sub : node.getChildren()) {
                visitTree((Node) sub, inputBuffer, path);
            }
            
            if (notSkip) {
                String text = org.parboiled.common.StringUtils.escape(ParseTreeUtils.getNodeText(node, inputBuffer));
                
                log.info("After " + path + " = " + text);
                Visit visit = rules.get(path);
                if(visit != null) {
                    visit.acceptAfter(text);
                }
            }
        }
    }

    /**
     * @return true if the file exists otherwise false
     */
    public boolean exists(String fileName) {
        return getClass().getResource(fileName) != null;
    }
    
    /**
     * Parse a mapping file
     * @param fileName file name
     * @param defaultConfig default config
     * @return the representation of the file
     */
    public Mapping parse(String fileName) {
        URL url = getClass().getResource(fileName);
        return parse(url);
    }
    
    /**
     * Parse a mapping file on url
     * @param url mapping file to parse
     * @param defaultConfig default config
     * @return the representation of the file
     */
    protected Mapping parse(URL url) {
        Mapping mapping = new Mapping();
        mapping.setName(url.toExternalForm());
                
        try {
            InputStream stream = url.openStream();
            String content = IOUtils.toString(stream);
            
            ParboiledMappingParser parser = Parboiled.createParser(ParboiledMappingParser.class);
            BasicParseRunner runner = new BasicParseRunner(parser.mapping());
            ParsingResult<?> result = runner.run(content);
            
            // Visit tree
            TreeVisitor tree = new TreeVisitor(mapping);
            tree.visitTree(result.parseTreeRoot, result.inputBuffer, "");
            return mapping;

        } catch (IOException ioe) {
            throw new WebMotionException("Error to read the file mapping", ioe);
        }
    }
        
}

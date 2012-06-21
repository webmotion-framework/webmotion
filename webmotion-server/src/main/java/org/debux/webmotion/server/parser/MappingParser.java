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
import org.debux.webmotion.server.mapping.*;
import org.debux.webmotion.server.mapping.Properties.PropertiesItem;
import org.nuiton.util.Resource;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parboile tree has been readed to generate the mapping object.
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
         * @param inputBuffer current buffer
         * @param node node of element
         */
        public void acceptBefore(Node node, InputBuffer inputBuffer) {
            String value = org.parboiled.common.StringUtils.escape(ParseTreeUtils.getNodeText(node, inputBuffer));
            log.debug("with value = " + value);
            acceptBefore(value);
        }
        
        /**
         * Accept visit before explore child, by default do nothing.
         * @param value value of element
         */
        public void acceptBefore(String value) {
        }
        
        /**
         * Accept visit after explore child, by default do nothing.
         * @param inputBuffer current buffer
         * @param node node of element
         */
        public void acceptAfter(Node node, InputBuffer inputBuffer) {
            String value = org.parboiled.common.StringUtils.escape(ParseTreeUtils.getNodeText(node, inputBuffer));
            acceptAfter(value);
        }
        
        /**
         * Accept visit after explore child, by default do nothing.
         * @param value value of element
         */
        public void acceptAfter(String value) {
        }
        
        /**
         * @param inputBuffer current buffer
         * @param node node of element
         * @return line number of node
         */
        public int getLine(Node node, InputBuffer inputBuffer) {
            int index = node.getStartIndex();
            Position position = inputBuffer.getPosition(index);
            return position.line;
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
        
        public TreeVisitor() {
            this.mapping = new Mapping();
            this.stack = new LinkedList<Object>();
            this.rules = new HashMap<String, Visit>();
            
            createRules();
        }

        public Mapping getMapping() {
            return mapping;
        }
        
        protected void createRules() {
            rules.put("/sectionConfig/configRule", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });
            
            rules.put("/sectionConfig/configRule/configName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    stack.addLast(value.trim());
                }
            });

            rules.put("/sectionConfig/configRule/configValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Config config = mapping.getConfig();

                    String name = (String) stack.peekLast();
                    config.set(name, value.trim());
                }
            });

            rules.put("/sectionProperties", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });
            
            rules.put("/sectionProperties/propertiesSection/propertiesIdentifier", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    PropertiesItem item = new PropertiesItem(value);
                    stack.addLast(item);
                    
                    Properties properties = mapping.getProperties();
                    properties.addItem(item);
                }
            });

            rules.put("/sectionProperties/propertiesRule/propertiesName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    stack.addLast(value.trim());
                }
            });

            rules.put("/sectionProperties/propertiesRule/propertiesValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String key = (String) stack.removeLast();
                    PropertiesItem item = (PropertiesItem) stack.peekLast();
                            
                    value = value.replaceAll("\\\\\\\\n\\p{Space}*", "");
                    item.addProperty(key, value.trim());
                }
            });
            
            rules.put("/sectionErrors/errorRule", new Visit() {
                @Override
                public void acceptBefore(Node node, InputBuffer inputBuffer) {
                    ErrorRule errorRule = new ErrorRule();
                    errorRule.setLine(getLine(node, inputBuffer));
                    errorRule.setMapping(mapping);
                    stack.addLast(errorRule);

                    List<ErrorRule> errorRules = mapping.getErrorRules();
                    errorRules.add(errorRule);
                }

                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/sectionErrors/errorRule/errorCode", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setError(value);
                }
            });

            rules.put("/sectionErrors/errorRule/errorException", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setError(value);
                }

                @Override
                public void acceptAfter(String valuen) {
                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setMapping(mapping);
                }
            });

            rules.put("/sectionErrors/errorRule/errorJava/errorJavaValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.ACTION);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/sectionErrors/errorRule/errorView/errorViewValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.VIEW);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/sectionErrors/errorRule/errorRedirect/errorRedirectValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.REDIRECT);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/sectionErrors/errorRule/errorForward/errorForwardValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.FORWARD);
                    action.setFullName(value);

                    ErrorRule errorRule = (ErrorRule) stack.peekLast();
                    errorRule.setAction(action);
                }
            });

            rules.put("/sectionFilters/filterRule", new Visit() {
                @Override
                public void acceptBefore(Node node, InputBuffer inputBuffer) {
                    FilterRule filterRule = new FilterRule();
                    filterRule.setLine(getLine(node, inputBuffer));
                    filterRule.setMapping(mapping);
                    stack.addLast(filterRule);

                    List<FilterRule> filterRules = mapping.getFilterRules();
                    filterRules.add(filterRule);
                }

                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/sectionFilters/filterRule/Method/MethodValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    List<String> methods = filterRule.getMethods();
                    methods.add(value);
                }
            });

            rules.put("/sectionFilters/filterRule/filterPath", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    value = value.replaceAll("/\\*/", "/[^/]*/");
                    value = value.replaceAll("/\\*", "/.*");
                    value = "^" + value + "$";

                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setPattern(Pattern.compile(value));
                }
            });

            rules.put("/sectionFilters/filterRule/filterJava/filterJavaValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.ACTION);
                    action.setFullName(value);

                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    filterRule.setAction(action);
                }
            });

            rules.put("/sectionFilters/filterRule/filterParameters/filterParameter", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/sectionFilters/filterRule/filterParameters/filterParameter/filterName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FilterRule filterRule = (FilterRule) stack.peekLast();
                    Map<String, String[]> defaultParameters = filterRule.getDefaultParameters();
                    defaultParameters.put(value, null);
                    stack.addLast(value);
                }
            });

            rules.put("/sectionFilters/filterRule/filterParameters/filterParameter/filterValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String key = (String) stack.peekLast();
                    FilterRule actionRule = (FilterRule) stack.getFirst();
                    Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                    defaultParameters.put(key, new String[]{value});
                }
            });

            rules.put("/sectionActions/actionRule", new Visit() {
                @Override
                public void acceptBefore(Node node, InputBuffer inputBuffer) {
                    ActionRule actionRule = new ActionRule();
                    actionRule.setLine(getLine(node, inputBuffer));
                    actionRule.setMapping(mapping);
                    stack.addLast(actionRule);

                    List<ActionRule> actionRules = mapping.getActionRules();
                    actionRules.add(actionRule);
                }

                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/sectionActions/actionRule/Method/MethodValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    List<String> methods = actionRule.getMethods();
                    methods.add(value);
                }
            });

            rules.put("/sectionActions/actionRule/actionPath/actionPathSlash", new Visit() {
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

            rules.put("/sectionActions/actionRule/actionPath/actionPathStatic", new Visit() {
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

            rules.put("/sectionActions/actionRule/actionPath/actionVariable", new Visit() {
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

            rules.put("/sectionActions/actionRule/actionPath/actionVariable/QualifiedIdentifier", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    fragment.setName(value);
                }
            });

            rules.put("/sectionActions/actionRule/actionPath/actionVariable/actionPattern", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    value = value.replaceAll("\\\\\\{", "{");
                    value = value.replaceAll("\\\\\\}", "}");
                    
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);
                }
            });

            rules.put("/sectionActions/actionRule/actionJava", new Visit() {
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

            rules.put("/sectionActions/actionRule/actionJava/actionJavaValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = (Action) stack.peekLast();
                    action.setFullName(value);
                }
            });

            rules.put("/sectionActions/actionRule/actionJava/actionJavaType", new Visit() {
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

            rules.put("/sectionActions/actionRule/actionView/actionViewValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.VIEW);
                    action.setFullName(value);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setAction(action);
                }
            });

            rules.put("/sectionActions/actionRule/actionRedirect/actionRedirectValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.REDIRECT);
                    action.setFullName(value);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setAction(action);
                }
            });

            rules.put("/sectionActions/actionRule/actionForward/actionForwardValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    Action action = new Action();
                    action.setType(Action.Type.FORWARD);
                    action.setFullName(value);

                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    actionRule.setAction(action);
                }
            });

            rules.put("/sectionActions/actionRule/actionDefaultParameters/actionDefaultParameter", new Visit() {
                @Override
                public void acceptAfter(String value) {
                    stack.removeLast();
                }
            });

            rules.put("/sectionActions/actionRule/actionDefaultParameters/actionDefaultParameter/actionDefaultName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    ActionRule actionRule = (ActionRule) stack.peekLast();
                    Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                    defaultParameters.put(value, null);
                    stack.addLast(value);
                }
            });

            rules.put("/sectionActions/actionRule/actionDefaultParameters/actionDefaultParameter/actionDefaultValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String key = (String) stack.peekLast();
                    ActionRule actionRule = (ActionRule) stack.getFirst();
                    Map<String, String[]> defaultParameters = actionRule.getDefaultParameters();
                    defaultParameters.put(key, new String[]{value});
                }
            });

            rules.put("/sectionActions/actionRule/actionParameters/actionParameter", new Visit() {
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

            rules.put("/sectionActions/actionRule/actionParameters/actionParameter/actionName", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    fragment.setParam(value);
                }
            });

            rules.put("/sectionActions/actionRule/actionParameters/actionParameter/actionVariable/QualifiedIdentifier", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    fragment.setName(value);
                }
            });

            rules.put("/sectionActions/actionRule/actionParameters/actionParameter/actionVariable/actionPattern", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    value = value.replaceAll("\\\\\\{", "{");
                    value = value.replaceAll("\\\\\\}", "}");
                    
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);
                }
            });

            rules.put("/sectionActions/actionRule/actionParameters/actionParameter/actionValue", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    FragmentUrl fragment = (FragmentUrl) stack.peekLast();
                    Pattern pattern = Pattern.compile("^" + value + "$");
                    fragment.setPattern(pattern);
                }
            });

            rules.put("/sectionExtensions/extensionRule/extensionPath", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    stack.addLast(value);
                }
            });

            rules.put("/sectionExtensions/extensionRule/extensionFile", new Visit() {
                @Override
                public void acceptBefore(String value) {
                    String path = (String) stack.removeLast();

                    List<Mapping> extensionsRules = mapping.getExtensionsRules();

                    try {
                        ClassLoader classLoader = getClass().getClassLoader();
                        List<URL> resources = Resource.getResources(value, classLoader);
                        if (resources.isEmpty()) {
                            log.warn("Extension not found for " + value);
                        }
                        
                        for (URL resource : resources) {
                            MappingParser parser = new MappingParser();

                            Mapping extensionMapping = parser.parse(resource);
                            extensionMapping.setExtensionPath(path);
                            extensionsRules.add(extensionMapping);
                            
                            Properties extensionProperties = extensionMapping.getProperties();
                            Properties properties = mapping.getProperties();
                            properties.addProperties(extensionProperties);
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
                    !label.equals("sections") &&
                    !label.equals("EOI") &&
                    !label.equals("Sequence") &&
                    !label.equals("Optional") &&
                    !label.equals("FirstOf") &&
                    !label.equals("ZeroOrMore") &&
                    !label.equals("OneOrMore");
            
            if (notSkip) {
                path += "/" + label;
                
                log.debug("Before " + path);
                Visit visit = rules.get(path);
                if (visit != null) {
                    visit.acceptBefore(node, inputBuffer);
                }
            }
            
            for (Object sub : node.getChildren()) {
                visitTree((Node) sub, inputBuffer, path);
            }
            
            if (notSkip) {
                log.debug("After " + path);
                Visit visit = rules.get(path);
                if (visit != null) {
                    visit.acceptAfter(node, inputBuffer);
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
        try {
            String name = url.toExternalForm();
            
            // Read the content in file
            InputStream stream = url.openStream();
            String content = IOUtils.toString(stream);
            
            // Parse the content
            ParboiledMappingParser parser = Parboiled.createParser(ParboiledMappingParser.class);
            ReportingParseRunner runner = new ReportingParseRunner(parser.mapping());
            ParsingResult<?> result = runner.run(content);
            
            // Detect if the parser has an errors
            if (result.hasErrors()) {
                log.error(ErrorUtils.printParseErrors(result));
                throw new WebMotionException("Error to parse the mapping file");
            }
            
            // Visit tree
            TreeVisitor tree = new TreeVisitor();
            tree.visitTree(result.parseTreeRoot, result.inputBuffer, "");
            
            // Get the mapping after the visit
            Mapping mapping = tree.getMapping();
            mapping.setName(name);

            return mapping;

        } catch (IOException ioe) {
            throw new WebMotionException("Error to read the file mapping", ioe);
        }
    }
        
}

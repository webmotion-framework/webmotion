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
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.parser.MappingLanguageParser.mapping_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of parser with use split and pattern to extract the 
 * mapping.
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
            mapping_return mapping = parser.mapping();
            
            // Verify parsing errors
            List<String> errors = reporter.getErrors();
            
            // Create mapping
            CommonTree tree = mapping.tree;
            TreeVisitor treeVisitor = new TreeVisitor();
            TreeVisitorAction visitorAction = new TreeVisitorAction() {
                @Override
                public Object pre(Object t) {
                    CommonTree tree = (CommonTree) t;
                    
                    Token token = tree.getToken();
                    int type = token.getType();
                    String text = token.getText();
                    
                    if(type == MappingLanguageParser.SECTION_CONFIG) {
                        
                    } else if(type == MappingLanguageParser.SECTION_FILTERS) {
                        
                    } else if(type == MappingLanguageParser.SECTION_ERRORS) {
                        
                    } else if(type == MappingLanguageParser.SECTION_ACTIONS) {
                        
                    }
                    
                    return t;
                }

                @Override
                public Object post(Object t) {
                    return t;
                }
            };
            treeVisitor.visit(tree, visitorAction);

        } catch (RecognitionException re) {
            throw new WebMotionException("Error to parse the file mapping", re);
            
        } catch (IOException ioe) {
            throw new WebMotionException("Error to read the file mapping", ioe);
        }
        
        return null;
    }

}

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
import java.net.URL;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.antlr.stringtemplate.StringTemplate;
import org.debux.webmotion.server.parser.MappingLanguageParser.mapping_return;

/**
 * Test on extract mapping.
 * 
 * @author julien
 */
public class GrammarTest {

    private static final Logger log = LoggerFactory.getLogger(GrammarTest.class);

    public class StdErrReporter implements ErrorReporter {
        @Override
        public void reportError(String error) {
            log.error("Error = " + error);
        }
    }
    
    @Factory
    public Object[] testFactory() {
        return new Object[] {
            new RunGrammar("mapping/webmotion-test.mapping"),
            new RunGrammar("mapping/debox-web.mapping")
        };
    }
    
    public class RunGrammar {
        
        protected String fileName;

        public RunGrammar(String fileName) {
            this.fileName = fileName;
        }
        
        @Test
        public void testParser() throws RecognitionException, IOException {
            StdErrReporter reporter = new StdErrReporter();
            
            ClassLoader classLoader = GrammarTest.class.getClassLoader();
            URL resource = classLoader.getResource(fileName);
            String path = resource.getPath();
            ANTLRFileStream stream = new ANTLRFileStream(path);
            
            MappingLanguageLexer lexer = new MappingLanguageLexer(stream);
            lexer.setErrorReporter(reporter);

            CommonTokenStream tokens = new CommonTokenStream();
            tokens.setTokenSource(lexer);

            MappingLanguageParser parser = new MappingLanguageParser(tokens);
            parser.setErrorReporter(reporter);
            mapping_return mapping = parser.mapping();
            CommonTree tree = mapping.tree;
            printTree(tree, 0);
            
            DOTTreeGenerator gen = new DOTTreeGenerator();
            StringTemplate st = gen.toDOT(tree);
            log.info(st.toString());
            
            TreeVisitor treeVisitor = new TreeVisitor(new CommonTreeAdaptor());
            TreeVisitorAction visitorAction = new TreeVisitorAction() {
                @Override
                public Object pre(Object o) {
                    CommonTree tree = (CommonTree) o;
                    log.info(tree.getText());
                    return o;
                }

                @Override
                public Object post(Object o) {
                    return o;
                }
            };
            treeVisitor.visit(tree, visitorAction);
        }
        
    }
    
    public void printTree(CommonTree t, int indent) {
        if (t != null) {
            StringBuffer sb = new StringBuffer(indent);
            for (int i = 0; i < indent; i++) {
                sb = sb.append("   ");
            }
            for (int i = 0; i < t.getChildCount(); i++) {
                log.info(sb.toString() + t.getChild(i).toString());
                printTree((CommonTree) t.getChild(i), indent + 1);
            }
        }
    }
    
}

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

import java.io.FileInputStream;
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
public class ANTLRParserTest {

    private static final Logger log = LoggerFactory.getLogger(ANTLRParserTest.class);

    @Factory
    public Object[] testFactory() {
        return new Object[] {
            new RunGrammar("mapping/webmotion-test.mapping"),
            new RunGrammar("mapping/debox-web.mapping")
        };
    }
    
    public class RunGrammar {
        
        protected String fileName;
        protected MappingParser parser;

        public RunGrammar(String fileName) {
            this.fileName = fileName;
            this.parser = new ANTLRMappingParser();
        }
        
        @Test
        public void testParser() throws RecognitionException, IOException {
            ClassLoader classLoader = ANTLRParserTest.class.getClassLoader();
            URL resource = classLoader.getResource(fileName);
            String path = resource.getPath();
            FileInputStream stream = new FileInputStream(path);
            parser.parse(stream);
        }
        
    }
    
}

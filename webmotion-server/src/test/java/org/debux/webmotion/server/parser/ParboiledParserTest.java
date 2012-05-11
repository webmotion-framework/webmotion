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
import org.apache.commons.io.IOUtils;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.StringUtils;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 * Test on extract mapping.
 * 
 * @author julien
 */
public class ParboiledParserTest {

    private static final Logger log = LoggerFactory.getLogger(ParboiledParserTest.class);

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
        public void testParser() throws IOException {
            ClassLoader classLoader = ParboiledParserTest.class.getClassLoader();
            InputStream input = classLoader.getResourceAsStream(fileName);
            String content = IOUtils.toString(input);
            
            ParboiledMappingParser parser = Parboiled.createParser(ParboiledMappingParser.class);
            BasicParseRunner runner = new BasicParseRunner(parser.mapping());
            ParsingResult<?> result = runner.run(content);
            
            String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
            System.out.println(parseTreePrintOut);
            System.out.println("error = " + result.hasErrors());
            
            StringBuilder sb = printTree(result.parseTreeRoot, result.inputBuffer, "", new StringBuilder());
            System.out.println(sb.toString());
        }
        
        private StringBuilder printTree(Node node, InputBuffer inputBuffer, String path, StringBuilder sb) {
            String label = node.getLabel();
            if (!label.startsWith("'") &&
                    !label.equals("mapping") &&
                    !label.startsWith("section") &&
                    !label.equals("Sequence") &&
                    !label.equals("Optional") &&
                    !label.equals("FirstOf") &&
                    !label.equals("ZeroOrMore") &&
                    !label.equals("OneOrMore")) {
                
                String nodeText = StringUtils.escape(ParseTreeUtils.getNodeText(node, inputBuffer));
                
                path += "/" + label;
                sb.append(path).append(" = ").append(nodeText).append("\n");
            }
            
            for (Object sub : node.getChildren()) {
                printTree((Node) sub, inputBuffer, path, sb);
            }
            return sb;
        }
    }
    
}

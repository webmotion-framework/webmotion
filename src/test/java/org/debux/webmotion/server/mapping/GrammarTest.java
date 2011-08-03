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
package org.debux.webmotion.server.mapping;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on extract mapping.
 * 
 * @author julien
 */
public class GrammarTest {

    private static final Logger log = LoggerFactory.getLogger(GrammarTest.class);

    public class StdErrReporter implements IErrorReporter {
        @Override
        public void reportError(String error) {
            log.error("Error = " + error);
        }
    }
    
    @Test
    public void testMain() throws RecognitionException {
        StdErrReporter reporter = new StdErrReporter();
        
        ANTLRStringStream stream = new ANTLRStringStream(
                 "[config]\n"
                + "package.views=tutu.t_ut-u/...\n"
                + "reloadable=true\n"
                + " \t\n"
                + "#test\n"
                + "[filters]\n"
                + "*   /    Action.test\n"
                );
        MappingLanguageLexer lexer = new MappingLanguageLexer(stream);
        lexer.setErrorReporter(reporter);
        
       	CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        
        MappingLanguageParser parser = new MappingLanguageParser(tokens);
        parser.setErrorReporter(reporter);
        parser.mapping();
    }
    
}

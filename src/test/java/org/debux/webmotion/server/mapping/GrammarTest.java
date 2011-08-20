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
                "[config]\n" +
                "package.views=org.delux.webmotion.test.views\n" +
                "package.filters=org.debux.webmotion.test.filters\n" +
                "package.actions=org.debux.webmotion.test.actions\n" +
                "package.errors=org.debux.webmotion.test.errors\n" +
                "\n" +
                "#reloadable=true\n" +
                "#mode=statefull/stateless\n" +
                "#request.encoding=UTF-8\n" +
                "\n" + // line 10
                "[errors]\n" +
                "java.lang.NullPointerException                              Error.npeError\n" +
                "code:404                                                    Error.notFound\n" +
                "code:500                                                    Error.error\n" +
                "\n" +
                "[filters]\n" +
                "*           /*                                              Filters.log\n" +
                "*           /test/hello/*                                   Filters.param\n" +
                "*           /test/*/*                                       Filters.log\n" +
                "\n" + // line 20
                "[actions]\n" +
                "#<method>   <url>                                           <action>\n" +
                "GET         /                                               Test.index\n" +
                "*           /index                                          view.html:test.index\n" +
                "*           /google                                         url:http://www.google.fr\n" +
                "*           /info/                                          url:/test/hello/3\n" +
                "\n" +
                "*           /include                                        view.jsp:test.include\n" +
                "\n" +
                "*           /info/{infoId}                                  Test.all\n" + // line 30
                "*           /info/{infoId}/                                 Test.run\n" +
                "*           /run/?param                                     Test.all\n" +
                "*           /run?param                                      Test.run\n" +
                "*           /url/run/                                       Test.run\n" +
                "*           /url/run                                        Test.all\n" +
                "\n" +
                "*           /test/run?param=test                            Test.all\n" +
                "*           /test/run                                       Test.run\n" +
                "*           /sub                                            sub.Subaction.index\n" +
                "*           /admin                                          view.jsp:sub.subaction.admin.index\n" + // line 40
                "\n" +
                "*           /static                                         Test.hello          value=coco,number=9\n" +
                "*           /test/hello?who={value:aaaa*}&number={number}   action:Test.hello\n" +
                "*           /test/{value}/{number}                          Test.hello\n" +
                "\n" +
                "*           /{sub}/{class}/{method}/{test}                  {sub}.{class}.{method}{test}\n" +
                "\n" +
                "# All match\n" +
                "*           /{class}/{method}                               {class}.{method}\n"
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

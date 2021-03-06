/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 * Test on extract mapping.
 * 
 * @author julien
 */
public class DefaultMappingParserTest {

    private static final Logger log = LoggerFactory.getLogger(DefaultMappingParserTest.class);

    @Factory
    public Object[] testFactory() {
        return new Object[] {
            new RunGrammar("mapping/webmotion-test.mapping"),
            new RunGrammar("mapping/debox-web.mapping")
        };
    }
    
    public class RunGrammar {
        
        protected String fileName;
        protected DefaultMappingParser parser;

        public RunGrammar(String fileName) {
            this.fileName = fileName;
            this.parser = new DefaultMappingParser();
        }
        
        @Test
        public void testParser() throws IOException {
            ClassLoader classLoader = DefaultMappingParserTest.class.getClassLoader();
            URL resource = classLoader.getResource(fileName);
            parser.parse(resource);
        }
        
    }
    
}

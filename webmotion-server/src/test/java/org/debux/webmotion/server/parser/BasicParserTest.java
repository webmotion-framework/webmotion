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

import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.FilterRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test on extract mapping.
 * 
 * @author julien
 */
public class BasicParserTest {

    private static final Logger log = LoggerFactory.getLogger(BasicParserTest.class);

    protected BasicMappingParser parser;
    
    @BeforeMethod
    public void createParser() {
        parser = new BasicMappingParser();
    }
    
    @Test
    public void testExtractSectionErrorsException() {
        ErrorRule result = parser.extractSectionErrors("java.lang.NullPointerException Error.error");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionErrorsCode() {
        ErrorRule result = parser.extractSectionErrors("code:404 view.jsp:error.error");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionFilters() {
        FilterRule result = parser.extractSectionFilters("* /* Filters.log");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionActionsBasic() {
        ActionRule result = parser.extractSectionActions("* / Test.index");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionActionsView() {
        ActionRule result = parser.extractSectionActions("* /admin view.jsp:sub.subaction.admin.index");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionActionsUrl() {
        ActionRule result = parser.extractSectionActions("* /google url:http://www.google.fr");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionActionsDynamic() {
        ActionRule result = parser.extractSectionActions("* /{sub}/{class}/{method}/{test} {sub}.{class}.{method}{test}");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionActionsStatic() {
        ActionRule result = parser.extractSectionActions("* /static Test.hello value=coco,number=9");
        AssertJUnit.assertNotNull(result);
    }
    
    @Test
    public void testExtractSectionActionsAction() {
        ActionRule result = parser.extractSectionActions("* /test/hello?who={value:aaaa*}&number={number} action:Test.hello");
        AssertJUnit.assertNotNull(result);
    }
    
}

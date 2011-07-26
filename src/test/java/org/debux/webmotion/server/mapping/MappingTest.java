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
public class MappingTest {

    private static final Logger log = LoggerFactory.getLogger(MappingTest.class);

    protected Mapping mapping;
    
    @BeforeMethod
    public void createMapping() {
        mapping = new Mapping();
    }
    
    @Test
    public void testExtractSectionErrorsException() {
        mapping.extractSectionErrors("java.lang.NullPointerException Error.error");
        AssertJUnit.assertEquals(1, mapping.getErrorRules().size());
    }
    
    @Test
    public void testExtractSectionErrorsCode() {
        mapping.extractSectionErrors("code:404 view.jsp:error.error");
        AssertJUnit.assertEquals(1, mapping.getErrorRules().size());
    }
    
    @Test
    public void testExtractSectionFilters() {
        mapping.extractSectionFilters("* /* Filters.log");
        AssertJUnit.assertEquals(1, mapping.getFilterRules().size());
    }
    
    @Test
    public void testExtractSectionActionsBasic() {
        mapping.extractSectionActions("* / Test.index");
        AssertJUnit.assertEquals(1, mapping.getActionRules().size());
    }
    
    @Test
    public void testExtractSectionActionsView() {
        mapping.extractSectionActions("* /admin view.jsp:sub.subaction.admin.index");
        AssertJUnit.assertEquals(1, mapping.getActionRules().size());
    }
    
    @Test
    public void testExtractSectionActionsUrl() {
        mapping.extractSectionActions("* /google url:http://www.google.fr");
        AssertJUnit.assertEquals(1, mapping.getActionRules().size());
    }
    
    @Test
    public void testExtractSectionActionsDynamic() {
        mapping.extractSectionActions("* /{sub}/{class}/{method}/{test} {sub}.{class}.{method}{test}");
        AssertJUnit.assertEquals(1, mapping.getActionRules().size());
    }
    
    @Test
    public void testExtractSectionActionsStatic() {
        mapping.extractSectionActions("* /static Test.hello value=coco,number=9");
        AssertJUnit.assertEquals(1, mapping.getActionRules().size());
    }
    
    @Test
    public void testExtractSectionActionsAction() {
        mapping.extractSectionActions("* /test/hello?who={value:aaaa*}&number={number} action:Test.hello");
        AssertJUnit.assertEquals(1, mapping.getActionRules().size());
    }
    
}

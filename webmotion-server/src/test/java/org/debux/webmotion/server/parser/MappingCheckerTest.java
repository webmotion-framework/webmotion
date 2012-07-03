/*
 * #%L
 * WebMotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.*;
import org.debux.webmotion.server.parser.MappingChecker.Warning;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test on check the mapping.
 * 
 * @author julien
 */
public class MappingCheckerTest {
    
    protected MappingChecker checker;
    protected Rule rule;
    
    @BeforeMethod
    public void setUp() {
        checker = new MappingChecker();
        rule = new Rule() {};
        rule.setAction(new Action());
    }
    
    @Test
    public void testIsVariable() {
        AssertJUnit.assertFalse(checker.isVariable(""));
        AssertJUnit.assertFalse(checker.isVariable("test"));
        AssertJUnit.assertFalse(checker.isVariable("\\{test"));
        AssertJUnit.assertFalse(checker.isVariable("\\}test"));
        
        AssertJUnit.assertTrue(checker.isVariable("{test}"));
        AssertJUnit.assertTrue(checker.isVariable("{test}{test}"));
        AssertJUnit.assertTrue(checker.isVariable("{test}test{test}"));
        AssertJUnit.assertTrue(checker.isVariable("test{test}test{test}"));
        AssertJUnit.assertTrue(checker.isVariable("{t{es}t}"));
    }
    
    @Test
    public void testAddWarning() {
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
        
        checker.addWarning(null, 0, "Message");
        warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testInvalidCheckClassName() {
        checker.checkClassName(rule, Object.class, "MappingCheckerTest");
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckClassName() {
        checker.checkClassName(rule, Object.class, "org.debux.webmotion.server.parser.MappingCheckerTest");
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    @Test
    public void testInvalidCheckMethodName() {
        checker.checkMethodName(rule, this.getClass(), "invalidCheckMethodName");
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckMethodName() {
        checker.checkMethodName(rule, this.getClass(), "testInvalidCheckMethodName");
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    @Test
    public void testInvalidCheckFile() {
        checker.checkFile(rule, "test.jsp");
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckFile() throws URISyntaxException {
        URL resource = this.getClass().getResource("/mapping/webmotion-test.mapping");
        File file = new File(resource.toURI());
        
        checker.checkFile(rule, file.getAbsolutePath());
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    @Test
    public void testInvalidCheckFragments() {
        FragmentUrl fragment = new FragmentUrl();
        fragment.setValue("invalid pattern");
        
        List<FragmentUrl> fragments = new ArrayList<FragmentUrl>();
        fragments.add(fragment);
        
        checker.checkFragments(rule, fragments);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckFragments() {
        FragmentUrl fragment = new FragmentUrl();
        fragment.setValue("test");
        fragment.setPattern(Pattern.compile("test"));
        
        List<FragmentUrl> fragments = new ArrayList<FragmentUrl>();
        fragments.add(fragment);
        
        checker.checkFragments(rule, fragments);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }

    @Test
    public void testInvalidCheckVariables() {
        rule.getAction().setFullName("{invalid}");
        FragmentUrl fragment = new FragmentUrl();
        fragment.setParam("test");
        
        List<FragmentUrl> fragments = new ArrayList<FragmentUrl>();
        fragments.add(fragment);
        
        checker.checkVariables(rule, fragments);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckVariables() {
        rule.getAction().setFullName("{test}");
        FragmentUrl fragment = new FragmentUrl();
        fragment.setParam("test");
        
        List<FragmentUrl> fragments = new ArrayList<FragmentUrl>();
        fragments.add(fragment);
        
        checker.checkVariables(rule, fragments);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    @Test
    public void testInvalidCheckError() {
        ErrorRule errorRule = new ErrorRule();
        errorRule.setError("java.lang.npe");

        checker.checkError(errorRule);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckError() {
        ErrorRule errorRule = new ErrorRule();
        errorRule.setError("java.lang.NullPointerException");
        checker.checkError(errorRule);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    @Test
    public void testCheckMapping() {
        ClassLoader classLoader = MappingParserTest.class.getClassLoader();
        URL resource = classLoader.getResource("mapping/webmotion-test.mapping");
        
        MappingParser parser = new MappingParser();
        Mapping mapping = parser.parse(resource);
        
        ServerContext context = new ServerContext();
        context.setGlobalControllers(new HashMap<String, Class<? extends WebMotionController>>());
        
        checker.checkMapping(context, mapping);
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertFalse(warnings.isEmpty());
        checker.print();
    }
    
    private static abstract class TestInvalidCheckModfiers4Class {}
    
    @Test
    public void testInvalidCheckModfiers4Class() {
        checker.checkModfiers(rule, TestInvalidCheckModfiers4Class.class);
        
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(3, warnings.size());
    }
    
    @Test
    public void testValidCheckModfiers4Class() {
        checker.checkModfiers(rule, MappingCheckerTest.class);
        
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    public class TestInvalidCheckModfiers4Method {
        public void valid() {}
        private void invalid() {}
    }
    
    @Test
    public void testInvalidCheckModfiers4Method() throws NoSuchMethodException {
        Method method = TestInvalidCheckModfiers4Method.class.getDeclaredMethod("invalid");
        checker.checkModfiers(rule, method);
        
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckModfiers4Method() throws NoSuchMethodException {
        Method method = TestInvalidCheckModfiers4Method.class.getDeclaredMethod("valid");
        checker.checkModfiers(rule, method);
        
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
    @Test
    public void testInvalidCheckSuperClass() throws NoSuchMethodException {
        checker.checkSuperClass(rule, WebMotionController.class, MappingCheckerTest.class);
        
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(1, warnings.size());
    }
    
    @Test
    public void testValidCheckSuperClass() throws NoSuchMethodException {
        checker.checkSuperClass(rule, WebMotionController.class, WebMotionFilter.class);
        
        List<Warning> warnings = checker.getWarnings();
        AssertJUnit.assertEquals(0, warnings.size());
    }
    
}

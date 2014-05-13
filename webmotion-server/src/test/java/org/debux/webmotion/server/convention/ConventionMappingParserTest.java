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
package org.debux.webmotion.server.convention;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.render.Render;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on utility.
 * 
 * @author julien
 */
public class ConventionMappingParserTest {

    private static final Logger log = LoggerFactory.getLogger(ConventionMappingParserTest.class);
    
    @Test
    public void testSuperClass() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(""))
            .setScanners(new SuperClassScanner()));
        
        Store store = reflections.getStore();
        Multimap<String, String> mmap = store.get(SuperClassScanner.class);
        
        String classConvention = WebMotionConventionController.class.getName();
        Multimap<String, String> classes = Multimaps.filterValues(mmap, Predicates.equalTo(classConvention));
        AssertJUnit.assertEquals(1, classes.size());
    }
    
    @Test
    public void testSplitCamelCase() {
        String name = "UserServiceTest";
        String[] result = StringUtils.splitByCharacterTypeCamelCase(name);
        AssertJUnit.assertEquals(3, result.length);
    }
        
    @Test
    public void testScan() {
        ConventionMappingScanner scanner = new ConventionMappingScanner();
        List<Mapping> mappings = scanner.scan();
        AssertJUnit.assertEquals(1, mappings.size());
    }
    
    public class UserService extends WebMotionConventionController {
        
        // GET       /user/service                UserService.get
        public Render get() {
            return null;
        }
        
        // POST       /user/service/add/friends   UserService.addFriends
        public Render addFriends() {
            return null;
        }
        
    }
    
    public class UserService2 extends UserService {
        
    }
    
}

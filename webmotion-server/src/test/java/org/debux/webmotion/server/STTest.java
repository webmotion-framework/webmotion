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
package org.debux.webmotion.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test on StringTempate.
 * 
 * @author julien
 */
public class STTest {
   
    @Test
    public void testGroup() {
        String g = "a(x) ::= <<\n"
                + "<x>\n"
                + ">>";
        
        STGroup group = new STGroupString(g);
        ST st = group.getInstanceOf("a");
        
        String expected = "foo";
        st.add("x", expected);
        
        String result = st.render();
        AssertJUnit.assertEquals(expected, result);
    }
    
    @Test
    public void testHtmlTemplate() {
        String g = "title(name) ::= <<\n"
                + "\\<h1\\><name>\\<h1\\>\n"
                + ">>";
        
        STGroup group = new STGroupString(g);
        ST st = group.getInstanceOf("title");
        
        st.add("name", "test");
        
        String result = st.render();
        AssertJUnit.assertEquals("<h1>test<h1>", result);
    }
    
    @Test void testStringTemplate() throws IOException {
        ST template = new ST("$model.key$", '$', '$');
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("key", "value");
        
        template.add("model", model);
        String render = template.render();
        AssertJUnit.assertEquals("value", render);
    }
    
}

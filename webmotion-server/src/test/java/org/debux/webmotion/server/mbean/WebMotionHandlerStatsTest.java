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
package org.debux.webmotion.server.mbean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * 
 * @author julien
 */
public class WebMotionHandlerStatsTest {

    private static final Logger log = LoggerFactory.getLogger(WebMotionHandlerStatsTest.class);
    
    @Test
    public void testGetHandlers() {
        WebMotionHandlerStats handlerStats = new WebMotionHandlerStats();
        handlerStats.registerHandlerTime("MyHandler", System.currentTimeMillis() - 1000);
        AssertJUnit.assertEquals(1, handlerStats.getHandlers().size());
        AssertJUnit.assertEquals(1, handlerStats.getHandlers().get("MyHandler").getRequestCount());
    }
    
    @Test
    public void testReset() {
        WebMotionHandlerStats handlerStats = new WebMotionHandlerStats();
        handlerStats.registerHandlerTime("MyHandler", System.currentTimeMillis() - 1000);
        handlerStats.reset();
        AssertJUnit.assertTrue(handlerStats.getHandlers().isEmpty());
    }
    
}

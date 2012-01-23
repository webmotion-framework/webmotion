/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server.mbean;

import java.util.Map;

/**
 * The MBean contains utilities to manage the server.
 * 
 * @author julien
 */
public interface ServerContextManagerMXBean {
    
    /**
     * Reset all stats.
     */
    void resetStats();
    
    /**
     * Reload the current mapping.
     */
    void reloadMapping();
    
    /**
     * @return attributes contains in server context.
     */
    Map<String, String> getAttibutes();
    
    /**
     * Disabled error page only the simple page is display.
     */
    void disabledErrorPage();
    
    /**
     * Enabled error page, if in mapping a rule is found, the page is not display.
     */
    void enabledErrorPage();
    
    /**
     * Force error page, even if in mapping a rule is found, the page is display.
     */
    void forceErrorPage();
    
}

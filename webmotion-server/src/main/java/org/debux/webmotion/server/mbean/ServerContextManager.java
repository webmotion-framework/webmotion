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

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionServerContext;

/**
 * Implements ServerContextManagerMXBean.
 * 
 * @author julien
 */
public class ServerContextManager implements ServerContextManagerMXBean {

    protected WebMotionServerContext serverContext;

    public ServerContextManager(WebMotionServerContext serverContext) {
        this.serverContext = serverContext;
    }
    
    /**
     * Register the MBean.
     */
    public void register() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=ServerContextManager");
            mBeanServer.registerMBean(this, name);
                        
        } catch (Exception ex) {
            throw new WebMotionException("Error during register the MBean", ex);
        }    
    }
    
    /**
     * Unregister the MBean.
     */
    public void unregister() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=ServerContextManager");
            mBeanServer.unregisterMBean(name);
            
        } catch (Exception ex) {
            throw new WebMotionException("Error during unregister the MBean", ex);
        }
    }
    
    @Override
    public void resetStats() {
        serverContext.getServerStats().reset();
        serverContext.getHandlerStats().reset();
    }

    @Override
    public void reloadMapping() {
        serverContext.loadMapping();
    }

    @Override
    public Map<String, String> getAttibutes() {
        Map<String, Object> attributes = serverContext.getAttributes();
        Map<String, String> map = new HashMap<String, String>(attributes.size());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            map.put(key, value);
        }
        return map;
    }
    
}

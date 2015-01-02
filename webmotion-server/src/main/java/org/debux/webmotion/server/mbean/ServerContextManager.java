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
package org.debux.webmotion.server.mbean;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Config.State;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.parser.MappingChecker;
import org.debux.webmotion.server.parser.MappingChecker.Warning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements ServerContextManagerMXBean.
 * 
 * @author julien
 */
public class ServerContextManager implements ServerContextManagerMXBean {

    private static final Logger log = LoggerFactory.getLogger(ServerContextManager.class);
    
    protected ServerContext serverContext;

    public ServerContextManager(ServerContext serverContext) {
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
            log.debug("Error during register the MBean", ex);
            log.warn("Error during register the MBean");
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
            log.debug("Error during register the MBean", ex);
            log.warn("Error during unregister the MBean");
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

    @Override
    public void disabledErrorPage() {
        Mapping mapping = serverContext.getMapping();
        Config config = mapping.getConfig();
        config.setErrorPage(State.DISABLED);
    }

    @Override
    public void enabledErrorPage() {
        Mapping mapping = serverContext.getMapping();
        Config config = mapping.getConfig();
        config.setErrorPage(State.ENABLED);
    }

    @Override
    public void forcedErrorPage() {
        Mapping mapping = serverContext.getMapping();
        Config config = mapping.getConfig();
        config.setErrorPage(State.FORCED);
    }

    @Override
    public String getErrorPageStatus() {
        Mapping mapping = serverContext.getMapping();
        Config config = mapping.getConfig();
        return config.getErrorPage().toString();
    }

    @Override
    public List<String> getWarnings() {
        MappingChecker mappingChecker = new MappingChecker();
        Mapping mapping = serverContext.getMapping();
        mappingChecker.checkMapping(serverContext, mapping);
        
        List<Warning> warnings = mappingChecker.getWarnings();
        List<String> result = new ArrayList<String>(warnings.size());
        for (Warning warning : warnings) {
            result.add(warning.toString());
        }
        
        return result;
    }
    
}

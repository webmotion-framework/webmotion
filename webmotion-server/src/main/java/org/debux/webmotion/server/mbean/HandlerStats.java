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

import java.beans.ConstructorProperties;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements HandlerStatsMXBean.
 * 
 * @author julien
 */
public class HandlerStats implements HandlerStatsMXBean {

    private static final Logger log = LoggerFactory.getLogger(HandlerStats.class);
    
    protected Map<String, HandlerData> handlers;
    
    /**
     * The class represents all stats for one handler.
     */
    public static class HandlerData {
        protected long requestCount;
        protected long requestTime;

        @ConstructorProperties({"requestCount", "requestTime"}) 
        public HandlerData(long requestCount, long requestTime) {
            this.requestCount = requestCount;
            this.requestTime = requestTime;
        }

        /**
         * @return number of request pass in handler.
         */
        public long getRequestCount() {
            return requestCount;
        }

        /**
         * @return total time passed in handler.
         */
        public long getRequestTime() {
            return requestTime;
        }
        
        /**
         * @return means time passed to execute the handler.
         */
        public long getMeansTime() {
            if (requestCount == 0) {
                return 0;
            }
            return requestTime / requestCount;
        }
    }
    
    /**
     * Default constructor.
     */
    public HandlerStats() {
        reset();
    }
        
    /**
     * Register the MBean.
     */
    public void register() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=HandlerStats");
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
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=HandlerStats");
            mBeanServer.unregisterMBean(name);
            
        } catch (Exception ex) {
            log.debug("Error during register the MBean", ex);
            log.warn("Error during unregister the MBean");
        }
    }
    
    /**
     * Store the information for one handler.
     * 
     * @param handlerName handler class name
     * @param start start when the call have been begin
     */
    public void registerHandlerTime(String handlerName, long start) {
        long end = System.currentTimeMillis();
        long time = end - start;
        
//        synchronized (handlers) {
            HandlerData handlerStats = handlers.get(handlerName);
            if (handlerStats == null) {
                handlerStats = new HandlerData(0, 0);
                handlers.put(handlerName, handlerStats);
            }
            
            handlerStats.requestCount ++;
            handlerStats.requestTime += time;
//        }
    }
    
    @Override
    public void reset() {
        handlers = Collections.synchronizedMap(new HashMap<String, HandlerData>());
    }

    @Override
    public Map<String, HandlerData> getHandlers() {
        return handlers;
    }
}

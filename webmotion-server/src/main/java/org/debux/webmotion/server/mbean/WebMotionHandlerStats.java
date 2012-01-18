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
import org.debux.webmotion.server.WebMotionException;

/**
 * Implements WebMotionHandlerStats.
 * 
 * @author julien
 */
public class WebMotionHandlerStats implements WebMotionHandlerStatsMXBean {

    protected Map<String, HandlerStats> handlers;
    
    public static class HandlerStats {
        protected long requestCount;
        protected long requestTime;

        @ConstructorProperties({"requestCount", "requestTime"}) 
        public HandlerStats(long requestCount, long requestTime) {
            this.requestCount = requestCount;
            this.requestTime = requestTime;
        }

        public long getRequestCount() {
            return requestCount;
        }

        public long getRequestTime() {
            return requestTime;
        }
        
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
    public WebMotionHandlerStats() {
        reset();
    }
        
    /**
     * Register the MBean.
     */
    public void register() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=WebMotionHandlerStats");
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
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=WebMotionHandlerStats");
            mBeanServer.unregisterMBean(name);
            
        } catch (Exception ex) {
            throw new WebMotionException("Error during unregister the MBean", ex);
        }
    }
    
    public void registerHandlerTime(String handlerName, long start) {
        long end = System.currentTimeMillis();
        long time = end - start;
        
//        synchronized (handlers) {
            HandlerStats handlerStats = handlers.get(handlerName);
            if (handlerStats == null) {
                handlerStats = new HandlerStats(0, 0);
                handlers.put(handlerName, handlerStats);
            }
            
            handlerStats.requestCount ++;
            handlerStats.requestTime += time;
//        }
    }
    
    @Override
    public void reset() {
        handlers = Collections.synchronizedMap(new HashMap<String, HandlerStats>());
    }

    @Override
    public Map<String, HandlerStats> getHandlers() {
        return handlers;
    }
}

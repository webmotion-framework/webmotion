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
import java.util.Collections;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.debux.webmotion.server.WebMotionUtils.LruCache;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements ServerStatsMXBean.
 * 
 * @author julien
 */
public class ServerStats implements ServerStatsMXBean {

    private static final Logger log = LoggerFactory.getLogger(ServerStats.class);
    
    protected int sizeLastRequest;
    protected Map<String, Long> lastRequests;
    
    protected long requestCount;
    protected long requestTime;
    protected long errorRequestCount;
    
    /**
     * Default constructor.
     */
    public ServerStats() {
        sizeLastRequest = 100;
        reset();
    }
    
    /**
     * Register the MBean.
     */
    public void register() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=ServerStats");
            mBeanServer.registerMBean(this, name);
                        
        } catch (Exception ex) {
            log.warn("Error during register the MBean");
        }    
    }
    
    /**
     * Unregister the MBean.
     */
    public void unregister() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.debux.webmotion.server:type=ServerStats");
            mBeanServer.unregisterMBean(name);
            
        } catch (Exception ex) {
            log.warn("Error during unregister the MBean");
        }
    }
    
    /**
     * Store the information from a call.
     * 
     * @param call call
     * @param start when the call have been begin
     */
    public void registerCallTime(Call call, long start) {
        long end = System.currentTimeMillis();
        long time = end - start;
        requestTime += time;
        
        requestCount ++;
        
        HttpContext context = call.getContext();
        if (context.isError()) {
            errorRequestCount ++;
        }
        
        String url = context.getUrl();
        lastRequests.put(url, time);
    }
    
    @Override
    public void reset() {
        requestCount = 0;
        requestTime = 0;
        errorRequestCount = 0;
        resetLastRequests();
    }
    
    @Override
    public void resetLastRequests() {
        lastRequests = Collections.synchronizedMap(new LruCache<String, Long>(sizeLastRequest));
    }
    
    @Override
    public void setSizeLastRequests(int size) {
        sizeLastRequest = size;
        resetLastRequests();
    }

    @Override
    public int getSizeLastRequests() {
        return sizeLastRequest;
    }
    
    @Override
    public long getRequestCount() {
        return requestCount;
    }

    @Override
    public long getRequestTime() {
        return requestTime;
    }
    
    @Override
    public long getRequestMeansTime() {
        if (requestCount == 0) {
            return 0;
        }
        return requestTime / requestCount;
    }

    @Override
    public long getErrorRequestCount() {
        return errorRequestCount;
    }

    @Override
    public Map<String, Long> getLastRequests() {
        return lastRequests;
    }
    
}

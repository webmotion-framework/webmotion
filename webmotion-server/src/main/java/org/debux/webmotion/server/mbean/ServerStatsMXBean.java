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

import java.util.Map;

/**
 * Interface MBean to give server informations.
 * 
 * @author julien
 */
public interface ServerStatsMXBean {

    /**
     * Reset all stats.
     */
    void reset();
    
    /**
     * Reset just last request map.
     */
    void resetLastRequests();
    
    /**
     * Set size last request by default is 100.
     * @param size size
     */
    void setSizeLastRequests(int size);
    
    /**
     * @return size of last request list.
     */
    int getSizeLastRequests();
    
    /**
     * @return number of executed request.
     */
    long getRequestCount();
    
    /**
     * @return total time passed on requests.
     */
    long getRequestTime();
    
    /**
     * @return means time passed to execute the requests.
     */
    long getRequestMeansTime();
    
    /**
     * @return last request is executed.
     */
    Map<String, Long> getLastRequests();
    
    /**
     * @return number of executed error request.
     */
    long getErrorRequestCount();
    
}

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

import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * 
 * @author julien
 */
public class ServerStatsTest {

    private static final Logger log = LoggerFactory.getLogger(ServerStatsTest.class);
    
    @Test
    public void testGetRequestCount() {
        ServerStats stats = new ServerStats();
        
        stats.registerCallTime(new Call() {
            @Override
            public HttpContext getContext() {
                return new HttpContext() {
                    @Override
                    public String getUrl() {
                        return "/test";
                    }
                    @Override
                    public boolean isError() {
                        return false;
                    }
                };
            }
        }, System.currentTimeMillis() - 1000);
        
        AssertJUnit.assertEquals(1, stats.getRequestCount());
    }
    
    @Test
    public void testGetErrorRequestCount() {
        ServerStats stats = new ServerStats();
        
        stats.registerCallTime(new Call() {
            @Override
            public HttpContext getContext() {
                return new HttpContext() {
                    @Override
                    public String getUrl() {
                        return "/error";
                    }
                    @Override
                    public boolean isError() {
                        return true;
                    }
                };
            }
        }, System.currentTimeMillis() - 1000);
        
        AssertJUnit.assertEquals(1, stats.getErrorRequestCount());
    }
    
    @Test
    public void testGetLastRequests() {
        ServerStats stats = new ServerStats();
        
        for (int index = 0; index < 10; index++) {
            final String url = "/test" + index;
            
            stats.registerCallTime(new Call() {
                @Override
                public HttpContext getContext() {
                    return new HttpContext() {
                        @Override
                        public String getUrl() {
                            return url;
                        }
                        @Override
                        public boolean isError() {
                            return false;
                        }
                    };
                }
            }, System.currentTimeMillis() - index * 100);
        }
        
        AssertJUnit.assertEquals(10, stats.getLastRequests().size());
    }
    
    @Test
    public void testReset() {
        ServerStats stats = new ServerStats();
        
        for (int index = 0; index < 10; index++) {
            final String url = "/test" + index;
            
            stats.registerCallTime(new Call() {
                @Override
                public HttpContext getContext() {
                    return new HttpContext() {
                        @Override
                        public String getUrl() {
                            return url;
                        }
                        @Override
                        public boolean isError() {
                            return false;
                        }
                    };
                }
            }, System.currentTimeMillis() - index * 100);
        }
        
        stats.reset();
        
        AssertJUnit.assertEquals(0, stats.getRequestCount());
        AssertJUnit.assertEquals(0, stats.getErrorRequestCount());
        AssertJUnit.assertEquals(0, stats.getRequestMeansTime());
        AssertJUnit.assertEquals(0, stats.getRequestTime());
        AssertJUnit.assertTrue(stats.getLastRequests().isEmpty());
    }
    
}

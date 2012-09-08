/*
 * #%L
 * WebMotion extra shiro
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
package org.debux.webmotion.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 *
 * @author julien
 */
public class ShiroListener implements WebMotionServerListener {

    @Override
    public void onStart(Mapping mapping, ServerContext context) {
        context.addGlobalController(Shiro.class);
        
        Realm realm = getRealm();
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        
        DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();
        sessionManager.setSessionDAO(getSessionDAO());
        
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Override
    public void onStop(ServerContext context) {
        // Do nothing
    }
    
    /**
     * @return basic realm in properties file on classpath
     */
    protected Realm getRealm() {
        PropertiesRealm realm = new PropertiesRealm();
        realm.setResourcePath("classpath:shiro.properties");
        realm.init();
        return realm;
    }
    
    /**
     * @return session dao to how store the session
     */
    protected SessionDAO getSessionDAO() {
        return new MemorySessionDAO();
    }
    
}

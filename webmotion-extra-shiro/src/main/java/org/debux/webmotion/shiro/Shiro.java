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

import java.net.HttpURLConnection;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julien
 */
public class Shiro extends WebMotionFilter {
    
    private static final Logger log = LoggerFactory.getLogger(Shiro.class);
    
    /**
     * Log the user by username and password.
     * 
     * @param username
     * @param password
     * @param rememberMe
     * @return 
     */
    public Render login(String username, String password, boolean rememberMe) {
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(rememberMe);

        try {
            currentUser.login(token);

        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
        }

        return renderJSON(currentUser.getPrincipal());
    }
    
    /**
     * Log out the current user.
     * 
     * @return 
     */
    public Render logout() {
        try {
            SecurityUtils.getSubject().logout();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return renderStatus(HttpURLConnection.HTTP_OK);
    }
    
    /**
     * Check if the user is authenticated. Try to connect user if found login 
     * information.
     * 
     * @param username
     * @param password
     * @param rememberMe
     * @return 
     */
    public Render isAuthenticated(String username, String password, Boolean rememberMe) {
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            if (username != null && !username.isEmpty()) {
                // Try to log the user
                UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                if (rememberMe != null) {
                    token.setRememberMe(rememberMe);
                }
                
                try {
                    currentUser.login(token);
                    doProcess();
                    return null;

                } catch (AuthenticationException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
    }
    
    /**
     * Check if the current user has role.
     * 
     * @param role
     * @return 
     */
    public Render hasRole(String role) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            if (currentUser.hasRole(role)) {
                doProcess();
            } else {
                return renderError(HttpURLConnection.HTTP_FORBIDDEN);
            }
        } else {
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        return null;
    }
    
    /**
     * Check if the current user is permitted.
     * 
     * @param permission
     * @return 
     */
    public Render isPermitted(String permission) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            if (currentUser.isPermitted(permission)) {
                doProcess();
            } else {
                return renderError(HttpURLConnection.HTTP_FORBIDDEN);
            }
        } else {
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        return null;
    }
    
}

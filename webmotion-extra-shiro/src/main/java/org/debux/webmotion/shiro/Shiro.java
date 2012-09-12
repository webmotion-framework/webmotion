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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shiro is a global controller to manage authentification and authorization.
 * 
 * @author julien
 */
public class Shiro extends WebMotionFilter {
    
    private static final Logger log = LoggerFactory.getLogger(Shiro.class);
    
    /**
     * Get the subject in http context. Must use WebSujet.Builder for Glassfish.
     * @param request
     * @param response
     * @return 
     */
    public Subject getSubject(HttpContext context) {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        Subject subject = new WebSubject.Builder(request, response).buildWebSubject();
        return subject;
    }
    
    /**
     * Log the user by username and password.
     * 
     * @param username
     * @param password
     * @param rememberMe
     * @return 
     */
    public Render login(HttpContext context, String username, String password, Boolean rememberMe, String redirect) {
        Subject currentUser = getSubject(context);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        if (rememberMe != null) {
            token.setRememberMe(rememberMe);
        }

        try {
            currentUser.login(token);

        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            throw e;
        }

        if (redirect != null && !redirect.isEmpty()) {
            return renderURL(redirect);
            
        } else {
            return renderJSON(currentUser.getPrincipal());
        }
    }
    
    /**
     * Log out the current user.
     * 
     * @return 
     */
    public Render logout(HttpContext context, String redirect) {
        try {
            Subject currentUser = getSubject(context);
            currentUser.logout();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        if (redirect != null && !redirect.isEmpty()) {
            return renderURL(redirect);
            
        } else {
            return renderSuccess();
        }
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
    public Render isAuthenticated(HttpContext context, String username, String password, Boolean rememberMe) {
        Subject currentUser = getSubject(context);
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
                    throw e;
                }
            }
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        
        doProcess();
        return null;
    }
    
    /**
     * Check if the current user has role.
     * 
     * @param role
     * @return 
     */
    public Render hasRole(HttpContext context, Call call) {
        FilterRule rule = (FilterRule) call.getCurrentRule();
        Map<String, String[]> defaultParameters = rule.getDefaultParameters();
        
        String[] values = defaultParameters.get("role");
        List<String> roles = Arrays.asList(values);
        
        Subject currentUser = getSubject(context);
        if (currentUser.isAuthenticated()) {
            
            boolean[] hasRoles = currentUser.hasRoles(roles);
            if (BooleanUtils.and(hasRoles)) {
                doProcess();
                return null;
            } else {
                return renderError(HttpURLConnection.HTTP_FORBIDDEN);
            }
            
        } else {
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }
    
    /**
     * Check if the current user is permitted.
     * 
     * @param permission
     * @return 
     */
    public Render isPermitted(HttpContext context, Call call) {
        FilterRule rule = (FilterRule) call.getCurrentRule();
        Map<String, String[]> defaultParameters = rule.getDefaultParameters();
        
        String[] permissions = defaultParameters.get("permission");
        
        Subject currentUser = getSubject(context);
        if (currentUser.isAuthenticated()) {
            
            boolean[] permitted = currentUser.isPermitted(permissions);
            if (BooleanUtils.and(permitted)) {
                doProcess();
                return null;
            } else {
                return renderError(HttpURLConnection.HTTP_FORBIDDEN);
            }
            
        } else {
            return renderError(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }
    
}

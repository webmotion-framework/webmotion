/*
 * #%L
 * Webmotion website
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
package org.debux.webmotion.wiki;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.wiki.service.WikiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter to manage authentification
 * 
 * @author julien
 */
public class Security extends WebMotionFilter {

    private static final Logger log = LoggerFactory.getLogger(Security.class);
    
    public static final String CURRENT_USER_ATTRIBUTE = "current_user";
    public static final String NO_SECURE = "no-secure";
            
    protected List<User> users;
    protected Map<String, String> permissions;

    public static class User {
        protected String name;
        protected String password;
        protected List<String> roles;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
    
    public Security() throws IOException, URISyntaxException {
        String usersPath = WikiConfig.instance.getUsersPath();
        InputStream stream = new FileInputStream(usersPath);
        String json = IOUtils.toString(stream);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<User>>() {}.getType();
        
        users = gson.fromJson(json, listType);
        
        permissions = new HashMap<String, String>();
        
        permissions.put("login", NO_SECURE);
        permissions.put("logout", NO_SECURE);
        permissions.put("language", NO_SECURE);
        
        permissions.put(null, "read");
        permissions.put("include", "read");
        permissions.put("sitemap", "read");
        permissions.put("mediamap", "read");
        permissions.put("first", "read");
        
        permissions.put("source", "write");
        permissions.put("delete", "write");
        permissions.put("create", "write");
        permissions.put("save", "write");
        permissions.put("preview", "write");
        permissions.put("edit", "write");
        permissions.put("attach", "write");
        permissions.put("upload", "write");
    }
    
    public Render login(String username, String password) throws Exception {
        for (User user : users) {
            String name = user.getName();
            
            if (name.equals(username)) {
                
                String encoded = DigestUtils.shaHex(password);
                log.debug("encoded = " + encoded);
                if (encoded.equals(user.getPassword())) {
                    
                    HttpContext context = getContext();
                    HttpSession session = context.getSession();
                    session.setAttribute(CURRENT_USER_ATTRIBUTE, user);
                    
                    return renderURL("/");
                            
                } else {
                    return renderURL("/login",
                            "error.login", "password");
                }
            }
        }
        return renderURL("/login",
                "error.login", "username");
    }
    
    public Render logout(String login, String password) throws Exception {
        HttpContext context = getContext();
        HttpSession session = context.getSession();
        session.removeAttribute(CURRENT_USER_ATTRIBUTE);
        return renderLastPage();
    }
    
    public Render check(String action, String sub) throws Exception {
        String[] publicPermissions = WikiConfig.instance.getPublicPermissions();
        List<String> currentPermissions = Arrays.asList(publicPermissions);
        
        HttpContext context = getContext();
        HttpSession session = context.getSession();
        User user = (User) session.getAttribute(CURRENT_USER_ATTRIBUTE);
        if (user != null) {
            List<String> roles = user.getRoles();
            currentPermissions.addAll(roles);
        }
        
        String required;
        if (action != null) {
            required = permissions.get(action);
        } else {
            required = permissions.get(sub);
        }

        log.debug("required = " + required + " in " + currentPermissions);
        if (required != null && (required.equals(NO_SECURE)
            || currentPermissions.contains(required))) {
            
            doProcess();
            return null;
            
        } else {
            return renderURL("/login");
        }
    }
    
}
 
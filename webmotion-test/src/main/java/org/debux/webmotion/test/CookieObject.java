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
package org.debux.webmotion.test;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.CookieManger;
import org.debux.webmotion.server.call.CookieManger.CookieEntity;
import org.debux.webmotion.server.render.Render;

/**
 * Use cookie with object value.
 * 
 * @author julien
 */
public class CookieObject extends WebMotionController {
    
    public Render read(CookieManger manger) {
        CookieEntity cookie = manger.get("user_cookie");
        if (cookie != null) {
            UserCookie userCookie = cookie.getValue(UserCookie.class);
            String value = userCookie.getValue();
            return renderContent("<div>Current value = " + value + "</div>", "text/html");
        } else {
            return renderContent("<div>Current value is empty</div>", "text/html");
        }
    }
    
    public Render create(CookieManger manger, String value) {
        CookieEntity cookie = manger.create("user_cookie", new UserCookie(value));
        cookie.setPath("/cookie/object");
        manger.add(cookie);
        
        return renderURL("/cookie/object/read");
    }
    
    public Render remove(CookieManger manger) {
        manger.remove("user_cookie");
        
        return renderURL("/cookie/object/read");
    }
}

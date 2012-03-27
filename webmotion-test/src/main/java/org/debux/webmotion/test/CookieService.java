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
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;

/**
 * Use cookie
 * 
 * @author julien
 */
public class CookieService extends WebMotionController {
    
    public Render create(HttpContext context) {
        CookieManger manger = context.getCookieManger("me", true, true);
        CookieEntity cookie = manger.create("a_name", "a_value");
        cookie.setPath("/cookie");
        manger.add(cookie);
        
        return renderURL("/cookie/read");
        
    }
    
    public Render read(HttpContext context) {
        CookieManger basicManger = context.getCookieManger();
        CookieEntity secureCookie = basicManger.get("a_name");
        String secureValue = secureCookie.getValue();
        
        CookieManger manger = context.getCookieManger(null, true, true);
        CookieEntity cookie = manger.get("a_name");
        String value = cookie.getValue();
        
        return renderContent("<div>secure value = " + secureValue + "</div>" +
                "<div>value = " + value + "</div>", "text/html");
    }
    
}

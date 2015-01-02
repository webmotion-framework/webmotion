/*
 * #%L
 * Webmotion website
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
package org.debux.webmotion.test;

import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.CookieManager;
import org.debux.webmotion.server.call.CookieManager.CookieEntity;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;

/**
 * Use cookie
 * 
 * @author julien
 */
public class CookieService extends WebMotionController {
    
    public Render create(HttpContext context, boolean secured) {
        String name = secured ? "secured_name" : "name";
        
        CookieManager manager = getCookieManager(context, secured);
        CookieEntity cookie = manager.create(name, "a_value");
        cookie.setPath("/cookie");
        manager.add(cookie);
        
        return renderURL("/cookie/read", "secured", secured);
    }
    
    public Render read(HttpContext context, boolean secured) {
        String name = secured ? "secured_name" : "name";
        
        CookieManager manager = getCookieManager(context, secured);
        CookieEntity cookie = manager.get(name);
        String value = cookie.getValue();
        
        return renderContent("<div>Value = " + value + "</div>", "text/html");
    }
    
    protected CookieManager getCookieManager(HttpContext context, boolean secured) {
        if (secured) {
            return context.getCookieManager("me", true, true);
        } else {
            return context.getCookieManager();
        }
    }
}

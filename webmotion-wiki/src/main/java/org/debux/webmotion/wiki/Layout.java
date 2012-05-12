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

import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.wiki.service.WikiConfig;

/**
 * Filter to manage layout
 * 
 * @author julien
 */
public class Layout extends WebMotionFilter {
    
    public Render put(String sub, String name) throws Exception {
        HttpContext context = getContext();
        HttpServletRequest request = context.getRequest();
        HttpSession session = context.getSession();
        
        String url = (String) request.getAttribute("layout_url");
        if(url == null && sub == null && name == null) {
            
            url = context.getUrl();
            String siteName = WikiConfig.instance.getSiteName();
            String[] languages = WikiConfig.instance.getSupportedLanguage();
            
            // Check current language
            String language = (String) Config.get(session, Config.FMT_LOCALE);
            if(language == null) {
                Locale locale = request.getLocale();
                String lang = locale.getLanguage();
                
                language = WikiConfig.instance.getDefaultLanguage();
                Config.set(session, Config.FMT_LOCALE, language);
                
                for (String supported : languages) {
                    if (lang.equals(supported)) {
                        Config.set(session, Config.FMT_LOCALE, lang);
                        break;
                    }
                }
            }
            
            return renderView("layout.jsp",
                    "layout_url" , url,
                    "site_name", siteName,
                    "languages", languages,
                    "language", language);
        } else {
            doProcess();
            return null;
        }
    }
    
    public Render language(String language) throws Exception {
        HttpContext context = getContext();
        HttpSession session = context.getSession();
        
        Config.set(session, Config.FMT_LOCALE, language);
        
        return renderLastPage();
    }
}

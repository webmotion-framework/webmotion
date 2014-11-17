package org.debux.webmotion.site;

/*
 * #%L
 * WebMotion website
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Debux
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

import java.io.File;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;

/**
 *
 * @author julien
 */
public class Page extends WebMotionController {

    public Render display(String lang, String folder, String name) {
        HttpContext context = getContext();
        HttpServletRequest request = context.getRequest();
        HttpSession session = context.getSession();
        
        String language = (String) Config.get(session, Config.FMT_LOCALE);
        if (lang != null) {
            Config.set(session, Config.FMT_LOCALE, lang);
            return renderLastPage("language", lang);
            
        } else if (language == null) {
            Locale locale = request.getLocale();
            String reqLang = locale.getLanguage();
            if (reqLang == null) {
                reqLang = "en";
            }
            Config.set(session, Config.FMT_LOCALE, reqLang);
            language = reqLang;
        }
        
        String file = name +"_" + language + ".html";
        if (folder != null) {
            file = folder + File.separator + file;
        }
        return renderView(file, "language", language);
    }
    
}

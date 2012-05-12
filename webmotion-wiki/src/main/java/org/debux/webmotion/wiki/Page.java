/*
 * #%L
 * Webmotion in test
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

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.wiki.service.WikiConfig;
import org.debux.webmotion.wiki.service.WikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Page extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Page.class);

    protected WikiService service;

    public Page() {
        this(new WikiService());
    }
    
    public Page(WikiService service) {
        this.service = service;
    }
    
    protected String getLanguage() {
        HttpContext context = getContext();
        HttpSession session = context.getSession();
        
        String language = (String) Config.get(session, Config.FMT_LOCALE);
        return language;
    }
    
    public Render firstPage() throws Exception {
        String firstPage = WikiConfig.instance.getFirstPage();
        return renderURL(firstPage);
    }
    
    public Render display(String nameSpace, String pageName) throws Exception {
        String url = null;
        if (nameSpace != null) {
            url = "/" + nameSpace + "/" + pageName;
        } else {
            url = "/" + pageName;
        }
                
        File page = service.findPage(nameSpace, pageName, getLanguage());
        if (page != null) {
            return renderView("page/display.jsp",
                    "nameSpace", nameSpace,
                    "pageName", pageName,
                    "url", url);
        } else {
            return renderView("page/create.jsp",
                    "nameSpace", nameSpace,
                    "pageName", pageName,
                    "url", url);
        }
    }
    
    public Render include(String nameSpace, String pageName) throws Exception {
        String content = service.evalContent(nameSpace, pageName, getLanguage());
        if (content != null) {
            return renderContent(content, "text/html");
        }
        
        return null;
    }

    public Render edit(String nameSpace, String pageName) throws Exception {
        String url = null;
        if (nameSpace != null) {
            url = "/" + nameSpace + "/" + pageName;
        } else {
            url = "/" + pageName;
        }
        
        String type = service.getType(nameSpace, pageName, getLanguage());
        
        return renderView("page/edit.jsp", 
                            "nameSpace", nameSpace,
                            "pageName", pageName,
                            "type", type,
                            "url", url);
    }

    public Render source(String nameSpace, String pageName) throws Exception {
        String content = service.getContent(nameSpace, pageName, getLanguage());
        
        if (content != null) {
            return renderContent(content, "text/plain");
        } else {
            return renderContent("", "text/plain");
        }
    }

    public Render preview(String type, String content) throws Exception {
        String generated = service.generate(type, content);
        return renderContent(generated, "text/html");
    }

    public Render create(String nameSpace, String pageName, String type) throws Exception {
        service.createPage(nameSpace, pageName, getLanguage(), type);
        if (nameSpace == null) {
            return renderURL("/" + pageName,
                    "action" , "edit");
        } else {
            return renderURL("/" + nameSpace + "/" + pageName,
                    "action" , "edit");
        }
    }
    
    public Render save(String nameSpace, String pageName, String content) throws Exception {
        service.save(nameSpace, pageName, getLanguage(), content);
        if (nameSpace == null) {
            return renderURL("/" + pageName);
        } else {
            return renderURL("/" + nameSpace + "/" + pageName);
        }
    }
    
    public Render map(String nameSpace) throws Exception {
        String url = "";
        if (nameSpace != null) {
            url = "/" + nameSpace;
        }
        
        Map<String, List<String>> map = service.getSiteMap(nameSpace);
        return renderView("page/map.jsp",
                "map", map,
                "url", url);
    }
}

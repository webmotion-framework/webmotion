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

import org.debux.webmotion.server.WebMotionAction;
import org.debux.webmotion.server.call.Render;
import org.debux.webmotion.wiki.service.WikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Wiki extends WebMotionAction {

    private static final Logger log = LoggerFactory.getLogger(Wiki.class);

    protected WikiService service;

    public Wiki() {
        this(new WikiService());
    }
    
    public Wiki(WikiService service) {
        this.service = service;
    }
    
    public Render display(String nameSpace, String pageName) {
        String url = "/deploy/include/";
        if(nameSpace != null) {
            url += nameSpace + "/" + pageName;
        } else {
            url += pageName;
        }
        
        return renderView("wiki.jsp", "url", url);
    }

    public Render include(String nameSpace, String pageName) throws Exception {
        String content = service.evalContent(nameSpace, pageName);
        if(content != null) {
            return renderContent(content, "text/html");
        } else {
            return edit(nameSpace, pageName);
        }
    }
    
    public Render edit(String nameSpace, String pageName) throws Exception {
        String url = "/deploy/content/";
        if(nameSpace != null) {
            url += nameSpace + "/" + pageName;
        } else {
            url += pageName;
        }
        
        
        String type = service.getType(nameSpace, pageName);
        
        return renderView("edit.jsp", 
                            "url", url,
                            "nameSpace", nameSpace,
                            "pageName", pageName,
                            "type", type);
    }

    public Render content(String nameSpace, String pageName) throws Exception {
        String content = service.getContent(nameSpace, pageName);
        
        if(content != null) {
            return renderContent(content, "text/plain");
        } else {
            return renderContent("", "text/plain");
        }
    }
    
    public Render save(String nameSpace, String pageName, String type, String content) throws Exception {
        service.save(nameSpace, pageName, type, content);
        if(nameSpace == null) {
            return renderAction("display/" + pageName);
        } else {
            return renderAction("display/" + nameSpace + "/" + pageName);
        }
    }
    
}

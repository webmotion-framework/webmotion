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

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.debux.webmotion.server.WebMotionAction;
import org.debux.webmotion.server.call.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Wiki extends WebMotionAction {

    private static final Logger log = LoggerFactory.getLogger(Wiki.class);

    public Render main(String nameSpace, String pageName) {
        log.info("name space = " + nameSpace + ", page name = " + pageName);
        String url = pageName;
        if(nameSpace != null) {
            url = nameSpace + "/" + url;
        }
        
        return renderView("wiki.jsp", 
                "url", url);
    }

    public Render display(String nameSpace, String pageName) throws IOException {
        log.info("name space = " + nameSpace + ", page name = " + pageName);
        String path = "/workspace/projets/debux/webmotion/src/wikimotion/src/main/webapp/data";
        if(nameSpace != null) {
            path += "/" + nameSpace;
        }
        path += "/" + pageName + ".html";
        FileInputStream file = new FileInputStream(path);
        String content = IOUtils.toString(file);
        return renderContent(content, "text/html");
    }
    
}

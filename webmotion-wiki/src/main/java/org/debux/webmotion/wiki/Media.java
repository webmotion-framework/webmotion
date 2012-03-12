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
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.call.UploadFile;
import org.debux.webmotion.wiki.service.WikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Media extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Media.class);

    protected WikiService service;

    public Media() {
        this(new WikiService());
    }
    
    public Media(WikiService service) {
        this.service = service;
    }
    
    public Render get(String nameSpace, String mediaName) throws Exception {
        File file = service.getMedia(nameSpace, mediaName);
        if(file.exists()) {
            return renderStream(new FileInputStream(file), null);
        }
        
        return null;
    }
    
    public Render attach(String nameSpace) throws Exception {
        String url = "/media";
        if(nameSpace != null) {
            url +=  "/" + nameSpace;
        }
        
        return renderView("media/attach.jsp",
                "url", url);
    }
    
    public Render upload(String nameSpace, UploadFile file) throws Exception {
        String name = file.getName();
        File content = file.getFile();
        service.uploadMedia(nameSpace, name, content);
        
        if(nameSpace == null) {
            return renderURL("/media",
                    "name", name);
            
        } else {
            return renderURL("/media/" + nameSpace,
                    "name", name);
        }
    }
    
    public Render map(String nameSpace) throws Exception {
        String url = "/media";
        if(nameSpace != null) {
            url += "/" + nameSpace;
        }
                
        Map<String, List<String>> map = service.getMediaMap(nameSpace);
        return renderView("media/map.jsp",
                "map", map,
                "url", url);
    }
    
}

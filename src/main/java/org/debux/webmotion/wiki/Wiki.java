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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URI;
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

    public Render display(String nameSpace, String pageName) {
        log.info("name space = " + nameSpace + ", page name = " + pageName);
        
        String url = "/deploy/include/";
        if(nameSpace != null) {
            url += nameSpace + "/" + pageName;
        } else {
            url += pageName;
        }
        
        return renderView("wiki.jsp", "url", url);
    }

    public Render include(String nameSpace, String pageName) throws Exception {
        File file = getFilePage(nameSpace, pageName);
        if(file != null) {
            String content = generate(file);
            return renderContent(content, "text/html");
            
        } else {
            return edit(nameSpace, pageName);
        }
    }
    
    protected String generate(File page) throws Exception {
        String pageName = page.getName();
        int lastIndexOf = pageName.lastIndexOf('.') + 1;
        String extension = pageName.substring(lastIndexOf);
        
        Generator generator = Generator.valueOf(extension.toUpperCase());
        
        String generated = generator.generate(page);
        return generated;
    }
    
    public Render edit(String nameSpace, String pageName) {
        log.info("name space = " + nameSpace + ", page name = " + pageName);
        
        String url = "/deploy/content/";
        if(nameSpace != null) {
            url += nameSpace + "/" + pageName;
        } else {
            url += pageName;
        }
        
        return renderView("edit.jsp", 
                            "url", url,
                            "nameSpace", nameSpace,
                            "pageName", pageName);
    }

    public Render content(String nameSpace, String pageName) throws Exception {
        File file = getFilePage(nameSpace, pageName);
        if(file != null) {
            String content = IOUtils.toString(new FileInputStream(file));
            return renderContent(content, "text/html");
            
        } else {
            return renderContent("", "text/html");
        }
    }
    
    public Render save(String nameSpace, String pageName, String content) throws Exception {
        File file = getFilePage(nameSpace, pageName);
        if(!file.exists()) {
            if(nameSpace != null && !nameSpace.isEmpty()) {
                file.getParentFile().mkdir();
            }
            file.createNewFile();
        }
        IOUtils.write(content, new FileOutputStream(file));
        
        return renderAction("display/" + nameSpace + "/" + pageName);
    }
    
    public File getFilePage(String nameSpace, final String pageName) throws Exception {
        log.info("name space = " + nameSpace + ", page name = " + pageName);
        String path = "../data";
        if(nameSpace != null) {
            path += "/" + nameSpace;
        }
        URI resource = getClass().getClassLoader().getResource(path).toURI();
        
        File directory = new File(resource);
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                int lastIndexOf = name.lastIndexOf('.');
                if(lastIndexOf != -1) {
                    String value = name.substring(0, lastIndexOf);
                    return value.equals(pageName);
                }                    
                return false;
            }
        });
        
        if(files.length == 1) {
            File page = files[0];
            return page;
        }
        
        return null;
    }
}

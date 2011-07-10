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
package org.debux.webmotion.wiki.service;

import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class WikiService {

    private static final Logger log = LoggerFactory.getLogger(WikiService.class);

    public String evalContent(String nameSpace, String pageName) throws Exception {
        File file = findPage(nameSpace, pageName);
        if(file != null) {
            String content = generate(file);
            return content;
        }
        
        return null;
    }
    
    public String generate(File page) throws Exception {
        String pageName = page.getName();
        int lastIndexOf = pageName.lastIndexOf('.') + 1;
        String type = pageName.substring(lastIndexOf);
        
        Generator generator = Generator.valueOf(type.toUpperCase());
        String generated = generator.generate(page);
        return generated;
    }
    
    public String generate(String type, String content) throws Exception {
        Generator generator = Generator.valueOf(type.toUpperCase());
        String generated = generator.generate(content);
        return generated;
    }
    
    public String getContent(String nameSpace, String pageName) throws Exception {
        File file = findPage(nameSpace, pageName);
        if(file != null) {
            String content = IOUtils.toString(new FileInputStream(file));
            return content;
        }
        
        return null;
    }
    
    public void save(String nameSpace, String pageName, String type, String content) throws Exception {
        File file = findPage(nameSpace, pageName);
        if(file == null) {
            file = createPage(nameSpace, pageName, type);
        }
        
        IOUtils.write(content, new FileOutputStream(file));
    }
    
    public File createPage(String nameSpace, String pageName, String type) throws Exception {
        File file = getPage(nameSpace, pageName, type);
        if(!file.exists()) {
            file.getParentFile().mkdir();
            file.createNewFile();
        }
        return file;
    }
    
    public File getPage(String nameSpace, String pageName, String type) throws Exception {
        String path = getPagePath();
        URI resource = getClass().getClassLoader().getResource(path).toURI();
        File data = new File(resource);
        
        String name = pageName + "." + type;
        if(nameSpace != null) {
            name = nameSpace + "/" + name;
        }

        File page = new File(data, name);
        return page;
    }
    
    public String getType(String nameSpace, String pageName) throws Exception {
        File file = findPage(nameSpace, pageName);
        if(file != null) {
            String fileName = file.getName();
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            return type;
        }
        return "";
    }
    
    public File findPage(String nameSpace, final String pageName) throws Exception {
        String path = getPagePath();
        if(nameSpace != null) {
            path += "/" + nameSpace;
        }
        URL url = getClass().getClassLoader().getResource(path);
        if(url == null) {
            return null;
        }
        
        URI uri = url.toURI();
        File directory = new File(uri);
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String value = removeExtension(name);
                return value.equals(pageName);
            }
        });
        
        if(files.length == 1) {
            File page = files[0];
            return page;
        }
        
        return null;
    }

    protected String getPagePath() {
        String path = "../data/page";
        return path;
    }

    protected String getMediaPath() {
        String path = "../data/media";
        return path;
    }

    public void uploadMedia(String nameSpace, String mediaName, File file) throws Exception {
        File upload = getMedia(nameSpace, mediaName);
        upload.getParentFile().mkdir();
        upload.createNewFile();

        IOUtils.copy(new FileInputStream(file), new FileOutputStream(upload));
    }
    
    public File getMedia(String nameSpace, String mediaName) throws Exception {
        String path = getMediaPath();
        URI resource = getClass().getClassLoader().getResource(path).toURI();
        File data = new File(resource);
        
        String name = mediaName;
        if(nameSpace != null) {
            name = nameSpace + "/" + name;
        }

        File media = new File(data, name);
        return media;
    }
    
    public Map<String, List<String>> getSiteMap() throws Exception {
        return getMap(getPagePath(), true);
    }
    
    public Map<String, List<String>> getMediaMap() throws Exception {
        return getMap(getMediaPath(), false);
    }
    
    protected Map<String, List<String>> getMap(String path, boolean removeExtension) throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        ArrayList<String> filesWithoutDirectory = new ArrayList<String>();
        result.put(null, filesWithoutDirectory);
        
        URL url = getClass().getClassLoader().getResource(path);
        URI uri = url.toURI();
        
        File pages = new File(uri);
        File[] files = pages.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            
            if(file.isFile()) {
                if(removeExtension) {
                    fileName = removeExtension(fileName);
                }
                filesWithoutDirectory.add(fileName);
                
            } else {
                String[] list = file.list();
                ArrayList<String> filesWithDirectory = new ArrayList<String>(list.length);
                for (String pageName : list) {
                    if(removeExtension) {
                        pageName = removeExtension(pageName);
                    }
                    filesWithDirectory.add(pageName);
                }
                result.put(fileName, filesWithDirectory);
            }
        }
        
        return result;
    }
    
    protected String removeExtension(String name) {
        String value = name;
        int lastIndexOf = name.lastIndexOf('.');
        if(lastIndexOf != -1) {
            value = name.substring(0, lastIndexOf);
        }
        return value;
    }
    
}

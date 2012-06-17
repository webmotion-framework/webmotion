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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class WikiService {

    private static final Logger log = LoggerFactory.getLogger(WikiService.class);

    public static class Reference {
        protected String nameSpace;
        protected String pageName;
        protected String lang;

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getNameSpace() {
            return nameSpace;
        }

        public void setNameSpace(String nameSpace) {
            this.nameSpace = nameSpace;
        }

        public String getPageName() {
            return pageName;
        }

        public void setPageName(String pageName) {
            this.pageName = pageName;
        }
    }
    
    public String evalContent(String nameSpace, String pageName, String lang) throws Exception {
        File file = findPage(nameSpace, pageName, lang);
        if (file != null) {
            String content = generate(file);
            return content;
        }
        
        return null;
    }
    
    protected String generate(File page) throws Exception {
        String pageName = page.getName();
        int lastIndexOf = pageName.lastIndexOf('.') + 1;
        String type = pageName.substring(lastIndexOf);
        
        WikiGenerator generator = WikiGenerator.valueOf(type.toUpperCase());
        String generated = generator.generate(page);
        return generated;
    }
    
    public String generate(String type, String content) throws Exception {
        WikiGenerator generator = WikiGenerator.valueOf(type.toUpperCase());
        String generated = generator.generate(content);
        return generated;
    }
    
    public String getContent(String nameSpace, String pageName, String lang) throws Exception {
        File file = findPage(nameSpace, pageName, lang);
        if (file != null) {
            String content = IOUtils.toString(new FileInputStream(file));
            return content;
        }
        
        return null;
    }
    
    public void save(String nameSpace, String pageName, String lang, String content) throws Exception {
        File file = findPage(nameSpace, pageName, lang);
        if (file != null) {
            IOUtils.write(content, new FileOutputStream(file));
        }
    }
    
    public File createPage(String nameSpace, String pageName, String lang, String type) throws Exception {
        File file = getPage(nameSpace, pageName, lang, type);
        if (!file.exists()) {
            file.getParentFile().mkdir();
            file.createNewFile();
        }
        return file;
    }
    
    public String getType(String nameSpace, String pageName, String lang) throws Exception {
        File file = findPage(nameSpace, pageName, lang);
        if (file != null) {
            String fileName = file.getName();
            String type = FilenameUtils.getExtension(fileName);
            return type;
        }
        return "";
    }
    
    protected File getPage(String nameSpace, String pageName, String lang, String type) throws Exception {
        String path = getPagePath();
        if (nameSpace != null) {
            path += File.separator + nameSpace;
        }
        
        String name = pageName;
        String[] language = WikiConfig.instance.getSupportedLanguage();
        if (language != null && language.length != 0 && lang != null) {
            name += "_" + lang;
        }

        name += "." + type;
        
        File page = new File(path, name);
        return page;
    }
    
    public File findPage(String nameSpace, String pageName, String lang) throws Exception {
        String path = getPagePath();
        if (nameSpace != null) {
            path += File.separator + nameSpace;
        }
        
        String prefix = pageName;
        String[] language = WikiConfig.instance.getSupportedLanguage();
        if (language != null && language.length != 0 && lang != null) {
            prefix += "_" + lang;
        }
        
        File directory = new File(path);
        log.debug("file search : " + directory.getAbsolutePath() + File.separator + prefix);
        File[] files = directory.listFiles((FilenameFilter) new PrefixFileFilter(prefix + "."));
        log.debug("result search : " + Arrays.toString(files));
        
        if (files != null && files.length >= 1) {
            File page = files[0];
            return page;
            
        } else if (lang != null) {
            return findPage(nameSpace, pageName, null);
        }
        
        return null;
    }

    protected String getPagePath() throws URISyntaxException {
        String filePath = WikiConfig.instance.getFilePath();
        String path = filePath + File.separator;
        return path;
    }

    protected String getMediaPath() throws URISyntaxException {
        String filePath = WikiConfig.instance.getMediaPath();
        String path = filePath + File.separator;
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
        if (nameSpace != null) {
            path += nameSpace + File.separator;
        }
        path += mediaName;
        
        File media = new File(path);
        return media;
    }
    
    public Map<String, List<String>> getSiteMap(String nameSpace) throws Exception {
        String pagePath = getPagePath();
        if (nameSpace != null) {
            pagePath += nameSpace;
        }
        return getMap(pagePath, true);
    }
    
    public Map<String, List<String>> getMediaMap(String nameSpace) throws Exception {
        String mediaPath = getMediaPath();
        if (nameSpace != null) {
            mediaPath += nameSpace;
        }
        return getMap(mediaPath, false);
    }
    
    protected Map<String, List<String>> getMap(String path, boolean removeExtension) throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        ArrayList<String> filesWithoutDirectory = new ArrayList<String>();
        result.put(null, filesWithoutDirectory);
        
        File pages = new File(path);
        File[] files = pages.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            
            if (file.isFile()) {
                if (removeExtension) {
                    fileName = removeExtension(fileName);
                }
                filesWithoutDirectory.add(fileName);
                
            } else {
                String[] list = file.list();
                ArrayList<String> filesWithDirectory = new ArrayList<String>(list.length);
                for (String pageName : list) {
                    if (removeExtension) {
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
        if (lastIndexOf != -1) {
            value = name.substring(0, lastIndexOf);
        }
        return value;
    }
    
}

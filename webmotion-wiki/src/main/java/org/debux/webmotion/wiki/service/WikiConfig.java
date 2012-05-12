/*
 * #%L
 * Webmotion in wiki
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
import org.debux.webmotion.server.mapping.Properties;

/**
 * Helper on configuration.
 * 
 * @author julien
 */
public class WikiConfig {

    public static WikiConfig instance;
    
    public static final String PROPERTY_SITE_NAME = "site.name";
    public static final String PROPERTY_FILE_PATH = "file.path";
    public static final String PROPERTY_MEDIA_PATH = "media.path";
    public static final String PROPERTY_FIRST_PAGE = "first.page";
    public static final String PROPERTY_DEFAULT_LANGUAGE = "default.language";
    public static final String PROPERTY_SUPPORTED_LANGUAGE = "supported.language";
    public static final String PROPERTY_PUBLIC_PERMISSIONS = "public.permissions";
    public static final String PROPERTY_USERS_PATH = "users.path";
    
    protected Properties properties;

    public WikiConfig(Properties properties) {
        this.properties = properties;
    }
    
    public String getSiteName() {
        return properties.getString(PROPERTY_SITE_NAME);
    }

    public String getFilePath() throws URISyntaxException {
        return getPath(PROPERTY_FILE_PATH);
    }

    public String getMediaPath() throws URISyntaxException {
        return getPath(PROPERTY_MEDIA_PATH);
    }

    public String getFirstPage() {
        return properties.getString(PROPERTY_FIRST_PAGE);
    }

    public String getDefaultLanguage() {
        return properties.getString(PROPERTY_DEFAULT_LANGUAGE);
    }
    
    public String[] getSupportedLanguage() {
        return properties.getStringArray(PROPERTY_SUPPORTED_LANGUAGE);
    }
    
    public String[] getPublicPermissions() {
        return properties.getStringArray(PROPERTY_PUBLIC_PERMISSIONS);
    }
    
    public String getUsersPath() throws URISyntaxException {
        return getPath(PROPERTY_USERS_PATH);
    }
    
    public String getPath(String key) throws URISyntaxException {
        String filePath = properties.getString(key);
        if(filePath.startsWith("classpath:")) {
            filePath = filePath.replaceFirst("classpath:", "");
            URL url = WikiConfig.class.getClassLoader().getResource(filePath);
            filePath = url.getFile();
        }
        return filePath;
    }
}

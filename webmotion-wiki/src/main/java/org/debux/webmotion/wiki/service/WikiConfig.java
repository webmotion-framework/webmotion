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
import java.util.List;
import org.nuiton.util.ApplicationConfig;
import org.nuiton.util.ApplicationConfig.OptionDef;
import org.nuiton.util.ArgumentsParserException;

/**
 *
 * @author julien
 */
public class WikiConfig {

    protected static ApplicationConfig config;

    public static final String PROPERTY_SITE_NAME = "site.name";
    public static final String PROPERTY_FILE_PATH = "file.path";
    public static final String PROPERTY_MEDIA_PATH = "media.path";
    public static final String PROPERTY_FIRST_PAGE = "first.page";
    public static final String PROPERTY_DEFAULT_LANGUAGE = "default.language";
    public static final String PROPERTY_SUPPORTED_LANGUAGE = "supported.language";
    public static final String PROPERTY_PUBLIC_PERMISSIONS = "public.permissions";
    public static final String PROPERTY_USERS_PATH = "users.path";
    
    public static ApplicationConfig getConfig(String... args) throws ArgumentsParserException {
        if (config == null) {
            config = new ApplicationConfig(ConfigOption.CONFIG_FILE.getDefaultValue());
            config.parse(args);
        }
        return config;
    }

    public static String getSiteName() throws ArgumentsParserException {
        return getConfig().getOption(PROPERTY_SITE_NAME);
    }

    public static String getFilePath() throws ArgumentsParserException, URISyntaxException {
        return getPath(PROPERTY_FILE_PATH);
    }

    public static String getMediaPath() throws ArgumentsParserException, URISyntaxException {
        return getPath(PROPERTY_MEDIA_PATH);
    }

    public static String getFirstPage() throws ArgumentsParserException {
        return getConfig().getOption(PROPERTY_FIRST_PAGE);
    }

    public static String getDefaultLanguage() throws ArgumentsParserException {
        return getConfig().getOption(PROPERTY_DEFAULT_LANGUAGE);
    }
    
    public static List<String> getSupportedLanguage() throws ArgumentsParserException {
        return getConfig().getOptionAsList(PROPERTY_SUPPORTED_LANGUAGE).getOption();
    }
    
    public static List<String> getPublicPermissions() throws ArgumentsParserException {
        return getConfig().getOptionAsList(PROPERTY_PUBLIC_PERMISSIONS).getOption();
    }
    
    public static String getUsersPath() throws ArgumentsParserException, URISyntaxException {
        return getPath(PROPERTY_USERS_PATH);
    }
    
    public static String getPath(String key) throws ArgumentsParserException, URISyntaxException {
        String filePath = getConfig().getOption(key);
        if(filePath.startsWith("classpath:")) {
            filePath = filePath.replaceFirst("classpath:", "");
            URL url = WikiConfig.class.getClassLoader().getResource(filePath);
            filePath = url.getFile();
        }
        return filePath;
    }

    public enum ConfigOption implements OptionDef {
        
        CONFIG_FILE(ApplicationConfig.CONFIG_FILE_NAME, "Config file", "wikimotion-config.properties", String.class, false, false),
        SITE_NAME(PROPERTY_SITE_NAME, "Site name", null, String.class, false, false),
        FILE_PATH(PROPERTY_FILE_PATH, "File path to store the wiki pages", null, String.class, false, false),
        MEDIA_PATH(PROPERTY_MEDIA_PATH, "Media path to store the wiki pages", null, String.class, false, false),
        FIRST_PAGE(PROPERTY_FIRST_PAGE, "First page display", null, String.class, false, false),
        DEFAULT_LANGUAGE(PROPERTY_DEFAULT_LANGUAGE, "Default language", null, String.class, false, false),
        SUPPORTED_LANGUAGE(PROPERTY_SUPPORTED_LANGUAGE, "Supported language", null, String.class, false, false),
        PUBLIC_PERMISSIONS(PROPERTY_PUBLIC_PERMISSIONS, "Public permissions", null, String.class, false, false),
        USERS_PATH(PROPERTY_USERS_PATH, "File path to get users", null, String.class, false, false);
        
        private final String key;
        private final String description;
        private String defaultValue;
        private final Class<?> type;
        private boolean _transient;
        private boolean _final;

        ConfigOption(String key, String description, String defaultValue,
                Class<?> type, boolean _transient, boolean _final) {

            this.key = key;
            this.description = description;
            this.defaultValue = defaultValue;
            this.type = type;
            this._final = _final;
            this._transient = _transient;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public boolean isTransient() {
            return _transient;
        }

        @Override
        public boolean isFinal() {
            return _final;
        }

        @Override
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public void setTransient(boolean isTransient) {
            _transient = isTransient;
        }

        @Override
        public void setFinal(boolean isFinal) {
            _final = isFinal;
        }
    }
}

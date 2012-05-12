/*
 * #%L
 * WebMotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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
package org.debux.webmotion.server.mapping;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.debux.webmotion.server.WebMotionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all user configuration. 
 * 
 * @author julien
 */
public class Properties extends DataConfiguration {

    private static final Logger log = LoggerFactory.getLogger(Properties.class);
    
    public Properties() {
        super(new CompositeConfiguration());
    }
    
    public void addItem(PropertiesItem item) {
        ((CompositeConfiguration) configuration).addConfiguration(item);
    }

    public void addProperties(Properties properties) {
        ((CompositeConfiguration) configuration).addConfiguration(properties);
    }

    public static class PropertiesItem extends CompositeConfiguration {
        protected String name;

        public PropertiesItem(String name) {
            this.name = name;
            includeConfiguration(name + ".properties");
            
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            MapConfiguration mapConfiguration = new MapConfiguration(map);
            addConfiguration(mapConfiguration, true);
        }

        @Override
        public void addPropertyDirect(String key, Object value) {
            if ("include".equals(key)) {
                String fileName = (String) value;
                includeConfiguration(fileName);
            } else {
                super.addPropertyDirect(key, value);
            }
        }

        protected void includeConfiguration(String fileName) {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
            addConfiguration(propertiesConfiguration);

            try {
                // Search in system configuration path
                String systemConfigurationPath = WebMotionUtils.getSystemConfigurationPath();
                String fileNameSystem = systemConfigurationPath + File.separator + fileName;
                File fileSystem = new File(fileNameSystem);
                if (fileSystem.exists() && fileSystem.canRead()) {
                    propertiesConfiguration.load(fileSystem);
                }

                // Search in user configuration path
                String userConfigurationPath = WebMotionUtils.getUserConfigurationPath();
                String fileNameUser = userConfigurationPath + File.separator + fileName;
                File fileUser = new File(fileNameUser);
                if (fileUser.exists() && fileUser.canRead()) {
                    propertiesConfiguration.load(fileUser);
                }

                // Search in classpath
                ClassLoader classLoader = Properties.class.getClassLoader();
                URL url = classLoader.getResource(fileName);
                if (url != null) {
                    propertiesConfiguration.load(url);
                }

            } catch (ConfigurationException ce) {
                log.error("Error during read properties file " + fileName, ce);
            }
        }
    }
}

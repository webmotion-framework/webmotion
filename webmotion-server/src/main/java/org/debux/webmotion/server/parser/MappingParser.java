/*
 * #%L
 * Webmotion server
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
package org.debux.webmotion.server.parser;

import java.net.URL;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Interface to implement a parser to extends the mapping.
 * 
 * @author julien
 */
public abstract class MappingParser {

    /**
     * Parse the first mapping file found.
     * @param fileNames file names
     * @param defaultConfig default config
     * @return the representation of the file
     */
    public Mapping parse(String[] fileNames) {
        Mapping mapping = null;
        for (String fileName : fileNames) {
            URL url = getMappingUrl(fileName);
            if (url != null) {
               mapping = parse(url); 
               break;
            }
        }
        
        return mapping;
    }
    
    /**
     * Parse a mapping file
     * @param fileName file name
     * @param defaultConfig default config
     * @return the representation of the file
     */
    public Mapping parse(String fileName) {
        URL url = getMappingUrl(fileName);
        return parse(url);
    }
    
    /**
     * Search mapping file in class loader.
     * @param fileName file name
     * @return url of the mapping file
     */
    protected URL getMappingUrl(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null) {
            url = getClass().getResource(fileName);
            if (url == null) {
                throw new WebMotionException("No mapping found for " + fileName);
            }
        }        
        return url;
    }
        
    /**
     * Parse a mapping file on url
     * @param url mapping file to parse
     * @param defaultConfig default config
     * @return the representation of the file
     */
    protected abstract Mapping parse(URL url);
}

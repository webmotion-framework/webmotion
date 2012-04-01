/*
 * #%L
 * Webmotion in action
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
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * The class represents to how parse the mapping file.
 * 
 * @author jruchaud
 */
public abstract class MappingParser {

    /** Default config for the mapping */
    protected Config defaultConfig;

    public MappingParser() {
        this(null);
    }

    public MappingParser(Config defaultConfig) {
        this.defaultConfig = defaultConfig;
    }
    
    /**
     * @return true if the file exists otherwise false
     */
    public boolean exists(String fileName) {
        return getClass().getResource(fileName) != null;
    }
    
    /**
     * Parse a mapping file
     * @param fileName file name
     * @param defaultConfig default config
     * @return the representation of the file
     */
    public Mapping parse(String fileName) {
        URL url = getClass().getResource(fileName);
        return parse(url);
    }
    
    /**
     * Parse a mapping file on url
     * @param url mapping file to parse
     * @param defaultConfig default config
     * @return the representation of the file
     */
    protected abstract Mapping parse(URL url);
    
}

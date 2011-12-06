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
package org.debux.webmotion.server.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * The class represents all sections in the mapping file. This data are static, 
 * there is loaded when the server is deployed.
 * 
 * @author julien
 */
public class Mapping {

    /** Represents config section */
    protected Config config;
    
    /** Represents error section */
    protected List<ErrorRule> errorRules;
    
    /** Represents filter section */
    protected List<FilterRule> filterRules;
    
    /** Represents action section */
    protected List<ActionRule> actionRules;

    /** Represents extension section */
    protected List<String> extensionsPath;
    protected List<Mapping> extensionsMapping;

    public Mapping() {
        config = new Config();
        errorRules = new ArrayList<ErrorRule>();
        filterRules = new ArrayList<FilterRule>();
        actionRules = new ArrayList<ActionRule>();
        extensionsPath = new ArrayList<String>();
        extensionsMapping = new ArrayList<Mapping>();
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
    
    public List<FilterRule> getFilterRules() {
        return filterRules;
    }

    public void setFilterRules(List<FilterRule> filterRules) {
        this.filterRules = filterRules;
    }

    public List<ActionRule> getActionRules() {
        return actionRules;
    }

    public List<ErrorRule> getErrorRules() {
        return errorRules;
    }

    public List<Mapping> getExtensionsMapping() {
        return extensionsMapping;
    }

    public void setExtensionsMapping(List<Mapping> extensionsMapping) {
        this.extensionsMapping = extensionsMapping;
    }

    public List<String> getExtensionsPath() {
        return extensionsPath;
    }

    public void setExtensionsPath(List<String> extensionsPath) {
        this.extensionsPath = extensionsPath;
    }
    
    public void putExtensions(String path, Mapping mapping) {
        extensionsPath.add(path);
        extensionsMapping.add(mapping);
    }
}

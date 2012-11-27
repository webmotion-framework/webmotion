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
package org.debux.webmotion.server.mapping;

import java.util.List;

/**
 * The class is helper to wrap Mapping class. You can extends this class to add 
 * your behaviour.
 * 
 * @author julien
 */
public class MappingWrapper extends Mapping {

    /** Instance to wrap */
    protected Mapping mapping;

    /**
     * Default contructor use to create wrapper
     */
    public MappingWrapper(Mapping mapping) {
        this.mapping = mapping;
    }
    
    @Override
    public Config getConfig() {
        return mapping.getConfig();
    }

    @Override
    public void setConfig(Config config) {
        mapping.setConfig(config);
    }

    @Override
    public Properties getProperties() {
        return mapping.getProperties();
    }

    @Override
    public void setProperties(Properties properties) {
        mapping.setProperties(properties);
    }
    
    @Override
    public List<FilterRule> getFilterRules() {
        return mapping.getFilterRules();
    }

    @Override
    public void setFilterRules(List<FilterRule> filterRules) {
        mapping.setFilterRules(filterRules);
    }

    @Override
    public List<ActionRule> getActionRules() {
        return mapping.getActionRules();
    }

    @Override
    public void setActionRules(List<ActionRule> actionRules) {
        mapping.setActionRules(actionRules);
    }

    @Override
    public List<ErrorRule> getErrorRules() {
        return mapping.getErrorRules();
    }

    @Override
    public void setErrorRules(List<ErrorRule> errorRules) {
        mapping.setErrorRules(errorRules);
    }
    
    @Override
    public String getExtensionPath() {
        return mapping.getExtensionPath();
    }

    @Override
    public void setExtensionPath(String extensionPath) {
        mapping.setExtensionPath(extensionPath);
    }

    @Override
    public List<Mapping> getExtensionsRules() {
        return mapping.getExtensionsRules();
    }

    @Override
    public void setExtensionsRules(List<Mapping> extensionsRules) {
        mapping.setErrorRules(errorRules);
    }

    @Override
    public String getName() {
        return mapping.getName();
    }

    @Override
    public void setName(String name) {
        mapping.setName(name);
    }

    @Override
    public Mapping getParentMapping() {
        return mapping.getParentMapping();
    }

    @Override
    public void setParentMapping(Mapping parentMapping) {
        mapping.setParentMapping(mapping);
    }
    
}

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

    public Mapping() {
        config = new Config();
        errorRules = new ArrayList<ErrorRule>();
        filterRules = new ArrayList<FilterRule>();
        actionRules = new ArrayList<ActionRule>();
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

    /**
     * Extract in the given line an error rule
     * @param line one line in mapping
     */
    public void extractSectionErrors(String line) {
        String[] splitRule = line.split(" ");
        ErrorRule errorRule = new ErrorRule();
        errorRule.extractError(splitRule[0]);
        errorRule.extractAction(splitRule[1]);
        errorRules.add(errorRule);
    }

    /**
     * Extract in the given line an filter rule
     * @param line one line in mapping
     */
    public void extractSectionFilters(String line) {
        String[] splitRule = line.split(" ");
        FilterRule filterRule = new FilterRule();
        filterRule.extractMethod(splitRule[0]);
        filterRule.extractPattern(splitRule[1]);
        filterRule.extractAction(splitRule[2]);
        filterRules.add(filterRule);
    }

    /**
     * Extract in the given line an action rule
     * @param line one line in mapping
     */
    public void extractSectionActions(String line) {
        String[] splitRule = line.split(" ");
        ActionRule actionRule = new ActionRule();
        actionRule.extractMethod(splitRule[0]);
        actionRule.extractURLPattern(splitRule[1]);
        actionRule.extractAction(splitRule[2]);
        if(splitRule.length >= 4) {
            actionRule.extractDefaultParameters(splitRule[3]);
        }
        actionRules.add(actionRule);
    }

}

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

/**
 * Represents an error. The action is executed when the error occurs. The url 
 * syntax likes the servlet filter. The error can be a exception class  or http 
 * error code.
 * 
 * @author julien
 */
public class ErrorRule {

    protected String error;
    protected Action action;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Extract the error
     * @param ruleException 
     */
    public void extractError(String ruleException) {
        error = ruleException;
    }

    /**
     * Extract the action to execute
     * @param ruleAction 
     */
    public void extractAction(String ruleAction) {
        int typeSeparatorIndex = ruleAction.indexOf(":");
        action = new Action();
        
        String value;
        if(typeSeparatorIndex == -1) {
            value = ruleAction;
        } else {
            action.setType(ruleAction.substring(0, typeSeparatorIndex));
            value = ruleAction.substring(typeSeparatorIndex + 1);
        }
        
        int packageSeparatorIndex = value.lastIndexOf(".");
        action.setClassName(value.substring(0, packageSeparatorIndex));
        action.setMethodName(value.substring(packageSeparatorIndex + 1));
        action.setFullName(value);
    }

}

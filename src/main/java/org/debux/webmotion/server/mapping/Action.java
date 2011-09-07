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

import org.apache.commons.lang.StringUtils;

/**
 * Represents the action to execute when the url matches.
 * 
 * @author julien
 */
public class Action {

    public static String TYPE_ACTION = "action";
    public static String TYPE_VIEW = "view";
    public static String TYPE_URL = "url";
    
    protected String type;
    protected String fullName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getClassName() {
        return StringUtils.substringBeforeLast(fullName, ".");
    }

    public String getMethodName() {
        return StringUtils.substringAfterLast(fullName, ".");
    }

    public String getExtension() {
        return StringUtils.substringAfterLast(type, ".");
    }

    /**
     * @return true if the action is directly a action
     */
    public boolean isAction() {
        return type == null || type.toLowerCase().startsWith(TYPE_ACTION);
    }
    
    /**
     * @return true if the action is directly a view
     */
    public boolean isView() {
        return type != null && type.toLowerCase().startsWith(TYPE_VIEW);
    }
    
    /**
     * @return true if the action is directly a url
     */
    public boolean isUrl() {
        return type != null && type.toLowerCase().startsWith(TYPE_URL);
    }
    
}

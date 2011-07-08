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
 * Contains all configuration for run the server like the package name for view,
 * action, filter and error.
 * 
 * @author julien
 */
public class Config {

    // Modes
    public static String MODE_STATEFULL = "statefull";
    public static String MODE_STATELESS = "stateless";
    
    // Config names
    public static String PACKAGE_VIEWS = "package.views";
    public static String PACKAGE_ACTIONS = "package.actions";
    public static String PACKAGE_FILTERS = "package.filters";
    public static String PACKAGE_ERRORS = "package.errors";
    public static String REQUEST_ENCODING = "request.encoding";
    public static String RELOADABLE = "reloadable";
    public static String MODE = "mode";
    
    /** The package name where the view is searched */
    protected String packageViews = "";
    
    /** The package name where the action is searched */
    protected String packageActions = "";
    
    /** The package name where the filter is searched */
    protected String packageFilters = "";
    
    /** The package name where the error is searched */
    protected String packageErrors = "";
    
    /** Force the encoding in parameter */
    protected String requestEncoding = "UTF-8";
    
    /** Indicates if the servlet is reloadable */
    protected boolean reloadable = true;
    
    /** Precises the behavior of server is stateless or statefull */
    protected String mode = MODE_STATELESS;
    
    public Config() {
    }

    public String getPackageActions() {
        return packageActions;
    }

    public void setPackageActions(String packageActions) {
        this.packageActions = packageActions;
    }

    public String getPackageErrors() {
        return packageErrors;
    }

    public void setPackageErrors(String packageErrors) {
        this.packageErrors = packageErrors;
    }

    public String getPackageFilters() {
        return packageFilters;
    }

    public void setPackageFilters(String packageFilters) {
        this.packageFilters = packageFilters;
    }

    public String getPackageViews() {
        return packageViews;
    }

    public void setPackageViews(String packageViews) {
        this.packageViews = packageViews;
    }

    public String getRequestEncoding() {
        return requestEncoding;
    }

    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
    
    /**
     * Extract the value in line
     * @param name config name
     * @param line line contains the config
     * @return the value in line
     */
    public String extractConfig(String name, String line) {
        String value = line.substring((name + "=").length());
        return value;
    }
}

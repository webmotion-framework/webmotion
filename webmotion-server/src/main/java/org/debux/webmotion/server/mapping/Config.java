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

import org.debux.webmotion.server.WebMotionException;

/**
 * Contains all configuration for run the server like the package name for view,
 * action, filter and error.
 * 
 * @author julien
 */
public class Config {

    public static enum Scope {
        REQUEST,
        SINGLETON,
        SESSION
    }
    
    public static enum State {
        ENABLED,
        DISABLED,
        FORCED
    }
    
    public static int SERVER_SECRET_MIN_SIZE = 31;
    
    // Config names
    public static String PACKAGE_VIEWS = "package.views";
    public static String PACKAGE_BASE = "package.base";
    public static String PACKAGE_ACTIONS = "package.actions";
    public static String PACKAGE_FILTERS = "package.filters";
    public static String PACKAGE_ERRORS = "package.errors";
    public static String JAVAC_DEBUG = "javac.debug";
    public static String SERVER_ENCODING = "server.encoding";
    public static String SERVER_ASYNC = "server.async";
    public static String SERVER_CONTROLLER_SCOPE = "server.controller.scope";
    public static String SERVER_ERROR_PAGE = "server.error.page";
    public static String SERVER_LISTENER_CLASS = "server.listener.class";
    public static String SERVER_MAIN_HANDLER_CLASS = "server.main.handler.class";
    public static String SERVER_SECRET = "server.secret";
    public static String SERVER_STATIC_AUTODETECT = "server.static.autodetect";
    
    /** The package name where the view is searched */
    protected String packageViews = "";
    
    /** The package name used to prefix the actions, the filters and errors package */
    protected String packageBase = "";
    
    /** The package name where the action is searched */
    protected String packageActions = "";
    
    /** The package name where the filter is searched */
    protected String packageFilters = "";
    
    /** The package name where the error is searched */
    protected String packageErrors = "";
    
    /** Force the encoding in parameter and response*/
    protected String encoding = "UTF-8";
    
    /** Indicates if by default the request is process to asynchronous mode */
    protected boolean async = false;
    
    /** Indicates if the application is compile with debug mode */
    protected boolean javacDebug = true;
    
    /** Precises the behavior of server is stateless or statefull */
    protected Scope controllerScope = Scope.SINGLETON;
    
    /** Class to chain handler */
    protected String mainHandler = "org.debux.webmotion.server.WebMotionMainHandler";
    
    /** Indicates how the error page is use */
    protected State errorPage = State.ENABLED;
    
    /** Listener on start/stop server */
    protected String serverListener = null;
    
    /** Secret use to manage secure on server */
    protected String secret;
    
    /** Precises if the static ressources is detected by server */
    protected boolean staticAutodetect = true;
    
    /** Default contructor. */
    public Config() {
    }

    /**
     * Set the value from name
     * @param name name
     * @param value value
     */
    public void set(String name, String value) {
        if (PACKAGE_BASE.equals(name)) {
            setPackageBase(value);
        } else if (PACKAGE_ACTIONS.equals(name)) {
            setPackageActions(value);
        } else if (PACKAGE_ERRORS.equals(name)) {
            setPackageErrors(value);
        } else if (PACKAGE_FILTERS.equals(name)) {
            setPackageFilters(value);
        } else if (PACKAGE_VIEWS.equals(name)) {
            setPackageViews(value);
        } else if (SERVER_ENCODING.equals(name)) {
            setEncoding(value);
        } else if (SERVER_ASYNC.equals(name)) {
            setAsync(Boolean.valueOf(value));
        } else if (JAVAC_DEBUG.equals(name)) {
            setJavacDebug(Boolean.valueOf(value));
        } else if (SERVER_CONTROLLER_SCOPE.equals(name)) {
            setControllerScope(Scope.valueOf(value.toUpperCase()));
        } else if (SERVER_MAIN_HANDLER_CLASS.equals(name)) {
            setMainHandler(value);
        } else if (SERVER_ERROR_PAGE.equals(name)) {
            setErrorPage(State.valueOf(value.toUpperCase()));
        } else if (SERVER_LISTENER_CLASS.equals(name)) {
            setServerListener(value);
        } else if (SERVER_SECRET.equals(name)) {
            if (value.length() < Config.SERVER_SECRET_MIN_SIZE) {
                throw new WebMotionException("Secret is too short, the value must contain more 31 characters.");
            }
            setSecret(value);
        } else if (SERVER_STATIC_AUTODETECT.equals(name)) {
            setAsync(Boolean.valueOf(value));
        }
    }
    
    protected String getPackage(String packageName) {
        String result = packageBase;
        if(!packageBase.isEmpty() && !packageName.isEmpty()) {
            result += ".";
        }
        result += packageName;
        
        return result;
    }

    public String getPackageBase() {
        return packageBase;
    }

    public void setPackageBase(String packageBase) {
        this.packageBase = packageBase.replaceFirst("\\.$", "");
    }

    public String getPackageActions() {
        return getPackage(packageActions);
    }

    public void setPackageActions(String packageActions) {
        this.packageActions = packageActions.replaceFirst("^\\.", "");
    }

    public String getPackageErrors() {
        return getPackage(packageErrors);
    }

    public void setPackageErrors(String packageErrors) {
        this.packageErrors = packageErrors.replaceFirst("^\\.", "");
    }

    public String getPackageFilters() {
        return getPackage(packageFilters);
    }

    public void setPackageFilters(String packageFilters) {
        this.packageFilters = packageFilters.replaceFirst("^\\.", "");
    }

    public String getPackageViews() {
        return packageViews;
    }

    public void setPackageViews(String packageViews) {
        this.packageViews = packageViews;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String requestEncoding) {
        this.encoding = requestEncoding;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isJavacDebug() {
        return javacDebug;
    }

    public void setJavacDebug(boolean javacDebug) {
        this.javacDebug = javacDebug;
    }

    public Scope getControllerScope() {
        return controllerScope;
    }

    public void setControllerScope(Scope controllerScope) {
        this.controllerScope = controllerScope;
    }

    public String getMainHandler() {
        return mainHandler;
    }

    public void setMainHandler(String mainHandler) {
        this.mainHandler = mainHandler;
    }

    public State getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(State errorPage) {
        this.errorPage = errorPage;
    }

    public String getServerListener() {
        return serverListener;
    }

    public void setServerListener(String serverListener) {
        this.serverListener = serverListener;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isStaticAutodetect() {
        return staticAutodetect;
    }

    public void setStaticAutodetect(boolean staticAutodetect) {
        this.staticAutodetect = staticAutodetect;
    }
    
}

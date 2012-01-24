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
    
    // Config names
    public static String PACKAGE_VIEWS = "package.views";
    public static String PACKAGE_BASE = "package.base";
    public static String PACKAGE_ACTIONS = "package.actions";
    public static String PACKAGE_FILTERS = "package.filters";
    public static String PACKAGE_ERRORS = "package.errors";
    public static String REQUEST_ENCODING = "request.encoding";
    public static String REQUEST_ASYNC = "request.async";
    public static String JAVAC_DEBUG = "javac.debug";
    public static String HANDLERS_FACTORY_CLASS = "handlers.factory.class";
    public static String SERVER_CONTROLLER_SCOPE = "server.controller.scope";
    public static String SERVER_ERROR_PAGE = "server.error.page";
    public static String SERVER_LISTENER_CLASS = "server.listener.class";
    
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
    
    /** Force the encoding in parameter */
    protected String requestEncoding = "UTF-8";
    
    /** Indicates if by default the request is process to asynchronous mode */
    protected boolean requestAsync = false;
    
    /** Indicates if the application is compile with debug mode */
    protected boolean javacDebug = true;
    
    /** Precises the behavior of server is stateless or statefull */
    protected Scope controllerScope = Scope.SINGLETON;
    
    /** Class to chain handler */
    protected String handlersFactory = "org.debux.webmotion.server.WebMotionHandlerFactory";
    
    /** Indicates how the error page is use */
    protected State errorPage = State.ENABLED;
    
    /** Listener on start/stop server */
    protected String serverListener = null;
    
    /** Default contructor. */
    public Config() {
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

    public String getRequestEncoding() {
        return requestEncoding;
    }

    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    public boolean isRequestAsync() {
        return requestAsync;
    }

    public void setRequestAsync(boolean requestAsync) {
        this.requestAsync = requestAsync;
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

    public String getHandlersFactory() {
        return handlersFactory;
    }

    public void setHandlersFactory(String handlersFactory) {
        this.handlersFactory = handlersFactory;
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
}

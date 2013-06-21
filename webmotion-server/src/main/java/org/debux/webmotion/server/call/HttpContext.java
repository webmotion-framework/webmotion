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
package org.debux.webmotion.server.call;

import static org.debux.webmotion.server.WebMotionServer.PATH_DEPLOY;
import static org.debux.webmotion.server.WebMotionServer.PATH_ERROR;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.debux.webmotion.server.call.CookieManager.CookieEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains servlet elements i.e. the request and the response. Moreover the 
 * class proposes shortcut to access directly the main elements like URL, 
 * headers, parameters, session, cookies, errors, ...
 * 
 * @author jruchaud
 */
public class HttpContext {

    private static final Logger log = LoggerFactory.getLogger(HttpContext.class);

    // [jruchaud:20110611] TODO To complete
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HEADER_ENCODING = "Accept-Encoding";
    public static final String HEADER_LANGUAGE = "Accept-Language";
    public static final String HEADER_REFERER = "Referer";
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    
    public static final String ATTRIBUTE_ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    public static final String ATTRIBUTE_ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ATTRIBUTE_ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String ATTRIBUTE_ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String ATTRIBUTE_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String ATTRIBUTE_ERROR_JSP_EXCEPTION = "javax.servlet.jsp.jspException";
    
    public static final String ATTRIBUTE_INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String ATTRIBUTE_INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    public static final String ATTRIBUTE_INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String ATTRIBUTE_INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    public static final String ATTRIBUTE_INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
    
    public static final String ATTRIBUTE_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    public static final String ATTRIBUTE_FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
    public static final String ATTRIBUTE_FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
    public static final String ATTRIBUTE_FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
    public static final String ATTRIBUTE_FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";
    
    public static final String ATTRIBUTE_ERROR_DATA = "errorData";
    public static final String ATTRIBUTE_FLASH_MESSAGES = "flashMessages";
    public static final String ATTRIBUTE_EXTENSION_PATH = "extensionPath";
    
    /** Current HTTP request. */
    protected HttpServletRequest request;
    
    /** Current HTTP response. */
    protected HttpServletResponse response;

    /** Current server context */
    protected ServerContext serverContext;
            
    /** Helper to manage cookies */
    protected CookieManager cookieManager;
    
    /** Helper to manage session on the client*/
    protected ClientSession clientSession;
    
    /** Information on error contained in request */
    protected ErrorData errorData;
    
    /** Contains all message for the user */
    protected FlashMessages flashMessages;
    
    /** Keep current path for extension */
    protected String extensionPath;

    /**
     * Error data is utility to get information on error in attributes.
     */
    public class ErrorData {
        protected Integer statusCode = (Integer) request.getAttribute(ATTRIBUTE_ERROR_STATUS_CODE);
        protected String message = (String) request.getAttribute(ATTRIBUTE_ERROR_MESSAGE);
        protected Class<?> exceptionType = (Class) request.getAttribute(ATTRIBUTE_ERROR_EXCEPTION_TYPE);
        protected Throwable exception = (Throwable) request.getAttribute(ATTRIBUTE_ERROR_EXCEPTION);
        protected Throwable jspException = (Throwable) request.getAttribute(ATTRIBUTE_ERROR_JSP_EXCEPTION);
        protected String requestUri = (String) request.getAttribute(ATTRIBUTE_ERROR_REQUEST_URI);
        protected Throwable cause;
        
        public boolean isException() {
            return exceptionType != null || jspException != null;
        }

        public Throwable getException() {
            if(jspException != null) {
                return jspException;
            }
            return exception;
        }

        public Class<?> getExceptionType() {
            return exceptionType;
        }

        public String getMessage() {
            return message;
        }

        public String getRequestUri() {
            return requestUri;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public Throwable getCause() {
            return cause;
        }

        public void setCause(Throwable cause) {
            this.cause = cause;
        }
    }

    /**
     * Utility to store message for the user.
     */
    public class FlashMessages {
        
        public static final String PREFIX_FLASH_MESSAGES_INFOS = "wm_flash_infos_";
        public static final String PREFIX_FLASH_MESSAGES_ERRORS = "wm_flash_errors_";
        public static final String PREFIX_FLASH_MESSAGES_WARNINGS = "wm_flash_warnings_";
        public static final String PREFIX_FLASH_MESSAGES_MISCS = "wm_flash_miscs_";
        
        protected Map<String, String> infos;
        protected Map<String, String> errors;
        protected Map<String, String> warnings;
        protected Map<String, String> miscs;

        public FlashMessages() {
            infos = new HashMap<String, String>();
            errors = new HashMap<String, String>();
            warnings = new HashMap<String, String>();
            miscs = new HashMap<String, String>();
            
            Collection<String> names = cookieManager.getNames();
            for (String name : names) {
                String prefix = null;
                Map<String, String> messages = null;

                if (name.startsWith(PREFIX_FLASH_MESSAGES_INFOS)) {
                    prefix = PREFIX_FLASH_MESSAGES_INFOS;
                    messages = infos;

                } else if (name.startsWith(PREFIX_FLASH_MESSAGES_ERRORS)) {
                    prefix = PREFIX_FLASH_MESSAGES_ERRORS;
                    messages = errors;

                } else if (name.startsWith(PREFIX_FLASH_MESSAGES_WARNINGS)) {
                    prefix = PREFIX_FLASH_MESSAGES_WARNINGS;
                    messages = warnings;

                } else if (name.startsWith(PREFIX_FLASH_MESSAGES_MISCS)) {
                    prefix = PREFIX_FLASH_MESSAGES_MISCS;
                    messages = miscs;
                }

                if(prefix != null) {
                    CookieEntity cookie = cookieManager.get(name);
                    String value = cookie.getValue();
                    
                    name = name.replaceFirst(prefix, "");
                    messages.put(name, value);
                }
            }
        }
        
        public void addInfos(String key, String value) {
            add(infos, PREFIX_FLASH_MESSAGES_INFOS, key, value);
        }
        
        public void addErrors(String key, String value) {
            add(errors, PREFIX_FLASH_MESSAGES_ERRORS, key, value);
        }
        
        public void addWarnings(String key, String value) {
            add(warnings, PREFIX_FLASH_MESSAGES_WARNINGS, key, value);
        }
        
        public void addMiscs(String key, String value) {
            add(miscs, PREFIX_FLASH_MESSAGES_MISCS, key, value);
        }
        
        public void add(Map<String, String> map, String prefix, String key, String value) {
            // Store value if do redirect the request
            CookieEntity cookie = cookieManager.create(prefix + key, value);
            cookie.setMaxAge(10); // 10s
            cookie.setPath("/");
            cookieManager.add(cookie);
            
            // Store value if do forward the request
            map.put(key, value);
        }

        /**
         * Get info messages
         * @return 
         */
        public Map<String, String> getInfos() {
            return infos;
        }

        /**
         * Get error messages
         * @return 
         */
        public Map<String, String> getErrors() {
            return errors;
        }

        /**
         * Get warning messages
         * @return 
         */
        public Map<String, String> getWarnings() {
            return warnings;
        }

        /**
         * Get miscs messages
         * @return 
         */
        public Map<String, String> getMiscs() {
            return miscs;
        }

        /**
         * Get all messages
         * @return 
         */
        public Map<String, String> getMessages() {
            Map<String, String> messages = new HashMap<String, String>();
            messages.putAll(infos);
            messages.putAll(errors);
            messages.putAll(warnings);
            messages.putAll(miscs);
            return messages;
        }

        @Override
        public String toString() {
            return getMessages().toString();
        }
    }
    
    /**
     * Default contructor use to create wrapper to test
     */
    public HttpContext() {
    }
    
    public HttpContext(ServerContext serverContext, HttpServletRequest request, HttpServletResponse response) {
        this.serverContext = serverContext;
        this.request = request;
        this.response = response;
        
        this.cookieManager = new CookieManager(this);
        this.clientSession = null;
        this.errorData = new ErrorData();
        this.flashMessages = new FlashMessages();
        this.extensionPath = "";
        
        request.setAttribute(ATTRIBUTE_ERROR_DATA, errorData);
        request.setAttribute(ATTRIBUTE_FLASH_MESSAGES, flashMessages);
    }

    /**
     * @return current request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @return current response
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * @return current session
     */
    public HttpSession getSession() {
        return request.getSession();
    }
    
    /**
     * @return get cookies use getCookieManager to manage easily cookie 
     */
    public Cookie[] getCookies() {
        return request.getCookies();
    }
    
    /**
     * @return helper to manage cookie
     */
    public CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * @return helper to manage secure cookie
     */
    public CookieManager getCookieManager(String username, boolean encrypt, boolean ssl) {
        return new CookieManager(this, username, encrypt, ssl);
    }
    
    /**
     * @return helper to manage session on client
     */
    public ClientSession getClientSession() {
        if (clientSession == null) {
            // Lazy instanciation to not create cookie if not uses the session
            clientSession = new ClientSession(this);
        }
        return clientSession;
    }
    
    /**
     * Save the session on client.
     */
    public void saveClientSession() {
        if (clientSession != null) { // true if the session uses
            clientSession.write();
        }
    }
    
    /**
     * @return get real url corresponding in mapping
     */
    public String getUrl() {
        String url = null;
        String currentExtension = null;
        
        DispatcherType dispatcherType = request.getDispatcherType();
        if (dispatcherType == DispatcherType.INCLUDE) {
            url = (String) request.getAttribute(ATTRIBUTE_INCLUDE_REQUEST_URI);
            currentExtension = (String) request.getAttribute(ATTRIBUTE_EXTENSION_PATH);
            
        } else if (isError()) {
            url = errorData.getRequestUri();
            
        } else {
            url = request.getRequestURI();
        }
        
        // Delete jsessionid
        url = url.replaceFirst(";(jsessionid|JSESSIONID)=[\\w-]*($|\\?|#)", "");
        
        // Delete context path
        String contextPath = request.getContextPath();
        if (contextPath != null) {
            url = url.replaceFirst("^" + contextPath, "");
        }
        
        // Force old extension in url for include
        if (!url.startsWith(PATH_DEPLOY) && currentExtension != null) {
            url = currentExtension + url;
            
        } else {
            // Delete deploy path
            url = url.replaceFirst("^" + PATH_DEPLOY, "");
        }
        
        // Delete current extension processed
        if (!extensionPath.isEmpty()) {
            url = url.replaceFirst("^" + extensionPath, "");
        }
        
        if (url.isEmpty()) {
            url = "/";
        }
        
        return url;
    }
    
    /**
     * @return true if error path otherwise false
     */
    public boolean isError() {
        String url = null;
        
        DispatcherType dispatcherType = request.getDispatcherType();
        if(dispatcherType == DispatcherType.INCLUDE) {
            url = (String) request.getAttribute(ATTRIBUTE_INCLUDE_PATH_INFO);
        } else {
            String contextPath = request.getContextPath();
            String requestUri = request.getRequestURI();
            if (contextPath != null) {
                url = requestUri.replaceFirst(contextPath, "");
            }
        }
        
        return url != null && (url.startsWith(PATH_ERROR) || url.startsWith(PATH_DEPLOY + PATH_ERROR));
    }
        
    /**
     * @return get http method
     */
    public String getMethod() {
        String method = request.getMethod();
        if (method.equalsIgnoreCase("POST")) {
            
            String header = request.getHeader("X-HTTP-Method");
            if (header == null || header.isEmpty()) {
                header = request.getHeader("X-HTTP-Method-Override");
                if (header == null || header.isEmpty()) {
                    header = request.getHeader("X-METHOD-OVERRIDE");
                }
            }
            
            if ("DELETE".equalsIgnoreCase(header) || "PUT".equalsIgnoreCase(header) 
                    || "HEAD".equalsIgnoreCase(header)) {
                method = header;
            }
        }
        
        return method.toUpperCase();
    }
    
    /**
     * @return get writer on response
     */
    public PrintWriter getOut() throws IOException {
        return response.getWriter();
    }

    /**
     * @return get parameters in request
     */
    public Map<String, String[]> getParameters() {
        return request.getParameterMap();
    }

    /**
     * @return header value for name
     */
    public String getHeader(String name) {
        return request.getHeader(name);
    }
    
    /**
     * @return get bean contains error data
     */
    public ErrorData getErrorData() {
        return errorData;
    }

    /**
     * @return current extension path
     */
    public String getExtensionPath() {
        return extensionPath;
    }

    /**
     * Add extension path
     * @param extensionPath path
     */
    public void addExtensionPath(String extensionPath) {
        // Not change path when the extension is mount on root
        if (!"/".equals(extensionPath)) {
            this.extensionPath += extensionPath;
        }
        
        DispatcherType dispatcherType = request.getDispatcherType();
        if (dispatcherType != DispatcherType.INCLUDE) {
            request.setAttribute(ATTRIBUTE_EXTENSION_PATH, this.extensionPath);
        }
    }

    /**
     * Remove extension path
     * @param extensionPath path
     */
    public void removeExtensionPath(String extensionPath) {
        this.extensionPath = this.extensionPath.replaceFirst(extensionPath + "$", "");
    }

    /**
     * @return get context path
     */
    public String getContextPath() {
        return request.getContextPath();
    }
    
    /**
     * @return get servlet context
     */
    public ServletContext getServletContext() {
        return request.getServletContext();
    }
    
    /**
     * @return get server context
     */
    public ServerContext getServerContext() {
        return serverContext;
    }
    
    /**
     * The value is stored in cookie during 10s.<p>
     * For example Use JSTL to read value : flashMessages.miscs.<key>
     * @param value value
     */
    public void addFlashMessage(String key, String value) {
        flashMessages.addMiscs(key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with error status.<p>
     * For example Use JSTL to read value : flashMessages.errors.<key>
     * @param key key
     * @param value value
     */
    public void addErrorMessage(String key, String value) {
        flashMessages.addErrors(key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with info status.<p>
     * For example Use JSTL to read value : flashMessages.infos.<key>
     * @param key key
     * @param value value
     */
    public void addInfoMessage(String key, String value) {
        flashMessages.addInfos(key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with warning status.<p>
     * For example Use JSTL to read value : flashMessages.warnings.<key>
     * @param key key
     * @param value value
     */
    public void addWarningMessage(String key, String value) {
        flashMessages.addWarnings(key, value);
    }

}

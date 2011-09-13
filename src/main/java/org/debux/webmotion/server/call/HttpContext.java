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
package org.debux.webmotion.server.call;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import org.apache.commons.lang.StringUtils;

/**
 * Contains servlet elements i.e. the request and the response. Moreover the 
 * class proposes shortcut to access directly the main elements like URL, 
 * headers, parameters, session, cookies, errors, ...
 * 
 * @author jruchaud
 */
public class HttpContext {

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
    public static final String PREFIX_FLASH_MESSAGES = "flash_";
    
    /** Current HTTP request. */
    protected HttpServletRequest request;
    
    /** Current HTTP response. */
    protected HttpServletResponse response;
    
    /** Information on error contained in request. */
    protected ErrorData errorData;
    
    /** Contains all message for the user. */
    protected FlashMessages flashMessages;
    
    /** Contains all constraint violation. */
    protected Set<ConstraintViolation<?>> constraintViolations;
    
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
        
        public boolean isError() {
            String url = getUrl();
            return url != null && url.startsWith("/error");
        }
        
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
        
    }

    /**
     * Utility to store message for the user.
     */
    public class FlashMessages {
        protected Map<String, String> messages;

        public FlashMessages() {
            messages = new HashMap<String, String>();
            
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    if (name.startsWith(PREFIX_FLASH_MESSAGES)) {
                        name = name.replaceFirst(PREFIX_FLASH_MESSAGES, "");
                        messages.put(name, value);
                    }
                }
            }
        }
        
        public void add(String key, String value) {
            // Store value if do redirect the request
            Cookie cookie = new Cookie(PREFIX_FLASH_MESSAGES + key, value);
            cookie.setMaxAge(10); // 10s
            cookie.setPath("/");
            response.addCookie(cookie);
            
            // Store value if do forward the request
            messages.put(key, value);
        }

        public Map<String, String> getMessages() {
            return messages;
        }
    }
    
    /**
     * Default contructor use to create wrapper to test
     */
    public HttpContext() {
    }
    
    public HttpContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        
        this.errorData = new ErrorData();
        this.flashMessages = new FlashMessages();
        
        request.setAttribute(ATTRIBUTE_ERROR_DATA, errorData);
        request.setAttribute(ATTRIBUTE_FLASH_MESSAGES, flashMessages.getMessages());
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpSession getSession() {
        return request.getSession();
    }
    
    public Cookie[] getCookies() {
        return request.getCookies();
    }
    
    public String getUrl() {
        String pathInfo = request.getPathInfo();
        if(pathInfo == null) {
            pathInfo = (String) request.getAttribute(ATTRIBUTE_INCLUDE_PATH_INFO);
        }
        if(pathInfo == null) {
            pathInfo = (String) request.getAttribute(ATTRIBUTE_FORWARD_PATH_INFO);
        }
        return pathInfo;
    }
    
    public String getBaseUrl() {
        String url = request.getRequestURL().toString();
        String servletPath = request.getServletPath();

        String path = StringUtils.substringBefore(url, servletPath) + servletPath;
        return path;
    }
    
    public String getMethod() {
        return request.getMethod();
    }
    
    public PrintWriter getOut() throws IOException {
        return response.getWriter();
    }

    public Map<String, String[]> getParameters() {
        return request.getParameterMap();
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }
    
    public ErrorData getErrorData() {
        return errorData;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

    public void setConstraintViolations(Set<ConstraintViolation<?>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }
    
    /**
     * The value is stored in cookie during 10s.<p>
     * For example Use JSTL to read value : cookie.<key>
     * @param key key
     * @param value value
     */
    public void addFlashMessage(String key, String value) {
        flashMessages.add(key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with error status.<p>
     * For example Use JSTL to read value : flashMessages.error_<key>
     * @param key key
     * @param value value
     */
    public void addErrorMessage(String key, String value) {
        addFlashMessage("error_" + key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with info status.<p>
     * For example Use JSTL to read value : flashMessages.info_<key>
     * @param key key
     * @param value value
     */
    public void addInfoMessage(String key, String value) {
        addFlashMessage("info_" + key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with warning status.<p>
     * For example Use JSTL to read value : flashMessages.warning_<key>
     * @param key key
     * @param value value
     */
    public void addWarningMessage(String key, String value) {
        addFlashMessage("warning_" + key, value);
    }
    
}

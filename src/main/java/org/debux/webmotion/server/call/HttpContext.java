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
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    
    /** Current HTTP request. */
    protected HttpServletRequest request;
    
    /** Current HTTP response. */
    protected HttpServletResponse response;
    
    /** Information on error contained in request. */
    protected ErrorData errorData;
    
    /**
     * Error data is utility to get information on error in attributes.
     */
    public class ErrorData {
        protected Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        protected String message = (String) request.getAttribute("javax.servlet.error.message");
        protected Class<?> exceptionType = (Class) request.getAttribute("javax.servlet.error.exception_type");
        protected Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
        protected String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        
        public boolean isError() {
            return statusCode != null;
        }
        
        public boolean isException() {
            return exceptionType != null;
        }

        public Throwable getException() {
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

    public HttpContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        
        this.errorData = new ErrorData();
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
        return request.getPathInfo();
    }
    
    public String getMethod() {
        return request.getMethod();
    }
    
    public ServletOutputStream getOut() throws IOException {
        return response.getOutputStream();
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
    
    /**
     * The value is stored in cookie during 10s.<p>
     * For example Use JSTL to read value : cookie.<key>
     * @param key key
     * @param value value
     */
    public void addFlashMessage(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(10); // 10s
        response.addCookie(cookie);
    }
    
    /**
     * The value is stored in cookie during 10s with error status.<p>
     * For example Use JSTL to read value : cookie.error_<key>
     * @param key key
     * @param value value
     */
    public void addErrorMessage(String key, String value) {
        addFlashMessage("error_" + key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with info status.<p>
     * For example Use JSTL to read value : cookie.info_<key>
     * @param key key
     * @param value value
     */
    public void addInfoMessage(String key, String value) {
        addFlashMessage("info_" + key, value);
    }
    
    /**
     * The value is stored in cookie during 10s with warning status.<p>
     * For example Use JSTL to read value : cookie.warning_<key>
     * @param key key
     * @param value value
     */
    public void addWarningMessage(String key, String value) {
        addFlashMessage("warning_" + key, value);
    }
    
}

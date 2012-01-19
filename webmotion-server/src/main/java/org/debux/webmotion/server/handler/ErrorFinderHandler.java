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
package org.debux.webmotion.server.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Mapping;
import java.util.List;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionServerContext;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search in mapping the error matched at the url. In mapping file, the first 
 * line is most priority.
 * 
 * @author julien
 */
public class ErrorFinderHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorFinderHandler.class);

    @Override
    public void init(Mapping mapping, WebMotionServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        ErrorData errorData = call.getErrorData();
        Throwable exception = errorData.getException();

        Integer statusCode = errorData.getStatusCode();
        if(statusCode == null) {
            throw new WebMotionException("It is not possible to call directly error servlet", exception);
        }

        List<ErrorRule> errorRules = mapping.getErrorRules();
        for (ErrorRule errorRule : errorRules) {
            String error = errorRule.getError();

            if(error == null) {
                call.setErrorRule(errorRule);
                break;
                
            } else if(error.startsWith("code:")) {
                String code = statusCode.toString();
                if(error.equals("code:" + code)) {
                    call.setErrorRule(errorRule);
                    break;
                }

            } else if(exception != null) {
                try {
                    Class<?> errorClass = Class.forName(error);

                    Throwable throwableFound = getException(errorClass, exception);
                    if(throwableFound != null) {
                        errorData.setCause(throwableFound);
                        call.setErrorRule(errorRule);
                        break;
                    }

                } catch (ClassNotFoundException clnfe) {
                    throw new WebMotionException("Class not found with name " + error, clnfe);
                }
            }              
        }
        
        ErrorRule errorRule = call.getErrorRule();
        if(errorRule == null) {
            call.setRender(new WebMotionExceptionRender());
        }
        
        List<Executor> filters = new ArrayList<Executor>(0);
        call.setFilters(filters);
    }
    
    /**
     * Search in stacktrace, if found exception.
     * 
     * @param errorClass
     * @param throwable
     * @return 
     */
    public Throwable getException(Class<?> errorClass, Throwable throwable) {
        if(errorClass.isInstance(throwable)) {
            return throwable;
        } else {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return getException(errorClass, cause);
            }
        }
        return null;
    }
    
    /**
     * Basic render error to user.
     */
    public static class WebMotionExceptionRender extends Render {

        @Override
        public void create(Mapping mapping, Call call) throws IOException, ServletException {
            HttpContext context = call.getContext();
            HttpServletRequest request = context.getRequest();
            HttpServletResponse response = context.getResponse();


            // Retrieve the three possible error attributes, some may be null
            ErrorData errorData = context.getErrorData();
            Integer code = errorData.getStatusCode();
            String message = errorData.getMessage();
            Class<?> type = errorData.getExceptionType();
            Throwable throwable = errorData.getException();

            String uri = errorData.getRequestUri();
            if (uri == null) {
                uri = request.getRequestURI(); // in case there's no URI given
            }

            // The error reason is either the status code or exception type
            String reason = (code != null ? "Error " + code.toString() : type.toString());

            response.setContentType("text/html");
            
            PrintWriter out = context.getOut();
            out.println("<html>");
            out.println("<head>");
            
            out.println("<title>" + reason + "</title>");
            out.println("<style media=\"screen, projection\">");
            out.println("html { background: #e6e6e6;}"
                    + "body { margin: 0; }"
                    + "div { margin: 0 auto; width: 98%; }"
                    + "#content { background: #fff; border: 2px solid #ccc; width: auto;margin-top: 1em; padding: 0em 1em 1em; }"
                    + "h1 { margin: 1.083em 0 0; color: #333; }"
                    + "#about { text-align: center; }");
            out.println("</style>");
            
            out.println("</head>");
            out.println("<body>");
            
            out.println("<div>");
            
            out.println("<div id=\"content\">");
            out.println("<h1> Oops! " + reason + "</h1>");
            out.println("<h2>" + message + "</h2>");
            out.println("<pre>");
            if (throwable != null) {
                throwable.printStackTrace(out);
            }
            out.println("</pre>");
            out.println("<hr>");
            out.println("<i>Error accessing " + uri + "</i>");
            out.println("</div>");
            
            out.println("<div id=\"about\">");
            out.println("WebMotion");
            out.println("</div>");
            
            out.println("</div>");
            
            out.println("</body></html>");
        }
    }
}

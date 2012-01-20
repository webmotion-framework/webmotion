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

import org.debux.webmotion.server.render.RenderException;
import java.util.ArrayList;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Mapping;
import java.util.List;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.call.Executor;
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
    public void init(Mapping mapping, ServerContext context) {
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
            call.setRender(new RenderException());
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
}

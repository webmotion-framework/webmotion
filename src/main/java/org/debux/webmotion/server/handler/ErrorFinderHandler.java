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

import java.util.ArrayList;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Mapping;
import java.util.List;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.ExecutorAction;
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
    public void handle(Mapping mapping, Call call) {
        ErrorData errorData = call.getErrorData();

        List<ErrorRule> errorRules = mapping.getErrorRules();
        for (ErrorRule errorRule : errorRules) {

            String error = errorRule.getError();

            Integer statusCode = errorData.getStatusCode();
            String code = statusCode.toString();
            if(error.startsWith("code:")) {
                if(error.equals("code:" + code)) {
                    call.setErrorRule(errorRule);
                    break;
                }

            } else {
                Throwable exception = errorData.getException();
                try {
                    Class<?> errorClass = Class.forName(error);

                    if(exception != null && isException(errorClass, exception)) {
                        call.setErrorRule(errorRule);
                        break;
                    }

                } catch (ClassNotFoundException clnfe) {
                    throw new WebMotionException("Class not found with name " + error, clnfe);
                }
            }              
        }

        List<ExecutorAction> filters = new ArrayList<ExecutorAction>(0);
        call.setFilters(filters);
    }
    
    /**
     * Search in stacktrace, if found exception.
     * 
     * @param errorClass
     * @param throwable
     * @return 
     */
    public boolean isException(Class<?> errorClass, Throwable throwable) {
        if(errorClass.isInstance(throwable)) {
            return true;
        } else {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return isException(errorClass, cause);
            }
        }
        return false;
    }
}

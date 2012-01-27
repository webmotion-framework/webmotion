/*
 * #%L
 * Webmotion in test
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
package org.debux.webmotion.test.errors;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Error extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(Error.class);
    
    public Render notFound() {
        return renderView("error.jsp", 
                "code", "404"
            );
    }
    
    public Render npeError() {
        return renderView("error.jsp", 
                "code", "NullPointerException"
            );
    }
    
    public Render error() {
        return renderView("error.jsp", 
                "code", "exception"
            );
    }
    
    public Render validation() {
        HttpContext context = getContext();
        ErrorData errorData = context.getErrorData();
        ConstraintViolationException exception = (ConstraintViolationException) errorData.getException();
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        return renderView("error.jsp", 
                "code", constraintViolations
            );
    }
    
}

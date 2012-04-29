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
package org.debux.webmotion.server.handler;

import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Config;
import org.debux.webmotion.server.mapping.Config.State;
import org.debux.webmotion.server.render.RenderException;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext.ErrorData;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.Mapping;
import java.util.List;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.ServerContext;
import org.debux.webmotion.server.mapping.Rule;
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

        HttpContext context = call.getContext();
        ServerContext serverContext = context.getServerContext();
        Mapping rootMapping = serverContext.getMapping();
        Config rootConfig = rootMapping.getConfig();
        State errorPage = rootConfig.getErrorPage();
        
        if (errorPage != State.FORCED) {
            
            List<ErrorRule> errorRules = mapping.getErrorRules();
            for (ErrorRule errorRule : errorRules) {
                String error = errorRule.getError();

                if(error == null) {
                    call.setRule(errorRule);
                    break;

                } else if(error.startsWith("code:")) {
                    String code = statusCode.toString();
                    if(error.equals("code:" + code)) {
                        call.setRule(errorRule);
                        break;
                    }

                } else if(exception != null) {
                    try {
                        Class<?> errorClass = Class.forName(error);

                        Throwable throwableFound = getException(errorClass, exception);
                        if(throwableFound != null) {
                            errorData.setCause(throwableFound);
                            call.setRule(errorRule);
                            break;
                        }

                    } catch (ClassNotFoundException clnfe) {
                        throw new WebMotionException("Class not found with name " + error, clnfe);
                    }
                }              
            }
        }
        
        // Create a direct render if neither rule is found or the error page 
        // is forced
        Rule rule = call.getRule();
        String extensionPath = mapping.getExtensionPath();
        if (extensionPath == null && rule == null) {
            call.setRule(null);
            
            RenderException render;
            if (errorPage == State.DISABLED) {
                render = getRenderSimple();
            } else {
                render = getRender();
            }
            call.setRender(render);
        }
    }

    /**
     * @return the render use to display error with all informations.
     */
    protected RenderException getRender() {
        return new RenderException("template/render_exception.stg");
    }
    
    /**
     * @return the render use to display error without all informations.
     */
    protected RenderException getRenderSimple() {
        return new RenderException("template/render_simple_exception.stg");
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

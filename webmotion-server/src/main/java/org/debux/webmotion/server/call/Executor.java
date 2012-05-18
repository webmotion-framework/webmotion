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

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.mapping.Rule;

/**
 * Information used to execute the user request on reflection java. The executor 
 * contains information on method to execute. It is used to call method for 
 * error, action and filter.
 * 
 * @author julien
 */
public class Executor {

    /** Class */
    protected Class<? extends  WebMotionController> clazz;
    
    /** Method */
    protected Method method;
    
    /** Instance */
    protected WebMotionController instance;

    /** Parameters */
    protected Map<String, Object> parameters;
    
    /** Rule uses to create the Executor */
    protected Rule rule;

    public Executor() {
        parameters = new LinkedHashMap<String, Object>();
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public WebMotionController getInstance() {
        return instance;
    }

    public void setInstance(WebMotionController instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<? extends WebMotionController> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends WebMotionController> clazz) {
        this.clazz = clazz;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

}

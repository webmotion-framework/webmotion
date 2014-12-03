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

import org.debux.webmotion.server.render.Render;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Rule;

/**
 * The object represents the elements for resolving user request. This 
 * object is given to all handlers and it is created  at each new request.
 * 
 * @author julien
 */
public class Call {

    /** Contains the servlet elements : request, response, etc. */
    protected HttpContext context;

    /** The request contains a file. */
    protected boolean fileUploadRequest;
    
    /** The parameters contained in request URL (POST, GET) and in multi-part request. */
    protected Map<String, Object> extractParameters;
    
    /** All raw parameters contained in request, URL, default, aliases. */
    protected Map<String, Object> rawParameters;

    /** All parameters contained in request, URL, default, aliases. */
    protected ParameterTree parameterTree;

    /** Action rule or error rule selected in mapping by given URL. */
    protected Rule rule;
    
    /** Filters selected in mapping by given URL. */
    protected List<FilterRule> filterRules;
    
    /** Information to execute the method for action or error. */
    protected Executor executor;
    
    /** Information to execute the filters. */
    protected List<Executor> filters;
    
    /** Current executor is processed */
    protected Executor current;
    
    /** Add handlers used during executor invoker */
    protected List<WebMotionHandler> executorHandlers;
    
    /** The final render for user. */
    protected Render render;
    
    /** Indicate if the request is really asynchronous */
    protected boolean async;
    
    /**
     * Represent the parameters as a tree. It contains URL, default, request, 
     * aliases parameters.
     */
    public static class ParameterTree {
        
        protected Map<String, ParameterTree> object;
        protected Map<String, List<ParameterTree>> array;
        protected Object value;

        public Map<String, ParameterTree> getObject() {
            return object;
        }

        public void setObject(Map<String, ParameterTree> object) {
            this.object = object;
        }

        public Map<String, List<ParameterTree>> getArray() {
            return array;
        }

        public void setArray(Map<String, List<ParameterTree>> array) {
            this.array = array;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
        
    }
    
    /**
     * Default contructor use to create wrapper to test
     */
    public Call() {
        this.extractParameters = new LinkedHashMap<String, Object>();
        this.rawParameters = new LinkedHashMap<String, Object>();
        this.filterRules = new LinkedList<FilterRule>();
        this.filters = new LinkedList<Executor>();
        
        this.parameterTree = new ParameterTree();
//        this.parameterTree.setTree(new LinkedHashMap<String, ParameterTree>());
    }
    
    /**
     * Default consturctor on request and response.
     * 
     * @param serverContext current server context used to store attributes in server.
     * @param request HTTP request used to get information on user request.
     * @param response HTTP response used to put next render for user.
     */
    public Call(ServerContext serverContext, HttpServletRequest request, HttpServletResponse response) {
        this();
        this.context = new HttpContext(serverContext, request, response);
    }

    public HttpContext getContext() {
        return context;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public void setRender(Render render) {
        this.render = render;
    }

    public Render getRender() {
        return render;
    }

    public ParameterTree getParameterTree() {
        return parameterTree;
    }

    public void setParameterTree(ParameterTree parameterTree) {
        this.parameterTree = parameterTree;
    }

    public Map<String, Object> getExtractParameters() {
        return extractParameters;
    }

    public void setExtractParameters(Map<String, Object> extractParameters) {
        this.extractParameters = extractParameters;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    public Executor getExecutor() {
        return executor;
    }

    public List<Executor> getFilters() {
        return filters;
    }

    public void setFilters(List<Executor> filters) {
        this.filters = filters;
    }

    public List<FilterRule> getFilterRules() {
        return filterRules;
    }

    public void setFilterRules(List<FilterRule> filterRules) {
        this.filterRules = filterRules;
    }

    public List<Executor> getExecutors() {
        List<Executor> result = new ArrayList<Executor>(filters.size() + 1);
        result.addAll(filters);
        if (executor != null) {
            result.add(executor);
        }
        return result;
    }
    
    public HttpContext.ErrorData getErrorData() {
        return context.getErrorData();
    }

    public boolean isFileUploadRequest() {
        return fileUploadRequest;
    }

    public void setFileUploadRequest(boolean fileUploadRequest) {
        this.fileUploadRequest = fileUploadRequest;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public Executor getCurrent() {
        return current;
    }

    public void setCurrent(Executor current) {
        this.current = current;
    }

    public Rule getCurrentRule() {
        return current.getRule();
    }
    
    public List<WebMotionHandler> getExecutorHandlers() {
        return executorHandlers;
    }

    public void setExecutorHandlers(List<WebMotionHandler> executorHandlers) {
        this.executorHandlers = executorHandlers;
    }

    public Map<String, Object> getRawParameters() {
        return rawParameters;
    }

    public void setRawParameters(Map<String, Object> rawParameters) {
        this.rawParameters = rawParameters;
    }
    
}

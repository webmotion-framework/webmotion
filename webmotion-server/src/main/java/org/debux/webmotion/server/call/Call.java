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

import org.debux.webmotion.server.render.Render;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.mapping.FilterRule;

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
    
    /** The parameters contained in URL with the mapping-defined names. */
    protected Map<String, Object> aliasParameters;

    /** Error rule selected in mapping by given URL. */
    protected ErrorRule errorRule;
    
    /** Action rule selected in mapping by given URL. */
    protected ActionRule actionRule;
    
    /** Filters selected in mapping by given URL. */
    protected List<FilterRule> filterRules;
    
    /** Information to execute the method for action or error. */
    protected Executor executor;
    
    /** Information to execute the filters. */
    protected List<Executor> filters;
    
    /** The final render for user. */
    protected Render render;
    
    /**
     * Default contructor use to create wrapper to test
     */
    public Call() {
    }
    
    /**
     * Default consturctor on request and response.
     * @param request HTTP request used to get information on user request.
     * @param response HTTP response used to put next render for user.
     */
    public Call(HttpServletRequest request, HttpServletResponse response) {
        context = new HttpContext(request, response);
    }

    public HttpContext getContext() {
        return context;
    }

    public ActionRule getActionRule() {
        return actionRule;
    }

    public void setActionRule(ActionRule actionRule) {
        this.actionRule = actionRule;
    }

    public void setRender(Render render) {
        this.render = render;
    }

    public Render getRender() {
        return render;
    }

    public Map<String, Object> getAliasParameters() {
        return aliasParameters;
    }

    public void setAliasParameters(Map<String, Object> aliasParameters) {
        this.aliasParameters = aliasParameters;
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

    public ErrorRule getErrorRule() {
        return errorRule;
    }

    public void setErrorRule(ErrorRule errorRule) {
        this.errorRule = errorRule;
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
        if(executor != null) {
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

}

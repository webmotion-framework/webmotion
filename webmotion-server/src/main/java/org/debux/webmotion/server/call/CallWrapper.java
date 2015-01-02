/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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
import java.util.List;
import java.util.Map;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Call.ParameterTree;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Rule;

/**
 * The class is helper to wrap Call class. You can extends this class to add 
 * your behaviour.
 * 
 * @author julien
 */
public class CallWrapper extends Call {
    
    /** Instance to wrap */
    protected Call call;

    /**
     * Default contructor use to create wrapper
     */
    public CallWrapper(Call call) {
        this.call = call;
    }
    
    @Override
    public HttpContext getContext() {
        return call.getContext();
    }

    @Override
    public Rule getRule() {
        return call.getRule();
    }

    @Override
    public void setRule(Rule rule) {
        call.setRule(rule);
    }

    @Override
    public void setRender(Render render) {
        call.setRender(render);
    }

    @Override
    public Render getRender() {
        return call.getRender();
    }

    @Override
    public ParameterTree getParameterTree() {
        return call.getParameterTree();
    }

    @Override
    public void setParameterTree(ParameterTree parameterTree) {
        call.setParameterTree(parameterTree);
    }

    @Override
    public Map<String, Object> getExtractParameters() {
        return call.getExtractParameters();
    }

    @Override
    public void setExtractParameters(Map<String, Object> extractParameters) {
        call.setExtractParameters(extractParameters);
    }

    @Override
    public void setExecutor(Executor executor) {
        call.setExecutor(executor);
    }
    
    @Override
    public Executor getExecutor() {
        return call.getExecutor();
    }

    @Override
    public List<Executor> getFilters() {
        return call.getFilters();
    }

    @Override
    public void setFilters(List<Executor> filters) {
        call.setFilters(filters);
    }

    @Override
    public List<FilterRule> getFilterRules() {
        return call.getFilterRules();
    }

    @Override
    public void setFilterRules(List<FilterRule> filterRules) {
        call.setFilterRules(filterRules);
    }

    @Override
    public List<Executor> getExecutors() {
        return call.getExecutors();
    }
    
    @Override
    public HttpContext.ErrorData getErrorData() {
        return call.getErrorData();
    }

    @Override
    public boolean isFileUploadRequest() {
        return call.isFileUploadRequest();
    }

    @Override
    public void setFileUploadRequest(boolean fileUploadRequest) {
        call.setFileUploadRequest(fileUploadRequest);
    }

    @Override
    public boolean isAsync() {
        return call.isAsync();
    }

    @Override
    public void setAsync(boolean async) {
        call.setAsync(async);
    }

    @Override
    public Executor getCurrent() {
        return call.getCurrent();
    }

    @Override
    public void setCurrent(Executor current) {
        call.setCurrent(current);
    }

    @Override
    public Rule getCurrentRule() {
        return call.getCurrentRule();
    }
    
    @Override
    public List<WebMotionHandler> getExecutorHandlers() {
        return call.getExecutorHandlers();
    }

    @Override
    public void setExecutorHandlers(List<WebMotionHandler> executorHandlers) {
        call.setExecutorHandlers(executorHandlers);
    }
    
}

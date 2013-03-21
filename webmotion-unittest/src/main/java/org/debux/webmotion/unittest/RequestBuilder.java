package org.debux.webmotion.unittest;

/*
 * #%L
 * WebMotion test
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Debux
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

import java.net.URISyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

/**
 * Wrapper on URIBuilder to make use the URIBuilder in fluent API.
 * 
 * Add documentation on :
 * Fluent API with RequestBuilder
 * Executor
 * StringResponseHandler vs ContentResponseHandler
 * 
 * @author julien
 */
public class RequestBuilder extends URIBuilder {

    /**
     * @return a get request
     */
    public Request Get() throws URISyntaxException {
        return Request.Get(this.build());
    }

    /**
     * @return a post request
     */
    public Request Post() throws URISyntaxException {
        return Request.Post(this.build());
    }

    /**
     * @return a delete request
     */
    public Request Delete() throws URISyntaxException {
        return Request.Delete(this.build());
    }

    /**
     * @return a put request
     */
    public Request Put() throws URISyntaxException {
        return Request.Put(this.build());
    }

    /**
     * @return a option request
     */
    public Request Options() throws URISyntaxException {
        return Request.Options(this.build());
    }

    /**
     * @return a head request
     */
    public Request Head() throws URISyntaxException {
        return Request.Head(this.build());
    }

    /**
     * @return a trace request
     */
    public Request Trace() throws URISyntaxException {
        return Request.Trace(this.build());
    }

    @Override
    public RequestBuilder addParameter(String param, String value) {
        return (RequestBuilder) super.addParameter(param, value);
    }

    @Override
    public RequestBuilder setParameter(String param, String value) {
        return (RequestBuilder) super.setParameter(param, value);
    }

    @Override
    public RequestBuilder removeQuery() {
        return (RequestBuilder) super.removeQuery();
    }

    @Override
    public RequestBuilder setFragment(String fragment) {
        return (RequestBuilder) super.setFragment(fragment);
    }

    @Override
    public RequestBuilder setHost(String host) {
        return (RequestBuilder) super.setHost(host);
    }

    @Override
    public RequestBuilder setPort(int port) {
        return (RequestBuilder) super.setPort(port);
    }

    @Override
    public RequestBuilder setQuery(String query) {
        return (RequestBuilder) super.setQuery(query);
    }

    @Override
    public RequestBuilder setScheme(String scheme) {
        return (RequestBuilder) super.setScheme(scheme);
    }

    @Override
    public RequestBuilder setUserInfo(String userInfo) {
        return (RequestBuilder) super.setUserInfo(userInfo);
    }

    @Override
    public RequestBuilder setUserInfo(String username, String password) {
        return (RequestBuilder) super.setUserInfo(username, password);
    }

    @Override
    public RequestBuilder setPath(String path) {
        return (RequestBuilder) super.setPath(path);
    }
    
}

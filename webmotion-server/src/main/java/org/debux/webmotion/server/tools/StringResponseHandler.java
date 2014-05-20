package org.debux.webmotion.server.tools;

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

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

/**
 * Return the content as String without test on status code. 
 * You can @see ContentResponseHandler for more information.
 * 
 * @author julien
 */
public class StringResponseHandler implements ResponseHandler<String> {

    @Override
    public String handleResponse(
            final HttpResponse response) throws ClientProtocolException, IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);
        } else {
            return null;
        }
    }

}

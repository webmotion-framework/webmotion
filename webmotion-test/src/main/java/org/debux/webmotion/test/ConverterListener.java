/*
 * #%L
 * Webmotion website
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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
package org.debux.webmotion.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.debux.webmotion.server.WebMotionServerListener;
import org.debux.webmotion.server.call.ServerContext;

/**
 * Converter listener.
 * 
 * @author julien
 */
public class ConverterListener implements WebMotionServerListener {

    @Override
    public void onStart(ServerContext context) {
        context.addConverter(new AbstractConverter() {
            @Override
            protected Object convertToType(Class type, Object value) throws Throwable {
                String json = (String) value;
                JsonParser parser = new JsonParser();
                return parser.parse(json);
            }

            @Override
            protected Class getDefaultType() {
                return JsonElement.class;
            }
        }, JsonElement.class);
    }

    @Override
    public void onStop(ServerContext context) {
        // Do nothing
    }
    
}

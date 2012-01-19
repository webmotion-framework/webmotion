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

import java.util.HashMap;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Action;
import java.util.Map;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.WebMotionServerContext;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.ErrorRule;
import org.debux.webmotion.server.render.RenderUrl;
import org.debux.webmotion.server.render.RenderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create the render if it directly mapped on view or url, the executor is null 
 * in this case.
 * 
 * @author julien
 */
public class ActionExecuteRenderHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ActionExecuteRenderHandler.class);

    @Override
    public void init(Mapping mapping, WebMotionServerContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        Action action = null;
        ActionRule actionRule = call.getActionRule();
        if (actionRule != null) {
            action = actionRule.getAction();
        }
        
        ErrorRule errorRule = call.getErrorRule();
        if (errorRule != null) {
            action = errorRule.getAction();
        }

        if (action != null) {
            Map<String, Object> parameters = call.getAliasParameters();
            if (action.isView()) {
                String pageName = action.getFullName();
                pageName = WebMotionUtils.replaceDynamicName(pageName, parameters);

                Map<String, Object> model = null;
                if(parameters != null) {
                    model = convert(parameters);
                }
                        
                Render render = new RenderView(pageName, model);
                call.setRender(render);

            } else if (action.isUrl()) {
                String url = action.getFullName();
                url = WebMotionUtils.replaceDynamicName(url, parameters);

                Render render = new RenderUrl(url, null);
                call.setRender(render);
            }
        }
    }
    
    /**
     * Replace all array contains only one element to the first value.
     */
    protected Map<String, Object> convert(Map<String, Object> parameters) {
        Map<String, Object> converted = new HashMap<String, Object>(parameters.size());
        
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if(value != null) {
                Class clazz = value.getClass();
                if (clazz.isArray()) {
                    Object[] array = (Object[]) value;
                    if(array.length == 1) {
                        value = array[0];
                    }

                } else if (Map.class.isAssignableFrom(clazz)) {
                    Map map = (Map) value;
                    value = convert(map);
                }
            }
            converted.put(name, value);
        }
        
        return converted;
    }

}

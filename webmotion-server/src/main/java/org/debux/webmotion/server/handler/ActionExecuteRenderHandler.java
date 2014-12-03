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

import java.util.HashMap;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Action;
import java.util.Map;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.tools.HttpUtils;
import org.debux.webmotion.server.call.Call.ParameterTree;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.mapping.Rule;
import org.debux.webmotion.server.render.RenderForward;
import org.debux.webmotion.server.render.RenderRedirect;
import org.debux.webmotion.server.render.RenderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create the render if it directly mapped on view or url, the executor is null 
 * in this case.
 * 
 * @author julien
 */
public class ActionExecuteRenderHandler extends AbstractHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ActionExecuteRenderHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        Rule rule = call.getRule();
        if (rule != null) {
            
            Action action = rule.getAction();
            Map<String, Object> rawParameters = call.getRawParameters();
            
            if (action.isView()) {
                String pageName = action.getFullName();
                pageName = HttpUtils.replaceDynamicName(pageName, rawParameters);

                Map<String, Object> model = null;
                if (!rawParameters.isEmpty()) {
                    model = convert(rawParameters);
                }
                        
                Render render = new RenderView(pageName, model);
                call.setRender(render);

            } else if (action.isRedirect()) {
                String url = action.getFullName();
                url = HttpUtils.replaceDynamicName(url, rawParameters);

                Render render = new RenderRedirect(url, null);
                call.setRender(render);
                
            } else if (action.isForward()) {
                String url = action.getFullName();
                url = HttpUtils.replaceDynamicName(url, rawParameters);

                Map<String, Object> model = null;
                if (!rawParameters.isEmpty()) {
                    model = convert(rawParameters);
                }

                Render render = new RenderForward(url, model, null);
                call.setRender(render);
            }
        }
    }
    
    /**
     * Replace all array contains only one element to the first value.
     */
    protected Map<String, Object> convert(Map<String, Object> rawParameters) {
        Map<String, Object> converted = new HashMap<String, Object>(rawParameters.size());
        
        for (Map.Entry<String, Object> entry : rawParameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            Class clazz = value.getClass();
            if (clazz.isArray()) {
                Object[] array = (Object[]) value;
                if (array.length == 1) {
                    value = array[0];
                }
            }
            converted.put(name, value);
        }
        
        return converted;
    }

}

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

import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.mapping.Action;
import java.util.Map;
import java.util.regex.Pattern;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Render;
import org.debux.webmotion.server.mapping.ActionRule;
import org.debux.webmotion.server.mapping.ErrorRule;
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

    protected static Pattern pattern = Pattern.compile("\\{(\\p{Alnum}*)\\}");
    
    @Override
    public void handle(Mapping mapping, Call call) {
        Map<String, Object> model = call.getAliasParameters();

        Action action = null;
        ActionRule actionRule = call.getActionRule();
        
        if(actionRule != null) {
            action = actionRule.getAction();
        } else {
            ErrorRule errorRule = call.getErrorRule();
            action = errorRule.getAction();
        }

        if(action.isView()) {
            String pageName = action.getFullName().replaceAll("\\.", "/")
                    + "." + action.getExtension();
            
            Render render = new Render.RenderView(pageName, model);
            call.setRender(render);
            
        } else if(action.isUrl()) {
            String url = action.getFullName();
            if(url.startsWith("/")) {
                HttpContext context = call.getContext();
                url = context.getBaseUrl() + url;
            }
            
            Render render = new Render.RenderUrl(url, model);
            call.setRender(render);
        }
    }

}

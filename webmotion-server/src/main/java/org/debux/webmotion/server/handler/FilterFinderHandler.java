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

import java.util.regex.Pattern;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;
import java.util.List;
import java.util.regex.Matcher;
import org.debux.webmotion.server.WebMotionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search in mapping all filters matched at the url.
 * 
 * @author julien
 */
public class FilterFinderHandler extends AbstractHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(FilterFinderHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        List<FilterRule> filterRules = mapping.getFilterRules();
        List<FilterRule> selection = call.getFilterRules();
        
        HttpContext context = call.getContext();
        String url = context.getUrl();
        if (url != null) {
            
            String method = context.getMethod();
            for (FilterRule filterRule : filterRules) {

                if(checkMethod(filterRule, method) &&
                        checkUrl(filterRule, url)) {
                    selection.add(filterRule);
                }
            }
        }
    }
    
    // Check http method
    public boolean checkMethod(FilterRule filterRule, String method) {
        List<String> methods = filterRule.getMethods();
        return methods.contains("*") || methods.contains(method);
    }
    
    // Check url
    public boolean checkUrl(FilterRule filterRule, String url) {
        Pattern pattern = filterRule.getPattern();
        Matcher matcher = pattern.matcher(url);
        boolean found = matcher.find();
        log.debug("Filter pattern : " + pattern.pattern() + " = " + found);
        return found;
    }

}

/*
 * #%L
 * Webmotion in test
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
package org.debux.webmotion.test.filters;

import org.debux.webmotion.server.WebMotionFilter;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jruchaud
 */
public class Filters extends WebMotionFilter {
    
    private static final Logger log = LoggerFactory.getLogger(Filters.class);
    
    public void log() {
        log.info("> A request log before");
        doProcess();
        log.info("> A request log after");
    }
    
    public Render param(String value, int number) {
        log.info("> " + value + " / " + number);
        if(number > 0) {
            doProcess();
        } else {
            return renderView("error.jsp",
                    "code", "filter");
        }
        return null;
    }
    
}

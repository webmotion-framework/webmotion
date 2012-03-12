/*
 * #%L
 * WebMotion Feed
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
package org.debux.webmotion.feed;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julien
 */
public class FeedValues {

    private static final Logger log = LoggerFactory.getLogger(FeedValues.class);

    protected List<FeedValue> values;

    public FeedValues() {
        this.values = new LinkedList<FeedValue>();
    }

    public List<FeedValue> getValues() {
        return values;
    }

    public void setValues(List<FeedValue> values) {
        this.values = values;
    }
    
    public void addValues(FeedValue value) {
        this.values.add(value);
    }
}

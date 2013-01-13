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
package org.debux.webmotion.server.tools;

import java.util.LinkedList;

/**
 * Class to manage easely insert before/after an element in a LinkedList.
 * 
 * @author jruchaud
 */
public class OrderedList<E> extends LinkedList<E> {
    
    /**
     * Warning the method is without type.
     * @param elements elements to add in list
     * @return an list of the elements with the good type
     */
    public static <T> OrderedList<T> asList(Object ... elements) {
        OrderedList<T> result = new OrderedList<T>();
        for (Object element : elements) {
            result.add((T) element);
        }
        return result;
    }
    
    public void addBefore(E reference, E value) {
        int index = this.indexOf(reference);
        this.add(index, value);
    }
    
    public void addAfter(E reference, E value) {
        int index = this.indexOf(reference);
        this.add(index + 1, value);
    }
    
    public void replace(E reference, E value) {
        int index = this.indexOf(reference);
        this.set(index, value);
    }
    
}

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

package org.debux.webmotion.server.convention;

import org.reflections.scanners.AbstractScanner;

/**
 * Used to find class by the super class.
 * 
 * @author julien
 */
public class SuperClassScanner extends AbstractScanner {
        
    @Override
    public void scan(Object cls) {
        String className = getMetadataAdapter().getClassName(cls);
        String superClassName = getMetadataAdapter().getSuperclassName(cls);
        getStore().put(className, superClassName);
    }
    
}

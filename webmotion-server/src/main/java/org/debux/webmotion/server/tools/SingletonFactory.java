/*
 * #%L
 * Webmotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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

import java.util.HashMap;
import java.util.Map;
import org.debux.webmotion.server.WebMotionException;

/**
 * Simple singleton factory, maybe that two threads creates the instance, but
 * it is not a problem.
 */
public class SingletonFactory<T> {
    protected Map<Class<? extends T>, T> singletons;

    public SingletonFactory() {
        singletons = new HashMap<Class<? extends T>, T>();
    }

    public T get(Class<? extends T> clazz) {
        T instance = singletons.get(clazz);
        return instance;
    }

    public T get(String clazzName) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName);
            return get(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
        }
    }

    public T remove(Class<? extends T> clazz) {
        return singletons.remove(clazz);
    }

    public T remove(String clazzName) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName);
            return singletons.remove(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
        }
    }

    public T createInstance(String clazzName) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName);
            return createInstance(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
        }
    }

    public T createInstance(Class<? extends T> clazz) {
        try {
            T instance = clazz.newInstance();
            singletons.put(clazz, instance);
            return instance;
        } catch (IllegalAccessException iae) {
            throw new WebMotionException("Error during create handler factory " + clazz, iae);
        } catch (InstantiationException ie) {
            throw new WebMotionException("Error during create handler factory " + clazz, ie);
        }
    }

    public T getInstance(String clazzName) {
        try {
            Class<T> clazz = (Class<T>) Class.forName(clazzName);
            return getInstance(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new WebMotionException("Error during create handler factory " + clazzName, cnfe);
        }
    }

    public T getInstance(Class<? extends T> clazz) {
        T instance = get(clazz);
        if (instance == null) {
            instance = createInstance(clazz);
        }
        return instance;
    }
    
}

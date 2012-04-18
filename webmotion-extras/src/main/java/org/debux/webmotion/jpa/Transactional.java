/*
 * #%L
 * WebMotion extras
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
package org.debux.webmotion.jpa;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import org.debux.webmotion.server.WebMotionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage the transaction during execute the request.
 * 
 * @author julien
 */
public abstract class Transactional extends WebMotionFilter {

    private static final Logger log = LoggerFactory.getLogger(Transactional.class);

    public static String CURRENT_ENTITY_MANAGER = "wm_current_entity_manager";
    public static String CURRENT_ENTITY_TRANSACTION = "wm_current_entity_transaction";
    public static String CURRENT_GENERIC_DAO = "wm_current_generic_dao";
    
    protected Map<String, EntityManagerFactory> factories;

    public Transactional() {
        factories = new HashMap<String, EntityManagerFactory>();
    }
    
    /**
     * Create the transaction.
     * 
     * @param request
     * @param persistenceUnitName 
     */
    public void tx(HttpServletRequest request, String persistenceUnitName,
            String packageEntityName, String entityName) throws Exception {
        
        EntityManagerFactory factory = factories.get(persistenceUnitName);
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(persistenceUnitName);
        }
        EntityManager manager = factory.createEntityManager();
        request.setAttribute(CURRENT_ENTITY_MANAGER, manager);
        
        EntityTransaction transaction = manager.getTransaction();
        request.setAttribute(CURRENT_ENTITY_TRANSACTION, transaction);
        
        if (entityName != null) {
            String fullEntityName = null;
            if (packageEntityName != null) {
                fullEntityName = packageEntityName + "." + entityName;
            } else {
                fullEntityName = entityName;
            }
            
            GenericDAO genericDAO = new GenericDAO(manager, fullEntityName);
            request.setAttribute(CURRENT_GENERIC_DAO, genericDAO);
        }
        
        transaction.begin();

        try {
            doProcess();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }

        if (transaction.isActive()) {
            transaction.commit();
        }
        manager.close();
    }
}
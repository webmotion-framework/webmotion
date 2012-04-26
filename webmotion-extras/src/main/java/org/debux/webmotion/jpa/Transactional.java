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

    /** Default persistence unit name  */
    public static String DEFAULT_PERSISTENCE_UNIT_NAME = "webmotion";
    
    /** Attribute name stores the EntityManager  */
    public static String CURRENT_ENTITY_MANAGER = "wm_current_entity_manager";
    
    /** Attribute name stores the EntityTransaction  */
    public static String CURRENT_ENTITY_TRANSACTION = "wm_current_entity_transaction";
    
    /** Attribute name stores the GenericDAO  */
    public static String CURRENT_GENERIC_DAO = "wm_current_generic_dao";
    
    /** Cache all EntityManagerFactory */
    protected Map<String, EntityManagerFactory> factories;

    /** Default constructor */
    public Transactional() {
        factories = new HashMap<String, EntityManagerFactory>();
    }

    /**
     * Create the transaction and the GenericDAO if the entity name is not 
     * empty or null.
     * 
     * @param request set EntityManager, EntityTransaction and GenericDAO into the request
     * @param persistenceUnitName precise the persistence unit
     * @param packageEntityName precise the package of entity
     * @param entityName precise the class name of the entity
     * 
     * @throws Exception catch execption to rollback the transaction
     */
    public void tx(HttpServletRequest request, String persistenceUnitName,
            String packageEntityName, String entityName) throws Exception {
        
        // Create factory
        if (persistenceUnitName == null || persistenceUnitName.isEmpty()) {
            persistenceUnitName = DEFAULT_PERSISTENCE_UNIT_NAME;
        }
        EntityManagerFactory factory = factories.get(persistenceUnitName);
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(persistenceUnitName);
            factories.put(persistenceUnitName, factory);
        }
        
        // Create manager
        EntityManager manager = (EntityManager) request.getAttribute(CURRENT_ENTITY_MANAGER);
        if (manager == null) {
            manager = factory.createEntityManager();
            request.setAttribute(CURRENT_ENTITY_MANAGER, manager);
        }
        
        // Create generic DAO each time if callback an action on an other entity
        if (entityName != null) {
            String fullEntityName = null;
            if (packageEntityName != null) {
                fullEntityName = packageEntityName + "." + entityName;
            } else {
                fullEntityName = entityName;
            }
            
            GenericDAO genericDAO = new GenericDAO(manager, fullEntityName);
            request.setAttribute(CURRENT_GENERIC_DAO, genericDAO);
            
        } else {
            request.setAttribute(CURRENT_GENERIC_DAO, null);
        }
        
        // Create transaction
        EntityTransaction transaction = (EntityTransaction) request.getAttribute(CURRENT_ENTITY_TRANSACTION);
        if (transaction == null) {
            transaction = manager.getTransaction();
            request.setAttribute(CURRENT_ENTITY_TRANSACTION, transaction);
        
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
            
        } else {
            doProcess();
        }
    }
}

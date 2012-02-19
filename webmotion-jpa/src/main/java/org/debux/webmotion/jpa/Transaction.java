/*
 * #%L
 * WebMotion JPA
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

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.debux.webmotion.server.WebMotionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction extends WebMotionFilter {

    private static final Logger log = LoggerFactory.getLogger(Transaction.class);

    protected EntityManagerFactory factory;

    public Transaction() {
        factory = Persistence.createEntityManagerFactory("webmotion");
    }
    
    public void manage() {
        Map<String, Object> parameters = getParameters();
        
        EntityManager manager = factory.createEntityManager();
        parameters.put("manager", manager);
        
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        
        parameters.put("transaction", transaction);
        
        doProcess();
        
        transaction.commit();
        manager.close();
    }
    
}

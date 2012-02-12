package org.debux.webmotion.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
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
        EntityManager manager = factory.createEntityManager();
        
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        
        doProcess();
        
        transaction.commit();
        manager.close();
    }
    
}

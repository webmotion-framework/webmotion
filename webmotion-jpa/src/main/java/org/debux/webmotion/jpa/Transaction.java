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

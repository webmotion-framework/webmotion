package org.debux.webmotion.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.debux.webmotion.jpa.GenericDAO.Parameters;

/**
 * Test the generic dao.
 * 
 * @author julien
 */
public class GenericDAOTest {
    
    protected EntityManagerFactory factory;
    protected EntityManager manager;
    protected EntityTransaction transaction;
    protected GenericDAO dao;
    
    protected String comment1Id;
    protected String comment2Id;
    protected String comment3Id;
    
    @BeforeMethod
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("webmotion");
        manager = factory.createEntityManager();
        manager.setFlushMode(FlushModeType.COMMIT);
        
        dao = new GenericDAO(manager, Comment.class);
        
        transaction = manager.getTransaction();
        transaction.begin();
        
        Comment comment1 = new Comment();
        comment1.setUsername("john");
        comment1.setComment("bla bla");
        comment1.setNote(19);
        manager.persist(comment1);
        comment1Id = comment1.getId();
        
        Comment comment2 = new Comment();
        comment2.setUsername("john");
        comment2.setComment("bla bla");
        comment2.setNote(13);
        manager.persist(comment2);
        comment2Id = comment2.getId();
        
        Comment comment3 = new Comment();
        comment3.setUsername("jack");
        comment3.setComment("bla bla");
        comment3.setNote(20);
        manager.persist(comment3);
        comment3Id = comment3.getId();
        
        transaction.commit();
        
        manager.clear();
        
        transaction.begin();
    }
    
    @AfterMethod
    public void tearDown() {
        transaction.commit();
        manager.close();
        factory.close();
    }
    
    protected Parameters createData() {
        Parameters parameters = Parameters.create()
                .add("username", "test")
                .add("comment", "bla bla bla")
                .add("note", "10")
                .add("tags", "java")
                .add("tags", "jee")
                .add("tags", "wm")
                .add("parent", comment1Id)
                .add("threads", new String[]{comment2Id, comment3Id})
                .add("unused", "unused");
        return parameters;
    }
    
    @Test
    public void testExtract() {
        Parameters parameters = createData();
        Comment entity = (Comment) dao.extract(parameters);
        AssertJUnit.assertNotNull(entity);
        AssertJUnit.assertNotNull(entity.getParent());
        AssertJUnit.assertEquals(2, entity.getThreads().size());
    }

    @Test
    public void testCreate() {
        Parameters parameters = createData();
        IdentifiableEntity entity = dao.create(parameters);
        AssertJUnit.assertNotNull(entity);
        
        entity = (Comment) dao.find(entity.getId());
        AssertJUnit.assertNotNull(entity);
    }
    
    @Test
    public void testFind() {
        IdentifiableEntity entity = dao.find(comment1Id);
        AssertJUnit.assertNotNull(entity);
    }
    
    @Test
    public void testNotFind() {
        IdentifiableEntity entity = dao.find("invalid");
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testDelete() {
        boolean deleted = dao.delete(comment1Id);
        AssertJUnit.assertTrue(deleted);
        
        Comment entity = (Comment) dao.find(comment1Id);
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testInvalidDelete() {
        boolean deleted = dao.delete("invalid");
        AssertJUnit.assertFalse(deleted);
        
        Comment entity = manager.find(Comment.class, "invalid");
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testUpdate() {
        Parameters parameters = Parameters.create()
                .add("username", "test");
        
        Comment entity = (Comment) dao.update(comment1Id, parameters);
        AssertJUnit.assertEquals("test", entity.getUsername());
        
        entity = (Comment) dao.find(comment1Id);
        AssertJUnit.assertEquals("test", entity.getUsername());
    }
    
    @Test
    public void testInvalidUpdate() {
        Parameters parameters = Parameters.create()
                .add("username", "test");
        Comment entity = (Comment) dao.update("invalid", parameters);
        
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testBasicQuery() {
        Parameters parameters = Parameters.create();
        List result = dao.query("findAll", parameters);
        AssertJUnit.assertEquals(3, result.size());
    }
    
    @Test
    public void testParameterQuery() {
        Parameters parameters = Parameters.create()
                .add("username", "john");
        List result = dao.query("findByUsername", parameters);
        AssertJUnit.assertEquals(2, result.size());
    }
    
    @Test
    public void testParametersQuery() {
        Parameters parameters = Parameters.create()
                .add("usernames", "john")
                .add("usernames", "jack")
                .add("usernames", "tutu");
        List result = dao.query("findByUsernames", parameters);
        AssertJUnit.assertEquals(3, result.size());
    }
    
    @Test
    public void testParametersExec() {
        Parameters parameters = Parameters.create()
                .add("note", 12);
        int result = dao.exec("updateNote", parameters);
        AssertJUnit.assertEquals(3, result);
        
        Comment entity = (Comment) dao.find(comment1Id);
        AssertJUnit.assertEquals(12, entity.getNote());
    }
    
}

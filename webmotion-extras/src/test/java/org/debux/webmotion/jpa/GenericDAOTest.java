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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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
    protected GenericDAO dao;
    
    protected Comment comment1;
    protected Comment comment2;
    protected Comment comment3;
    
    @BeforeMethod
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("webmotion");
        manager = factory.createEntityManager();
        dao = new GenericDAO(manager, Comment.class);
        
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        
        comment1 = new Comment();
        comment1.setUsername("john");
        comment1.setComment("bla bla");
        comment1.setNote(19);
        manager.persist(comment1);
        
        comment2 = new Comment();
        comment2.setUsername("john");
        comment2.setComment("bla bla");
        comment2.setNote(13);
        manager.persist(comment2);
        
        comment3 = new Comment();
        comment3.setUsername("jack");
        comment3.setComment("bla bla");
        comment3.setNote(20);
        manager.persist(comment3);
        
        transaction.commit();
    }
    
    @AfterMethod
    public void tearDown() {
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
                .add("parent", comment1.getId())
                .add("threads", new String[]{comment2.getId(), comment3.getId()})
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
    }
    
    @Test
    public void testFind() {
        IdentifiableEntity entity = dao.find(comment1.getId());
        AssertJUnit.assertNotNull(entity);
    }
    
    @Test
    public void testNotFind() {
        IdentifiableEntity entity = dao.find("invalid");
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testDelete() {
        String id = comment1.getId();
        dao.delete(id);
        Comment entity = manager.find(Comment.class, id);
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testInvalidDelete() {
        dao.delete("invalid");
        Comment entity = manager.find(Comment.class, "invalid");
        AssertJUnit.assertNull(entity);
    }
    
    @Test
    public void testUpdate() {
        String id = comment1.getId();
        
        Parameters parameters = Parameters.create()
                .add("username", "test");
        dao.update(id, parameters);
        
        Comment entity = manager.find(Comment.class, id);
        AssertJUnit.assertEquals("test", entity.getUsername());
    }
    
    @Test
    public void testInvalidUpdate() {
        Parameters parameters = Parameters.create()
                .add("username", "test");
        dao.update("invalid", parameters);
        
        Comment entity = manager.find(Comment.class, "invalid");
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
    
}

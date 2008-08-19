/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.structuredb.persistency.dao;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.BaseObject;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.ILoggedInUserKeeper;
import net.bioclipse.structuredb.persistency.HsqldbTestServerManager;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic base class for testing the daos. Performs tests of the basic dao methods.
 * Usage: Extend and give the domain class to be tested as generic class parameter.
 *        When extra testsmethods are written setUpTestEnvironment() needs to be called
 *        to initiate the dao.
 * 
 * @author jonalv
 *
 * @param <DomainType>
 */
public abstract class GenericDaoTest<DomainType extends BaseObject> 
                extends AbstractAnnotationAwareTransactionalTests  {

    static {
        HsqldbTestServerManager.INSTANCE.startServer();
        HsqldbTestServerManager.INSTANCE.setupTestEnvironment();
        System.setProperty(
           "javax.xml.parsers.SAXParserFactory", 
           "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        );
        System.setProperty(
           "javax.xml.parsers.DocumentBuilderFactory", 
           "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );
    }
    
    protected IGenericDao<DomainType> dao;
    
    private Class<DomainType> domainClass;
    
    protected DomainType object1;
    protected DomainType object2;
    
    protected User testUser;

    /**
     * @param c Class for the domain type to be tested
     */
    public GenericDaoTest( Class<DomainType> c ) {
        super();
        this.domainClass = c;
    }
    
    @Transactional
    public void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        
        testUser = new User("username", "password", true);
        
        ((ILoggedInUserKeeper)applicationContext.getBean("loggedInUserKeeper"))
        .setLoggedInUser(null);
        
        IUserDao userDao = (IUserDao) applicationContext.getBean("userDao");
        userDao.insert(testUser);
        
        ((ILoggedInUserKeeper)applicationContext.getBean("loggedInUserKeeper"))
        .setLoggedInUser(testUser);
        
        String daoName = domainClass.getSimpleName() + "Dao";
        daoName = firstToLowerCase(daoName);
        dao = (IGenericDao<DomainType>) applicationContext.getBean(daoName);
        try {
            object1 = domainClass.newInstance();
            object2 = domainClass.newInstance();
            object2.setName("otherName");
            addCreatorAndEditor(object1);
            addCreatorAndEditor(object2);
        } 
        catch (Exception e) {
            fail( e.toString() );
        }
        dao.insert(object1);
        dao.insert(object2);
    }
    
    private String firstToLowerCase(String daoName) {
        return Character.toLowerCase( daoName.charAt(0) ) + daoName.substring(1);
    }

    protected void addCreatorAndEditor(BaseObject object) {
        Timestamp now = new Timestamp( System.currentTimeMillis() );
        object.setCreated(now);
        object.setEdited(now);
        object.setCreator(testUser);
        object.setLastEditor(testUser);
    }

    /**
     * tests getting all objects of the domain type handled by the tested dao
     */
    public void testGetAll() {
        List<DomainType> objects = dao.getAll();
        assertTrue( objects.contains(object1) );
        assertTrue( objects.contains(object2) );
    }
    
    /**
     * tests getting an instance by id of the domain type handled by the tested dao
     */
    public void testGetById() {
        DomainType loadedObject1 = dao.getById( object1.getId() );
        assertNotNull( "The lodaded object should not be null", loadedObject1 );
        assertTrue( "The loaded object should have values equal to the original object", object1.hasValuesEqualTo(loadedObject1) );
        assertNotSame( "The loaded object and the original object shuold not be the same",  object1, loadedObject1 );
    }
    
    /**
     * tests deleting of an instance of the domain type handled by the tested dao 
     */
    public void testDelete() {
        
        String sqlDomainObject 
            = "SELECT COUNT(*) FROM " + domainClass.getSimpleName() 
              + " WHERE id='" + object1.getId() + "'";
        String sqlBaseObject 
            = "SELECT COUNT(*) FROM BaseObject WHERE id='" 
              + object1.getId() + "'";
        
        int numberInDomainTableBefore = jdbcTemplate
                                        .queryForInt(sqlDomainObject);
        int numberInBaseTableBefore = jdbcTemplate
                                      .queryForInt(sqlBaseObject);
        
        dao.delete( object1.getId() );
        DomainType loadedObject1 = dao.getById( object1.getId() );
        assertNull(loadedObject1);
        
        int numberInBaseTableAfter = jdbcTemplate.queryForInt(sqlBaseObject);
        assertEquals( "The entry should be deleted", 
                      numberInBaseTableBefore - 1, 
                      numberInBaseTableAfter );
        
        int numberInDomainTableAfter = jdbcTemplate
                                       .queryForInt(sqlDomainObject);
        assertEquals( "The entry should be deleted", 
                      numberInDomainTableBefore - 1, 
                      numberInDomainTableAfter );
    }
    
    /**
     * tests updating an instance of the domain type handled by the tested dao
     */
    public void testUpdate() {
        DomainType loadedObject1 = dao.getById( object1.getId() );
        assertFalse(loadedObject1.getName().equals("edited"));
        loadedObject1.setName("edited");
        dao.update(loadedObject1);
        loadedObject1 = dao.getById( object1.getId() );
        assertEquals("name should have changed", "edited", loadedObject1.getName());
    }
    
    protected String[] getConfigLocations() {
        String path = Structuredb.class.getClassLoader()
                                 .getResource("applicationContext.xml")
                                 .toString();
        
        return new String[] { path };
    }
}

/* *****************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.structuredb.persistence.dao;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.BaseObject;
import net.bioclipse.structuredb.persistence.HsqldbTestServerManager;
import net.bioclipse.structuredb.persistency.dao.IGenericDao;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic base class for testing the daos. Performs tests of the basic 
 * dao methods.
 * Usage: Extend and give the domain class to be tested as generic class 
 *        parameter. When extra testsmethods are written setUpTestEnvironment() 
 *        needs to be called to initiate the dao.
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
    
    private static Logger logger = Logger.getLogger( GenericDaoTest.class );
    
    protected IGenericDao<DomainType> dao;
    
    private Class<DomainType> domainClass;
    
    protected DomainType object1;
    protected DomainType object2;
    
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
        
        String daoName = domainClass.getSimpleName() + "Dao";
        daoName = firstToLowerCase(daoName);
        try {
            dao = (IGenericDao<DomainType>) applicationContext.getBean(daoName);
            object1 = domainClass.newInstance();
            object2 = domainClass.newInstance();
            addAuditInformation(object1);
            addAuditInformation(object2);
        } 
        catch (Exception e) {
            LogUtils.debugTrace( logger, e );
            throw new RuntimeException(e);
        }
        try {
            Method m = object1.getClass().getMethod( "setName", String.class );
            m.invoke( object1, "object1" );
            m.invoke( object2, "object2" );
        }
        catch ( NoSuchMethodException e ) {
            //TODO: find a way to test this with an if instead...
            //      ...if there is one...
        }
        dao.insert(object1);
        dao.insert(object2);
    }
    
    private String firstToLowerCase(String daoName) {
        return Character.toLowerCase( daoName.charAt(0) ) 
               + daoName.substring(1);
    }

    protected void addAuditInformation(BaseObject object) {
        Timestamp now = new Timestamp( System.currentTimeMillis() );
        object.setCreated(now);
        object.setEdited(now);
    }

    /**
     * tests getting all objects of the domain type handled by the tested dao
     */
    public void testGetAll() {
        List<DomainType> objects = dao.getAll();
        Assert.assertTrue( objects.contains(object1) );
        Assert.assertTrue( objects.contains(object2) );
    }
    
    /**
     * tests getting an instance by id of the domain type handled by the 
     * tested dao
     */
    public void testGetById() {
        DomainType loadedObject1 = dao.getById( object1.getId() );
        Assert.assertNotNull( "The lodaded object should not be null", 
                              loadedObject1 );
        Assert.assertEquals( "The loaded object should be equal to the origianl " +
        		      "object", 
        		      object1, 
        		      loadedObject1 );
        Assert.assertTrue( "The loaded object should have values equal to " 
                        + "the original object", 
        		    object1.hasValuesEqualTo(loadedObject1) );
        Assert.assertNotSame( "The loaded object and the original object should "
                           + "not be the same",  
                       object1, 
                       loadedObject1 );
    }
    
    /**
     * tests deleting of an instance of the domain type handled by the 
     * tested dao 
     * @throws Exception 
     */
    public void testDelete() throws Exception {
        
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
        Assert.assertNull(loadedObject1);
        
        int numberInBaseTableAfter = jdbcTemplate.queryForInt(sqlBaseObject);
        Assert.assertEquals( "The entry should be deleted", 
                      numberInBaseTableBefore - 1, 
                      numberInBaseTableAfter );
        
        int numberInDomainTableAfter = jdbcTemplate
                                       .queryForInt(sqlDomainObject);
        Assert.assertEquals( "The entry should be deleted", 
                      numberInDomainTableBefore - 1, 
                      numberInDomainTableAfter );
    }
    
    /**
     * tests updating an instance of the domain type handled by the tested dao
     */
    public void testUpdate() {
        DomainType loadedObject1 = dao.getById( object1.getId() );
        Timestamp before = loadedObject1.getEdited();
        dao.update(loadedObject1);
        loadedObject1 = dao.getById( object1.getId() );
        Assert.assertFalse( "timestamp should have changed", 
                     before.equals( loadedObject1.getEdited() ) );
    }
    
    protected String[] getConfigLocations() {
        String path = Structuredb.class.getClassLoader()
                                 .getResource("applicationContext.xml")
                                 .toString();
        
        return new String[] { path };
    }
}

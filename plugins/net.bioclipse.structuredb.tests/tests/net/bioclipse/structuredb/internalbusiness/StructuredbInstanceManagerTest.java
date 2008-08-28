/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.persistence.HsqldbTestServerManager;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import testData.TestData;

public class StructuredbInstanceManagerTest 
       extends AbstractAnnotationAwareTransactionalTests  {
    
    protected IStructuredbInstanceManager manager;
    
    protected IAnnotationDao annotationDao;
    protected IUserDao       userDao;
    protected IDBMoleculeDao  dBMoleculeDao;

    public StructuredbInstanceManagerTest() {
        super();
    }
    
    static {
        
        System.setProperty(
           "javax.xml.parsers.SAXParserFactory", 
           "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        );
        System.setProperty(
           "javax.xml.parsers.DocumentBuilderFactory", 
           "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );        
        
        HsqldbTestServerManager.INSTANCE.startServer();
        HsqldbTestServerManager.INSTANCE.setupTestEnvironment();
    }
    
    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        manager = (IStructuredbInstanceManager) 
                  applicationContext.getBean("structuredbInstanceManager");
        annotationDao    = (IAnnotationDao) applicationContext
                                    .getBean("annotationDao");
        dBMoleculeDao = (IDBMoleculeDao) applicationContext
                                       .getBean("dBMoleculeDao");
        userDao      = (IUserDao) applicationContext
                                  .getBean("userDao");
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        ( (ILoggedInUserKeeper) applicationContext
                .getBean("loggedInUserKeeper") )
                .setLoggedInUser(null);
        User testUser = new User("username", "password", true);
        userDao.insert(testUser);
        ( (ILoggedInUserKeeper) applicationContext
                                .getBean("loggedInUserKeeper") )
                                .setLoggedInUser(testUser);
    }
    
    private Annotation createAnnotation(String name) {
        Annotation annotation = new Annotation(name);
        manager.insertAnnotation(annotation);
        return annotation;
    }
    
    public void testInsertLabel() {
        Annotation annotation = createAnnotation("testLabel");
        List<Annotation> allLabels = annotationDao.getAll(); 
        assertTrue( allLabels.contains(annotation) );
    }

    private DBMolecule createStructure( String name, 
                                       AtomContainer atomContainer) 
            throws CDKException {

        long before = System.currentTimeMillis();
        DBMolecule dBMolecule = new DBMolecule( name, atomContainer );
        long inBetween = System.currentTimeMillis();
        manager.insertStructure(dBMolecule);
        long after = System.currentTimeMillis();
        System.out.println("Creating structure took: " + (inBetween - before) + "ms");
        System.out.println("Persisting structure took: " + (after - inBetween) + "ms");
        return dBMolecule;
    }
    
    public void testInsertStructure() throws CDKException {
        
        DBMolecule dBMolecule = createStructure( "CycloOctan", 
                                               TestData.getCycloOctan() );
        List<DBMolecule> allStructures = dBMoleculeDao.getAll(); 
        assertTrue( allStructures.contains(dBMolecule) );
    }

    private User createUser(String username, String password, boolean sudoer) {
        User user = new User(username, password, sudoer);
        manager.insertUser(user);
        return user;
    }
    
    public void testInsertUser() {

        User user = createUser("another username", "secrest", false);
        List<User> allUsers = userDao.getAll(); 
        assertTrue( allUsers.contains(user) );
    }

    public void testDeleteLibrary() {
        Annotation annotation = createAnnotation("testLabel");
        assertTrue( annotationDao.getAll().contains(annotation) );
        manager.delete(annotation);
        assertFalse( annotationDao.getAll().contains(annotation) );
    }

    public void testDeleteUser() {
        User user = createUser("another username", "secrest", true);
        assertTrue( userDao.getAll().contains(user) );
        manager.delete(user);
        assertFalse( userDao.getAll().contains(user) );
    }

    public void testDeleteStructure() throws CDKException {
        DBMolecule dBMolecule = createStructure( "CycloOcan", 
                                               TestData.getCycloOctan() );
        assertTrue( dBMoleculeDao.getAll().contains(dBMolecule) );
        manager.delete(dBMolecule);
        assertFalse( dBMoleculeDao.getAll().contains(dBMolecule) );
    }

    public void testRetrieveAllLibraries() {
        Annotation folder1 = createAnnotation("testLibrary1");
        Annotation folder2 = createAnnotation("testLibrary2");
        
        assertTrue( annotationDao.getAll().containsAll( 
                Arrays.asList(new Annotation[] {folder1, folder2}) ) );
    }

    public void testRetrieveAllStructures() throws CDKException {
        DBMolecule structure1 = createStructure( "CycloOctan", 
                                                 TestData.getCycloOctan() );
        DBMolecule structure2 = createStructure( "CycloPropane", 
                                                 TestData.getCycloPropane() );
        
        assertTrue( dBMoleculeDao.getAll().containsAll(
                Arrays.asList(new DBMolecule[] {structure1, structure2}) ) );
    }

    public void testRetrieveAllUsers() {
        User user1 = createUser("username1", "secret", false);
        User user2 = createUser("username2", "masterkey", true);
        
        assertTrue( userDao.getAll().containsAll( 
                Arrays.asList(new User[] {user1, user2}) ) );
    }

    public void testRetrieveLibraryByName() {
        Annotation annotation = createAnnotation("testLibrary1");
        
        assertNotNull(annotation);
        
        assertEquals( annotation, 
                      manager.retrieveAnnotationByName("testLibrary1") );
    }

    public void testRetrieveStructureByName() throws CDKException {
        DBMolecule dBMolecule = createStructure( "CycloOctan", 
                                                TestData.getCycloOctan() );
        assertTrue( manager
                    .retrieveStructureByName("CycloOctan")
                    .contains(dBMolecule) );
    }

    public void testRetrieveUserByName() {
        User user = createUser("another username", "secret", false);
        
        assertNotNull(user);
        
        assertEquals( user,
                      manager.retrieveUserByUsername("another username") );
    }

    public void testUpdateLibrary() {
        Annotation annotation = createAnnotation("testLibrary");
        annotation.setName("edited");
        manager.update(annotation);
        assertEquals( annotation, annotationDao.getById(annotation.getId()) );
    }

    public void testUpdateUser() {
        User user = createUser("another username", "secret", false);
        user.setName("edited");
        manager.update(user);
        assertEquals( user, userDao.getById(user.getId()) );
    }

    public void testUpdateStructure() throws CDKException {
        DBMolecule dBMolecule = createStructure( "CycloOctan", 
                                               TestData.getCycloOctan() );
        dBMolecule.setName("edited");
        manager.update(dBMolecule);
        assertEquals( dBMolecule, dBMoleculeDao.getById(dBMolecule.getId()) );
    }
    
    public void testDeleteLabelAndStructures() throws CDKException {
        DBMolecule dBMolecule = createStructure( "CycloOcan", 
                                               TestData.getCycloOctan() );
        Annotation annotation = createAnnotation( "test" );
        dBMolecule.addAnnotation( annotation );
        manager.update( annotation );
        
        assertTrue( annotationDao.getById( annotation.getId() )
                            .getDBMolecules()
                            .contains(dBMolecule) );
        manager.deleteWithStructures( annotation, null );
        assertFalse( dBMoleculeDao.getAll().contains(dBMolecule) );
        assertFalse( annotationDao.getAll().contains(annotation) );
    }
    
    public void testAllStructureIterator() throws CDKException {
        testRetrieveAllStructures();
        List<DBMolecule> dBMolecules = manager.retrieveAllStructures();
        Iterator<DBMolecule> structureIterator = manager.allStructuresIterator();
        assertTrue( structureIterator.hasNext() );
        while ( structureIterator.hasNext() ) {
            assertTrue( dBMolecules.contains( structureIterator.next() ) );
        }
    }
    
    protected String[] getConfigLocations() {
        String path = Structuredb.class.getClassLoader()
                                 .getResource("applicationContext.xml")
                                 .toString();
        
        return new String[] { path };
    }
}

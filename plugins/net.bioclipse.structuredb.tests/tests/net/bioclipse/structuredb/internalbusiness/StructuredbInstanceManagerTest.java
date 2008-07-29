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
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.persistency.HsqldbTestServerManager;
import net.bioclipse.structuredb.persistency.dao.ILabelDao;
import net.bioclipse.structuredb.persistency.dao.IStructureDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import testData.TestData;

public class StructuredbInstanceManagerTest 
       extends AbstractAnnotationAwareTransactionalTests  {
    
    protected IStructuredbInstanceManager manager;
    
    protected ILabelDao    labelDao;
    protected IUserDao      userDao;
    protected IStructureDao structureDao;

    public StructuredbInstanceManagerTest() {
        super();
    }
    
    static {
        HsqldbTestServerManager.INSTANCE.startServer();
        HsqldbTestServerManager.INSTANCE.setupTestEnvironment();
    }
    
    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        manager = (IStructuredbInstanceManager) 
                  applicationContext.getBean("structuredbInstanceManager");
        labelDao    = (ILabelDao) applicationContext
                                    .getBean("labelDao");
        structureDao = (IStructureDao) applicationContext
                                       .getBean("structureDao");
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
    
    private Label createLabel(String name) {
        Label label = new Label(name);
        manager.insertLabel(label);
        return label;
    }
    
    public void testInsertLabel() {
        Label label = createLabel("testLabel");
        List<Label> allLabels = labelDao.getAll(); 
        assertTrue( allLabels.contains(label) );
    }

    private Structure createStructure( String name, 
                                       AtomContainer atomContainer) 
            throws CDKException {

        long before = System.currentTimeMillis();
        Structure structure = new Structure( name, atomContainer );
        long inBetween = System.currentTimeMillis();
        manager.insertStructure(structure);
        long after = System.currentTimeMillis();
        System.out.println("Creating structure took: " + (inBetween - before) + "ms");
        System.out.println("Persisting structure took: " + (after - inBetween) + "ms");
        return structure;
    }
    
    public void testInsertStructure() throws CDKException {
        
        Structure structure = createStructure( "CycloOctan", 
                                               TestData.getCycloOctan() );
        List<Structure> allStructures = structureDao.getAll(); 
        assertTrue( allStructures.contains(structure) );
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
        Label label = createLabel("testLabel");
        assertTrue( labelDao.getAll().contains(label) );
        manager.delete(label);
        assertFalse( labelDao.getAll().contains(label) );
    }

    public void testDeleteUser() {
        User user = createUser("another username", "secrest", true);
        assertTrue( userDao.getAll().contains(user) );
        manager.delete(user);
        assertFalse( userDao.getAll().contains(user) );
    }

    public void testDeleteStructure() throws CDKException {
        Structure structure = createStructure( "CycloOcan", 
                                               TestData.getCycloOctan() );
        assertTrue( structureDao.getAll().contains(structure) );
        manager.delete(structure);
        assertFalse( structureDao.getAll().contains(structure) );
    }

    public void testRetrieveAllLibraries() {
        Label folder1 = createLabel("testLibrary1");
        Label folder2 = createLabel("testLibrary2");
        
        assertTrue( labelDao.getAll().containsAll( 
                Arrays.asList(new Label[] {folder1, folder2}) ) );
    }

    public void testRetrieveAllStructures() throws CDKException {
        Structure structure1 = createStructure( "CycloOctan", 
                                                 TestData.getCycloOctan() );
        Structure structure2 = createStructure( "CycloPropane", 
                                                 TestData.getCycloPropane() );
        
        assertTrue( structureDao.getAll().containsAll(
                Arrays.asList(new Structure[] {structure1, structure2}) ) );
    }

    public void testRetrieveAllUsers() {
        User user1 = createUser("username1", "secret", false);
        User user2 = createUser("username2", "masterkey", true);
        
        assertTrue( userDao.getAll().containsAll( 
                Arrays.asList(new User[] {user1, user2}) ) );
    }

    public void testRetrieveLibraryByName() {
        Label label = createLabel("testLibrary1");
        
        assertNotNull(label);
        
        assertEquals( label, 
                      manager.retrieveLabelByName("testLibrary1") );
    }

    public void testRetrieveStructureByName() throws CDKException {
        Structure structure = createStructure( "CycloOctan", 
                                                TestData.getCycloOctan() );
        assertTrue( manager
                    .retrieveStructureByName("CycloOctan")
                    .contains(structure) );
    }

    public void testRetrieveUserByName() {
        User user = createUser("another username", "secret", false);
        
        assertNotNull(user);
        
        assertEquals( user,
                      manager.retrieveUserByUsername("another username") );
    }

    public void testUpdateLibrary() {
        Label label = createLabel("testLibrary");
        label.setName("edited");
        manager.update(label);
        assertEquals( label, labelDao.getById(label.getId()) );
    }

    public void testUpdateUser() {
        User user = createUser("another username", "secret", false);
        user.setName("edited");
        manager.update(user);
        assertEquals( user, userDao.getById(user.getId()) );
    }

    public void testUpdateStructure() throws CDKException {
        Structure structure = createStructure( "CycloOctan", 
                                               TestData.getCycloOctan() );
        structure.setName("edited");
        manager.update(structure);
        assertEquals( structure, structureDao.getById(structure.getId()) );
    }
    
    public void testDeleteLabelAndStructures() throws CDKException {
        Structure structure = createStructure( "CycloOcan", 
                                               TestData.getCycloOctan() );
        Label label = createLabel( "test" );
        structure.addLabel( label );
        manager.update( label );
        
        assertTrue( labelDao.getById( label.getId() )
                            .getStructures()
                            .contains(structure) );
        manager.deleteWithStructures( label, null );
        assertFalse( structureDao.getAll().contains(structure) );
        assertFalse( labelDao.getAll().contains(label) );
    }
    
    public void testAllStructureIterator() throws CDKException {
        testRetrieveAllStructures();
        List<Structure> structures = manager.retrieveAllStructures();
        Iterator<Structure> structureIterator = manager.allStructuresIterator();
        assertTrue( structureIterator.hasNext() );
        while ( structureIterator.hasNext() ) {
            assertTrue( structures.contains( structureIterator.next() ) );
        }
    }
    
    protected String[] getConfigLocations() {
        String path = Structuredb.class.getClassLoader()
                                 .getResource("applicationContext.xml")
                                 .toString();
        
        return new String[] { path };
    }
}

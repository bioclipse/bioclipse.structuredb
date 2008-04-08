/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
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

import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.persistency.HsqldbTestServerManager;
import net.bioclipse.structuredb.persistency.dao.IFolderDao;
import net.bioclipse.structuredb.persistency.dao.IStructureDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

import org.openscience.cdk.exception.CDKException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import testData.TestData;

public class StructuredbInstanceManagerTest 
       extends AbstractAnnotationAwareTransactionalTests  {
	
	protected IStructuredbInstanceManager manager;
	
	protected IFolderDao   folderDao;
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
	}
	
	public void testCreateLibrary() {
		Folder folder = manager.createLibrary("testLibrary");
		assertTrue( folderDao.getAll().contains(folder) );
	}

	public void testCreateStructureAtomContainer() throws CDKException {
		
		Structure structure = manager
		                      .createStructure( "CycloOctan", 
		                    		            TestData.getCycloOctan() );
		assertTrue( structureDao.getAll().contains(structure) );
	}
	
	public void testCreateStructureICDKMolecule() {
		fail("Not yet implemented, waiting for ICDKMolecule");
	}

	public void testCreateUser() {
		User user = manager.createUser("newuser", "secret", true);
		assertTrue( userDao.getAll().contains(user) );
	}

	public void testDeleteLibrary() {
		Folder folder = manager.createLibrary("testLibrary");
		assertTrue( folderDao.getAll().contains(folder) );
		manager.delete(folder);
		assertFalse( folderDao.getAll().contains(folder) );
	}

	public void testDeleteUser() {
		User user = manager.createUser("newuser", "secret", true);
		assertTrue( userDao.getAll().contains(user) );
		manager.delete(user);
		assertFalse( userDao.getAll().contains(user) );
	}

	public void testDeleteStructure() throws CDKException {
		Structure structure = manager
                              .createStructure( "CycloOctan", 
      		                                    TestData.getCycloOctan() );
		assertTrue( structureDao.getAll().contains(structure) );
		manager.delete(structure);
		assertFalse( structureDao.getAll().contains(structure) );
	}

	public void testRetrieveAllLibraries() {
		Folder library1 = manager.createLibrary("testLibrary1");
		Folder library2 = manager.createLibrary("testLibrary2");
		
		assertTrue( folderDao.getAll().containsAll( 
				Arrays.asList(new Folder[] {library1, library2}) ) );
	}

	public void testRetrieveAllStructures() throws CDKException {
		Structure structure1 = manager
		                       .createStructure( "CycloOctan", 
				                                 TestData.getCycloOctan() );
		Structure structure2 = manager
                               .createStructure( "CycloPropane", 
                                                 TestData.getCycloPropane() );
		
		assertTrue( structureDao.getAll().containsAll(
				Arrays.asList(new Structure[] {structure1, structure2}) ) );
	}

	public void testRetrieveAllUsers() {
		User user1 = manager.createUser("username1", "secret", false);
		User user2 = manager.createUser("username2", "masterkey", true);
		
		assertTrue( userDao.getAll().containsAll( 
				Arrays.asList(new User[] {user1, user2}) ) );
	}

	public void testRetrieveLibraryByName() {
		Folder folder = manager.createLibrary("testLibrary1");
		
		assertNotNull(folder);
		
		assertEquals( folder, 
				      manager.retrieveLibraryByName("testLibrary1") );
	}

	public void testRetrieveStructureByName() throws CDKException {
		Structure structure = manager
                              .createStructure( "CycloOctan", 
                                                TestData.getCycloOctan() );
		assertTrue( manager
				    .retrieveStructureByName("CycloOctan")
				    .contains(structure) );
	}

	public void testRetrieveUserByName() {
		User user = manager.createUser("username", "secret", false);
		
		assertNotNull(user);
		
		assertEquals( user,
				      manager.retrieveUserByUsername("username") );
	}

	public void testUpdateLibrary() {
		Folder folder = manager.createLibrary("testLibrary");
		folder.setName("edited");
		manager.update(folder);
		assertEquals( folder, folderDao.getById(folder.getId()) );
	}

	public void testUpdateUser() {
		User user = manager.createUser("username", "secret", false);
		user.setName("edited");
		manager.update(user);
		assertEquals( user, userDao.getById(user.getId()) );
	}

	public void testUpdateStructure() throws CDKException {
		Structure structure = manager
                             .createStructure( "CycloOctan", 
                                               TestData.getCycloOctan() );
		structure.setName("edited");
		manager.update(structure);
		assertEquals( structure, structureDao.getById(structure.getId()) );
	}
	
	protected String[] getConfigLocations() {
		String path = Structuredb.class.getClassLoader()
		                         .getResource("applicationContext.xml")
		                         .toString();
		
		return new String[] { path };
	}
}

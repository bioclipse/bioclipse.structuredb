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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import testData.TestData;

public class StructuredbInstanceManagerTest 
       extends AbstractAnnotationAwareTransactionalTests  {
	
	protected IStructuredbInstanceManager manager;
	
	protected IFolderDao    folderDao;
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
		folderDao    = (IFolderDao) applicationContext
		                            .getBean("folderDao");
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
	
	private Folder createFolder(String name) {
		Folder folder = new Folder(name);
		manager.insertFolder(folder);
		return folder;
	}
	
	public void testInsertFolder() {
		
		assertTrue( folderDao.getAll().contains(createFolder("testFolder")) );
	}

	private Structure createStructure( String name, 
			                           AtomContainer atomContainer) 
	        throws CDKException {
	
		Structure structure = new Structure( name, atomContainer );
	    manager.insertStructure(structure);
		return structure;
	}
	
	public void testInsertStructure() throws CDKException {
		assertTrue( structureDao
				    .getAll().contains( 
				    		createStructure( "CycloOctan", 
				    				         TestData.getCycloOctan())) );
	}

	private User createUser(String username, String password, boolean sudoer) {
		User user = new User(username, password, sudoer);
		manager.insertUser(user);
		return user;
	}
	
	public void testInsertUser() {

		assertTrue( userDao.getAll()
				    .contains(createUser("another username", "secrest", false)) );
	}

	public void testDeleteLibrary() {
		Folder folder = createFolder("testFolder");
		assertTrue( folderDao.getAll().contains(folder) );
		manager.delete(folder);
		assertFalse( folderDao.getAll().contains(folder) );
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
		Folder folder1 = createFolder("testLibrary1");
		Folder folder2 = createFolder("testLibrary2");
		
		assertTrue( folderDao.getAll().containsAll( 
				Arrays.asList(new Folder[] {folder1, folder2}) ) );
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
		Folder folder = createFolder("testLibrary1");
		
		assertNotNull(folder);
		
		assertEquals( folder, 
				      manager.retrieveLibraryByName("testLibrary1") );
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
				      manager.retrieveUserByUsername("username") );
	}

	public void testUpdateLibrary() {
		Folder folder = createFolder("testLibrary");
		folder.setName("edited");
		manager.update(folder);
		assertEquals( folder, folderDao.getById(folder.getId()) );
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
	
	protected String[] getConfigLocations() {
		String path = Structuredb.class.getClassLoader()
		                         .getResource("applicationContext.xml")
		                         .toString();
		
		return new String[] { path };
	}
}

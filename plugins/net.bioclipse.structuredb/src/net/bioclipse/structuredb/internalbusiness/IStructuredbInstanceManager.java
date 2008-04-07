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

import java.util.List;

import org.openscience.cdk.AtomContainer;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.structuredb.StructuredbDataSource;
import net.bioclipse.structuredb.domain.Library;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

/**
 * @author jonalv
 *
 */
public interface IStructuredbInstanceManager {

	/**
	 * Persists changes in a given library retrieved from the database.
	 * 
	 * @param library
	 */
	public void update(Library library);
	
	/**
	 * Persists changes in a given user retrieved from the database.
	 * 
	 * @param user
	 */
	public void update(User user);
	
	/**
	 * Persists changes in a given user retrieved from the database.
	 * 
	 * @param structure
	 */
	public void update(Structure structure);
	
	/**
	 * Creates a new user with the given username, password and sudoer flag 
	 * and persists it to the database
	 * 
	 * @param username
	 * @param password
	 * @param sudoer
	 * @return the created User
	 */
	public User createUser( String  username,
			                String  password, 
			                boolean sudoer );
	
	/**
	 * Creates a new Library with the given name and persists it to the database
	 * 
	 * @param name
	 * @return the created library
	 */
	public Library createLibrary(String name);
	
	/**
	 * Creates a new Structure from a given <code>ICDKMolecule</code> and 
	 * persists it to the database
	 * 
	 * @param name
	 * @param cdkMolecule
	 * @return the created Structure
	 */
	public Structure createStructure( String name, 
			                          ICDKMolecule cdkMolecule );
	
	/**
	 * Creates a new Structure from a given <code>AtomContainer</code> and 
	 * persists it to the database
	 * 
	 * @param name
	 * @param atomContainer
	 * @return the created Structure
	 */
	public Structure createStructure( String name, 
			                          AtomContainer atomContainer );

	/**
	 * Removes the given library from the database.
	 * 
	 * @param library
	 */
	public void delete(Library library);
	
	/**
	 * Removes the given user from the database
	 * 
	 * @param user
	 */
	public void delete(User user);
	
	/**
	 * Removes the given Structure from the database
	 * 
	 * @param structure
	 */
	public void delete(Structure structure);
	
	/**
	 * @return all structures
	 */
	public List<Structure> retrieveAllStructures();
	
	/**
	 * @return all libraries
	 */
	public List<Library> retrieveAllLibraries();
	
	/**
	 * @return all users
	 */
	public List<User> retrieveAllUsers();
	
	/**
	 * Loads the user with the given username from the database
	 * 
	 * @param username
	 * @return a user or null if no such user exists
	 */
	public User retrieveUserByName(String username);
	
	/**
	 * Loads all structures with a given name from the database
	 * 
	 * @param name
	 * @return all structures with the given name
	 */
	public List<Structure> retrieveStructureByName(String name);
	
	/**
	 * Loads the library with the given name from the database
	 * 
	 * @param name
	 * @return a library or null if no such library exists
	 */
	public Library retrieveLibraryByName(String name);


}

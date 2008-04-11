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
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.structuredb.StructuredbInstance;
import net.bioclipse.structuredb.domain.Folder;
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
	 * @param folder
	 */
	public void update(Folder folder);
	
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
	 * Insert a user into the database
	 * 
	 * @param user to be inserted
	 */
	public void insertUser( User user );
	
	/**
	 * Insert a folder into the database
	 * 
	 * @param folder to be inserted
	 */
	public void insertFolder( Folder folder );
	
	/**
	 * Insert a structure into the database
	 * 
	 * @param structure to be inserted
	 */
	public void insertStructure( Structure structure ); 
	
	/**
	 * Removes the given library from the database.
	 * 
	 * @param folder
	 */
	public void delete(Folder folder);
	
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
	public List<Folder> retrieveAllLibraries();
	
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
	public User retrieveUserByUsername(String username);
	
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
	public Folder retrieveFolderByName(String name);

	/**
	 * @return the logged in user
	 */
	public User getLoggedInUser();
	
	/**
	 * @param user to set as logged in
	 */
	public void setLoggedInUser(User user);
}

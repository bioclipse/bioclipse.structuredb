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
import net.bioclipse.core.domain.BioList;
import net.bioclipse.structuredb.domain.Library;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

public class StructuredbInstanceManager 
	   extends AbstractStructuredbInstanceManager 
	   implements IStructuredbInstanceManager {

	public Library createLibrary(String name) {
		Library library = new Library(name);
		libraryDao.insert(library);
		return library;
	}

	public Structure createStructure(String name, ICDKMolecule cdkMolecule) 
	                 throws BioclipseException {
		
		Structure structure = new Structure(name, cdkMolecule);
		structureDao.insert(structure);
		return structure;
	}

	public Structure createStructure(String name, AtomContainer atomContainer) {
		
		Structure structure = new Structure(name, atomContainer);
		structureDao.insert(structure);
		return structure;
	}

	public User createUser(String username, String password, boolean sudoer) {
		
		User user = new User(username, password, sudoer);
		userDao.insert(user);
		return user;
	}

	public void delete(Library library) {
		libraryDao.delete( library.getId() );
	}

	public void delete(User user) {
		userDao.delete( user.getId() );
	}

	public void delete(Structure structure) {
		structureDao.delete( structure.getId() );
	}

	public List<Library> retrieveAllLibraries() {
		return new BioList<Library>( libraryDao.getAll() );
	}

	public List<Structure> retrieveAllStructures() {
		return new BioList<Structure>( structureDao.getAll() );
	}

	public List<User> retrieveAllUsers() {
		return new BioList<User>( userDao.getAll() );
	}

	public Library retrieveLibraryByName(String name) {
		return libraryDao.getByName(name);
	}

	public List<Structure> retrieveStructureByName(String name) {
		return structureDao.getByName(name);
	}

	public User retrieveUserByUsername(String username) {
		return userDao.getByUserName(username);
	}

	public void update(Library library) {
		libraryDao.update(library);
	}

	public void update(User user) {
		userDao.update(user);
	}

	public void update(Structure structure) {
		structureDao.update(structure);
	}

}

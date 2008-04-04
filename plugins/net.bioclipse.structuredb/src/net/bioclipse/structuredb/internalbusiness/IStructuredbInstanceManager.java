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

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.structuredb.StructuredbDataSource;
import net.bioclipse.structuredb.domain.Library;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

public interface IStructuredbInstanceManager {

	/**
	 * Get all libraries in the given structure database
	 * 
	 * @param database database to look in
	 * @return all libraries in the database
	 */
	public List<Library> getAllLibraries( StructuredbDataSource database );
	
	public void update(Library library);
	
	public void update(User user);
	
	public void update(Structure structure);
	
	public User createUser( String  username,
			                String  password, 
			                boolean sudoer );
	
	public Library createLibrary(String name);
	
	public Structure createStructure( String name, 
			                          ICDKMolecule cdkMolecule );
}

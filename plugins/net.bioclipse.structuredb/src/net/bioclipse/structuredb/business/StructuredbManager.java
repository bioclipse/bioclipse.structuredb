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
package net.bioclipse.structuredb.business;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.persistency.tables.TableCreator;

public class StructuredbManager implements IStructuredbManager {

	private Map<String, IStructuredbInstanceManager> instances 
		= new HashMap<String, IStructuredbInstanceManager>();
	
	public void createLocalInstance(String databaseName)
		throws IllegalArgumentException {

		if(instances.containsKey(databaseName)) {
			throw new IllegalArgumentException( "Database name already used: " 
					                            + databaseName );
		}
//		String path = Platform.getWS() + File.separator
//		HsqldbUtil.getInstance().addDatabase(path, name)
//		TableCreator.INSTANCE.createTables(url);
	}
	
	public Folder createFolder(String databaseName, String folderName)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Structure createStructure(String databaseName, String moleculeName,
			ICDKMolecule cdkMolecule) throws BioclipseException {
		// TODO Auto-generated method stub
		return null;
	}

	public User createUser(String databaseName, String username,
			String password, boolean sudoer) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeLocalInstance(String databaseName) {
		// TODO Auto-generated method stub

	}

	public List<Folder> retrieveAllFolders(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Structure> retrieveAllStructures(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> retrieveAllUser(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Structure retrieveFolderByName(String databaseName, String folderName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Structure> retrieveStructureByName(String databaseName,
			String structureName) {
		// TODO Auto-generated method stub
		return null;
	}

	public User retrieveUserByName(String databaseName, String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

}

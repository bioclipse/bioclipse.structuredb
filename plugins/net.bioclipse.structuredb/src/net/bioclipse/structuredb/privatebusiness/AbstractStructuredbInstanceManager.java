/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.bioclipse.structuredb.privatebusiness;

import net.bioclipse.structuredb.persistency.dao.ILibraryDao;
import net.bioclipse.structuredb.persistency.dao.IStructureDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

public abstract class AbstractStructuredbInstanceManager 
                      implements IStructuredbInstanceManager {

	protected ILibraryDao   libraryDAO;
	protected IStructureDao structureDAO;
	protected IUserDao      userDAO;
	
	public AbstractStructuredbInstanceManager() {
		
	}

	public ILibraryDao getLibraryDAO() {
		return libraryDAO;
	}

	public void setLibraryDAO(ILibraryDao libraryDAO) {
		this.libraryDAO = libraryDAO;
	}

	public IStructureDao getStructureDAO() {
		return structureDAO;
	}

	public void setStructureDAO(IStructureDao structureDAO) {
		this.structureDAO = structureDAO;
	}

	public IUserDao getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(IUserDao userDAO) {
		this.userDAO = userDAO;
	}
}

/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import net.bioclipse.structuredb.persistency.dao.ILibraryDao;
import net.bioclipse.structuredb.persistency.dao.IStructureDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

public abstract class AbstractStructuredbInstanceManager 
                      implements IStructuredbInstanceManager {

	protected ILibraryDao   libraryDao;
	protected IStructureDao structureDao;
	protected IUserDao      userDao;
	
	public AbstractStructuredbInstanceManager() {
		
	}

	public ILibraryDao getLibraryDao() {
		return libraryDao;
	}

	public void setLibraryDao(ILibraryDao libraryDao) {
		this.libraryDao = libraryDao;
	}

	public IStructureDao getStructureDao() {
		return structureDao;
	}

	public void setStructureDao(IStructureDao structureDao) {
		this.structureDao = structureDao;
	}

	public IUserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(IUserDao userDao) {
		this.userDao = userDao;
	}
}

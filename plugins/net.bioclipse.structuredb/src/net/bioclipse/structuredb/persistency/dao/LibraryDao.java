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
package net.bioclipse.structuredb.persistency.dao;

import net.bioclipse.structuredb.domain.Library;

/**
 * The libraryDao persists and loads libraries
 * 
 * @author jonalv
 *
 */
public class LibraryDao extends GenericDao<Library> {

	public LibraryDao() {
		super(Library.class);
	}

	@Override
	public void insert(Library library) {
		getSqlMapClientTemplate().update( "BaseObject.insert", library );
		getSqlMapClientTemplate().update( "Library.insert",    library );
	}
	
	@Override
	public void update(Library library) {
		getSqlMapClientTemplate().update( "BaseObject.update", library );
		getSqlMapClientTemplate().update( "Library.update",    library );
	}
}

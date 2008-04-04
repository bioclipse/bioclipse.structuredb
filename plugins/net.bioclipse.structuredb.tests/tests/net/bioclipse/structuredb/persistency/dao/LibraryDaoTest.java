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
import net.bioclipse.structuredb.domain.Structure;

/**
 * @author jonalv
 *
 */
public class LibraryDaoTest extends GenericDaoTest<Library> {

	public LibraryDaoTest() {
		super(Library.class);
	}
	
	@Override
	public void testDelete() {
		
		IStructureDao structureDao 
		= (IStructureDao) applicationContext.getBean("structureDao");
		
		Structure structure = new Structure();
		structure.setCreator(testUser);
		structure.setLastEditor(testUser);
		structureDao.insert(structure);
		object1.addStructure(structure);
		dao.update(object1);
		assertNotNull(structure.getLibrary());
		super.testDelete();
		
		structure = structureDao.getById( structure.getId() );
		assertNull( structure.getLibrary() );
	}
}

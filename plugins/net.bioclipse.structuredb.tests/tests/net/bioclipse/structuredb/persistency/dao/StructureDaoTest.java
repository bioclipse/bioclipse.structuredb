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

import java.util.List;

import org.openscience.cdk.exception.CDKException;

import testData.TestData;
import net.bioclipse.structuredb.domain.Library;
import net.bioclipse.structuredb.domain.Structure;

public class StructureDaoTest extends GenericDaoTest<Structure> {

	public StructureDaoTest() {
		super(Structure.class);
	}
	
	public void testPersistStructureWithLibrary() {
		
		Library library = new Library();
		ILibraryDao libraryDao = (ILibraryDao) applicationContext.getBean("libraryDao");
		setCreatorAndEditor(library);
		libraryDao.insert(library);
		
		Structure structure = new Structure();
		structure.setLibrary(library);
		setCreatorAndEditor(structure);
		dao.insert(structure);
		
		Structure loaded = dao.getById( structure.getId() );
		assertNotNull("The lodaded object shuold not be null", loaded);
		assertNotSame(structure, loaded);
		assertTrue( structure.hasValuesEqualTo(loaded) );
		assertTrue( structure.getLibrary().hasValuesEqualTo(loaded.getLibrary()) );
	}
	
	@Override
	public void testUpdate() {
		super.testUpdate();
		
		Library library = new Library();
		ILibraryDao libraryDao = (ILibraryDao) applicationContext.getBean("libraryDao");
		setCreatorAndEditor(library);
		libraryDao.insert(library);
		
		Structure structure = new Structure();
		structure.setLibrary(library);
		setCreatorAndEditor(structure);
		dao.insert(structure);
		
		Structure loaded = dao.getById( structure.getId() );
		structure.setName("edited");
		dao.update(structure);
		Structure updated = dao.getById( structure.getId() );
		assertTrue( structure.hasValuesEqualTo(updated) );
	}
	
	public void testGetByName() throws CDKException {
		Structure structure1 = new Structure( "structure",
				                              TestData.getCycloOctan() );
		Structure structure2 = new Structure( "structure", 
				                              TestData.getCycloPropane() );
		dao.insert(structure1);
		dao.insert(structure2);
		
		List<Structure> saved = ((IStructureDao)dao).getByName("structure");
		assertTrue( saved.contains(structure1) );
		assertTrue( saved.contains(structure2) );
	}
}

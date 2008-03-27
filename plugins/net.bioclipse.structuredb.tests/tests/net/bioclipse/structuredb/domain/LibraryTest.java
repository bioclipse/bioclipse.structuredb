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
package net.bioclipse.structuredb.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openscience.cdk.AtomContainer;

import testData.TestData;

/**
 * @author jonalv
 *
 */
public class LibraryTest {

	@Test
	public void testHasValuesEqualsTo() {

		Structure s1 = new Structure();
		Structure s2 = new Structure();
		s2.setName("s2");
		
		Library library1 = new Library();
		library1.addStructure(s1);
		Library library2 = new Library(library1);
		Library library3 = new Library();
		library3.addStructure(s2);
		
		assertTrue(  library1.hasValuesEqualTo(library2) );
		assertFalse( library1.hasValuesEqualTo(library3) );
	}
	
	@Test
	public void testDoubleReferences() {

		Structure structure = new Structure();
		
		Library library = new Library();
		
		library.addStructure( structure );
	
		assertTrue( library.getStructures().contains(structure) );
		assertTrue( structure.getLibrary() == library );
		
		library.removeStructure( structure );
		
		assertFalse( library.getStructures().contains(structure) );
		assertFalse( structure.getLibrary() == library );
	}
}

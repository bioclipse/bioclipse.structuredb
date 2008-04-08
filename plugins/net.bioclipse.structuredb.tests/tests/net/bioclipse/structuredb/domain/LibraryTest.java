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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		
		Folder library1 = new Folder();
		library1.addStructure(s1);
		Folder library2 = new Folder(library1);
		Folder library3 = new Folder();
		library3.addStructure(s2);
		
		assertTrue(  library1.hasValuesEqualTo(library2) );
		assertFalse( library1.hasValuesEqualTo(library3) );
	}
	
	@Test
	public void testDoubleReferences() {

		Structure structure = new Structure();
		
		Folder folder = new Folder();
		
		folder.addStructure( structure );
	
		assertTrue( folder.getStructures().contains(structure) );
		assertTrue( structure.getFolder() == folder );
		
		folder.removeStructure( structure );
		
		assertFalse( folder.getStructures().contains(structure) );
		assertFalse( structure.getFolder() == folder );
	}
}

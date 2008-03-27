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


public class BaseObjectTest {

	@Test
	public void testHasValuesEqualTo() {

		User creator1 = new User();
		User creator2 = new User();
		
		BaseObject baseObject1 = new BaseObject();
		baseObject1.setCreator(creator1);
		baseObject1.setLastEditor(creator1);
		BaseObject baseObject2 = new BaseObject(baseObject1);
		
		BaseObject baseObject3 = new BaseObject();
		baseObject3.setCreator(creator2);
		baseObject3.setCreator(creator2);
		
		
		assertTrue(  baseObject1.hasValuesEqualTo(baseObject2) );
		assertFalse( baseObject1.hasValuesEqualTo(baseObject3) );
	}
	
	@Test
	public void testDoubleReferences() {
		BaseObject baseObject = new BaseObject();
		
		User user = new User();
		
		user.addCreatedBaseObject( baseObject );
	
		assertTrue( user.getCreatedBaseObjects().contains(baseObject) );
		assertTrue( baseObject.getCreator() == user );
		
		user.removeCreatedBaseObject( baseObject );
		
		assertFalse( user.getCreatedBaseObjects().contains(baseObject) );
		assertFalse( baseObject.getCreator() == user );
	}
}

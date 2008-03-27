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


public class UserTest {

	@Test
	public void testHasValuesEqualTo() {
		
		User user1 = new User( "full username", "username", "password");
		User user2 = new User(user1);
		User user3 = new User( "full username", "another username", "password");
		
		assertTrue(  user1.hasValuesEqualTo(user2) );
		assertFalse( user1.hasValuesEqualTo(user3) );
	}
	
	@Test
	public void testDoubleReferences() {
		Structure structure = new Structure();
		
		User user = new User();
		
		user.addCreatedBaseObject( structure );
	
		assertTrue( user.getCreatedBaseObjects().contains(structure) );
		assertTrue( structure.getCreator() == user );
	}
}

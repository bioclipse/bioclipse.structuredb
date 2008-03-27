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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.bioclipse.hsqldb.Activator;
import net.bioclipse.structuredb.domain.User;

/**
 * @author jonalv
 *
 */
public class UserDaoTest extends GenericDaoTest<User> {

	public UserDaoTest() {
		super(User.class);
	}
	
	public void testPersistGod() {
		
		IUserDao userDao = (IUserDao) dao;
		User loaded = userDao.getById( god.getId() );
		
		assertNotNull( "The loaded object should not be null", loaded );
		assertTrue( god.hasValuesEqualTo(loaded) );
	}
}

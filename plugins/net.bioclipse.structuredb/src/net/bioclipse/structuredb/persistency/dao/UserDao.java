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

import net.bioclipse.structuredb.domain.User;

/**
 * The userDao perists and loads libraries
 * 
 * @author jonalv
 *
 */
public class UserDao extends GenericDao<User> implements IUserDao {

	public UserDao() {
		super(User.class);
	}

	@Override
	public void insert(User user) {
		getSqlMapClientTemplate().insert( "BaseObject.insert", user );
		getSqlMapClientTemplate().insert( "User.insert",       user );
	}

	public void persistGodObject(User user) {
		getSqlMapClientTemplate().insert( "BaseObject.insertGod", user );
		getSqlMapClientTemplate().insert( "User.insert",          user );
	}
	
	@Override
	public void update(User user) {
		getSqlMapClientTemplate().update( "BaseObject.update", user );
		getSqlMapClientTemplate().update( "User.update",       user );
	}
}

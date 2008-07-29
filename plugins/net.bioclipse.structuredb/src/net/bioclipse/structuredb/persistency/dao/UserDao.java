/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
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
        if(user.getCreator() != null)
            getSqlMapClientTemplate().insert( "BaseObject.insert", user );
        else 
            getSqlMapClientTemplate().insert( 
                    "BaseObject.insertWithoutAuditInfo", user );
        getSqlMapClientTemplate().insert( "User.insert",       user );
    }
    
    @Override
    public void update(User user) {
        getSqlMapClientTemplate().update( "BaseObject.update", user );
        getSqlMapClientTemplate().update( "User.update",       user );
    }

    public User getByUserName(String username) {
        return (User)getSqlMapClientTemplate()
               .queryForObject( "User.getByUsername", username );
    }
}

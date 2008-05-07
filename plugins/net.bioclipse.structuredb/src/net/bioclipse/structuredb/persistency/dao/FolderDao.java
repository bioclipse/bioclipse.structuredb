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

import java.sql.SQLException;

import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;

/**
 * The folderDao persists and loads libraries
 * 
 * @author jonalv
 *
 */
public class FolderDao extends GenericDao<Folder> implements IFolderDao {

    public FolderDao() {
        super(Folder.class);
    }

    @Override
    public void insert(Folder folder) {
        getSqlMapClientTemplate().update( "BaseObject.insert", folder );
        getSqlMapClientTemplate().update( "Folder.insert",    folder );
    }
    
    @Override
    public void update(Folder folder) {
        getSqlMapClientTemplate().update( "BaseObject.update", folder );
        getSqlMapClientTemplate().update( "Folder.update",     folder );
    }

    public Folder getByName(String name) {
        return (Folder)getSqlMapClientTemplate()
               .queryForObject( "Folder.getByName", name);
    }
}

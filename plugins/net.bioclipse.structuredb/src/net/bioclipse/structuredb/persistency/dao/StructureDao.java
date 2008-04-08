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

import net.bioclipse.structuredb.domain.Structure;

/**
 * The StructureDao persists and loads structures
 * 
 * @author jonalv
 *
 */
public class StructureDao extends GenericDao<Structure> implements IStructureDao {

	public StructureDao() {
		super(Structure.class);
	}

	@Override
	public void insert(Structure structure) {
		
		getSqlMapClientTemplate().update( "BaseObject.insert", structure );
		
		//TODO: Figure out a better way to do this:
		if( structure.getFolder() != null ) {
			getSqlMapClientTemplate().update( type.getSimpleName() + ".insert", structure );
		}
		else {
			getSqlMapClientTemplate().update( type.getSimpleName() + ".insertWithoutFolder", structure );
		}
	}
	
	@Override
	public void update(Structure structure) {
		if(structure.getFolder() == null) {
			getSqlMapClientTemplate().update( "Structure-without-folder.update", structure );
		}
		else {
			getSqlMapClientTemplate().update( "Structure.update",  structure );
		}
		getSqlMapClientTemplate().update( "BaseObject.update", structure );
	}

	@SuppressWarnings("unchecked")
	public List<Structure> getByName(String name) {
		return getSqlMapClientTemplate().queryForList( "Structure.getByName" );
	}
}
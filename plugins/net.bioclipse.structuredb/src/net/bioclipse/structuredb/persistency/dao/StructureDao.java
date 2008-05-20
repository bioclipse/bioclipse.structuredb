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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.bioclipse.structuredb.domain.Structure;

/**
 * The StructureDao persists and loads structures
 * 
 * @author jonalv
 *
 */
public class StructureDao extends GenericDao<Structure> 
                          implements IStructureDao {

    public StructureDao() {
        super(Structure.class);
    }

    @Override
    public void insert(Structure structure) {
        
        getSqlMapClientTemplate().update( "BaseObject.insert", structure );
        
        //TODO: Figure out a better way to do this:
        if( structure.getFolder() != null ) {
            getSqlMapClientTemplate()
            .update( type.getSimpleName() + ".insert", structure );
        }
        else {
            getSqlMapClientTemplate()
            .update( type.getSimpleName() + ".insertWithoutFolder", structure );
        }
    }
    
    @Override
    public void update(Structure structure) {
        if(structure.getFolder() == null) {
            getSqlMapClientTemplate()
            .update( "Structure-without-folder.update", structure );
        }
        else {
            getSqlMapClientTemplate().update( "Structure.update",  structure );
        }
        getSqlMapClientTemplate().update( "BaseObject.update", structure );
    }

    @SuppressWarnings("unchecked")
    public List<Structure> getByName(String name) {
        return getSqlMapClientTemplate()
               .queryForList( "Structure.getByName", name );
    }

    public Iterator<Structure> allStructuresIterator() {
        
        return new StructureIterator( getSqlMapClient() );
    }
    
    private class StructureIterator implements Iterator<Structure> {
        
        private SqlMapClient sqlMapClient;
        private Structure nextStructure = null;
        private int skip = 0;
        
        public StructureIterator( SqlMapClient sqlMapClient ) {
            this.sqlMapClient = sqlMapClient;
        }
        
        @SuppressWarnings("unchecked")
        public boolean hasNext() {

            try {
                List<Structure> result = (List<Structure>)sqlMapClient
                                         .queryForList( "Structure.getAll", 
                                                        null, 
                                                        skip, 
                                                        1 );
                if(result.size() != 1) {
                    nextStructure = null;
                    return false;
                }
                else {
                    nextStructure = result.get( 0 );
                    return true;
                }
            } 
            catch ( SQLException e ) {
                throw new RuntimeException(e);
            }
        }

        public Structure next() {
            if( nextStructure != null ) {
                skip++;
                return nextStructure;
            }
            if( hasNext() ) {
                skip++;
                return nextStructure;
            }
            throw new IllegalStateException("There is no next structure");
        }

        public void remove() {
            throw new UnsupportedOperationException();            
        }
    }

    public void insertInFolder( Structure structure, String folderId ) {

        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          structure );
        getSqlMapClientTemplate().update( "Structure.insertWithoutFolder", 
                                          structure );
        Map<String, String> params = new HashMap<String, String>();
        params.put( "structureId", structure.getId() );
        params.put( "folderId", folderId );
        getSqlMapClientTemplate().update( "Structure.setFolder", params );
    };
}
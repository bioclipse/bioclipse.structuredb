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

import java.sql.SQLException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.bioclipse.structuredb.domain.Annotation;
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
    public void insert( final Structure structure ) {
        
        getSqlMapClientTemplate().update( "BaseObject.insert", structure );
        getSqlMapClientTemplate().update( type.getSimpleName() + ".insert", 
                                          structure );
        fixStructureLabel(structure);
    }
    
    private void fixStructureLabel( final Structure structure ) {

        getSqlMapClientTemplate().delete( "Structure.deleteLabelCoupling", 
                                          structure );
        for( final Annotation l : structure.getLabels() ) {
            Map<String, String> params = new HashMap<String, String>() {
                {
                    put( "labelId",     l.getId()         );
                    put( "structureId", structure.getId() );
                }
            };
            if ( (Integer) getSqlMapClientTemplate()
                .queryForObject( "StructureLabel.hasConnection", 
                                 params ) == 0 ) {
                getSqlMapClientTemplate().update( "StructureLabel.connect", 
                                                  params );
            }
        }
    }

    @Override
    public void update(Structure structure) {
        getSqlMapClientTemplate().update( "Structure.update",  structure );
        getSqlMapClientTemplate().update( "BaseObject.update", structure );
        fixStructureLabel( structure );
    }

    @SuppressWarnings("unchecked")
    public List<Structure> getByName(String name) {
        return getSqlMapClientTemplate()
               .queryForList( "Structure.getByName", name );
    }

    public Iterator<Structure> allStructuresIterator() {
        
        return new StructureIterator( getSqlMapClient(), "Structure.getAll" );
    }
    
    private class StructureIterator implements Iterator<Structure> {
        
        private SqlMapClient sqlMapClient;
        private Structure nextStructure = null;
        private int skip = 0;
        private String sqlMapId;
        private Object queryParam = null;
        
        public StructureIterator( SqlMapClient sqlMapClient,
                                  String sqlMapId ) {
            this.sqlMapClient = sqlMapClient;
            this.sqlMapId = sqlMapId;
        }
        
        public StructureIterator( SqlMapClient sqlMapClient, 
                                  String sqlMapId,
                                  Object queryParam ) {
            this.sqlMapClient = sqlMapClient;
            this.sqlMapId = sqlMapId;
            this.queryParam = queryParam;
        }

        @SuppressWarnings("unchecked")
        public boolean hasNext() {

            try {
                List<Structure> result = (List<Structure>)sqlMapClient
                                         .queryForList( sqlMapId, 
                                                        queryParam, 
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

    public void insertWithLabel( Structure structure, String labelId ) {

        getSqlMapClientTemplate().update( "BaseObject.insert", structure );
        getSqlMapClientTemplate().update( "Structure.insert",  structure );

        Map<String, String> params = new HashMap<String, String>();
        params.put( "structureId", structure.getId() );
        params.put( "labelId", labelId );
        getSqlMapClientTemplate().update( "StructureLabel.connect", params );
    }

    public int numberOfStructures() {
        
        return (Integer) getSqlMapClientTemplate()
                         .queryForObject( "Structure.numberOf" );
    }

    public Iterator<Structure> fingerPrintSubsetSearch( byte[] fingerPrint ) {

        Map<String, byte[]> paramaterMap = new HashMap<String, byte[]>();
        paramaterMap.put( "param", fingerPrint );
        return new StructureIterator( getSqlMapClient(), 
                                      "Structure.fingerPrintSubsetSearch", 
                                      paramaterMap );
    }

    public int numberOfFingerprintSubstructureMatches(byte[] fingerPrint) {
        Map<String, byte[]> paramaterMap = new HashMap<String, byte[]>();
        paramaterMap.put( "param", fingerPrint );
        return (Integer) getSqlMapClientTemplate().queryForObject( 
            "Structure.numberOfFingerprintSubstructureMatches", 
            paramaterMap );
    };
}
/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistency.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;

/**
 * The DBMoleculeDao persists and loads structures
 * 
 * @author jonalv
 *
 */
public class DBMoleculeDao extends GenericDao<DBMolecule> 
                           implements IDBMoleculeDao {

    public DBMoleculeDao() {
        super(DBMolecule.class);
    }

    @Override
    public void insert( final DBMolecule dBMolecule ) {
        
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          dBMolecule );
        getSqlMapClientTemplate()
            .update( type.getSimpleName() + ".insert", 
                     dBMolecule );
        fixStructureAnnotation(dBMolecule);
    }
    
    private void fixStructureAnnotation( final DBMolecule dBMolecule ) {

        getSqlMapClientTemplate()
            .delete( "DBMolecule.deleteAnnotationCoupling", 
                     dBMolecule );
        for ( final Annotation a : dBMolecule.getAnnotations() ) {
            Map<String, String> params = new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;

                {
                    put( "annotationId", a.getId()          );
                    put( "dBMoleculeId", dBMolecule.getId() );
                }
            };
            if ( (Integer) getSqlMapClientTemplate().queryForObject( 
                     "DBMoleculeAnnotation.hasConnection", 
                      params ) 
                 == 0 ) {
                
                getSqlMapClientTemplate()
                    .update( "DBMoleculeAnnotation.connect", 
                             params );
            }
        }
    }

    @Override
    public void update(DBMolecule dBMolecule) {
        getSqlMapClientTemplate().update( "DBMolecule.update",  
                                          dBMolecule );
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          dBMolecule );
        fixStructureAnnotation( dBMolecule );
    }

    @SuppressWarnings("unchecked")
    public List<DBMolecule> getByName(String name) {
        return getSqlMapClientTemplate()
               .queryForList( "DBMolecule.getByName", name );
    }

    public Iterator<DBMolecule> allStructuresIterator() {
        
        return new StructureIterator( getSqlMapClient(), 
                                      "DBMolecule.getAll" );
    }
    
    private class StructureIterator implements Iterator<DBMolecule> {
        
        private SqlMapClient sqlMapClient;
        private DBMolecule nextStructure = null;
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
                List<DBMolecule> result = (List<DBMolecule>)sqlMapClient
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

        public DBMolecule next() {
            if( nextStructure != null ) {
                skip++;
                return nextStructure;
            }
            if( hasNext() ) {
                skip++;
                return nextStructure;
            }
            throw new IllegalStateException( 
                "There is no next structure" );
        }

        public void remove() {
            throw new UnsupportedOperationException();            
        }
    }

    public void insertWithAnnotation( DBMolecule dBMolecule, 
                                      String annotationId ) {

        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          dBMolecule );
        getSqlMapClientTemplate().update( "DBMolecule.insert",  
                                          dBMolecule );

        Map<String, String> params = new HashMap<String, String>();
        params.put( "dBMoleculeId",  dBMolecule.getId() );
        params.put( "annotationId", annotationId );
        getSqlMapClientTemplate().update( "DBMoleculeAnnotation.connect", 
                                          params );
    }

    public int numberOfStructures() {
        
        return (Integer) getSqlMapClientTemplate()
                         .queryForObject( "DBMolecule.numberOf" );
    }

    public Iterator<DBMolecule> fingerPrintSubsetSearch( 
                                   byte[] fingerPrint ) {

        Map<String, byte[]> paramaterMap = new HashMap<String, byte[]>();
        paramaterMap.put( "param", fingerPrint );
        return 
            new StructureIterator( getSqlMapClient(), 
                                   "DBMolecule.fingerPrintSubsetSearch", 
                                   paramaterMap );
    }

    public int numberOfFingerprintSubstructureMatches( 
                   byte[] fingerPrint ) {
        
        Map<String, byte[]> paramaterMap = new HashMap<String, byte[]>();
        paramaterMap.put( "param", fingerPrint );
        return (Integer) getSqlMapClientTemplate().queryForObject( 
            "DBMolecule.numberOfFingerprintSubstructureMatches", 
            paramaterMap );
    };
    
    @SuppressWarnings("unchecked")
    public DBMolecule getMoleculeAtIndexInLabel( TextAnnotation label, 
                                                 int index ) {
        List<DBMolecule> results 
            = getSqlMapClientTemplate()
                  .queryForList( "DBMolecule.atIndexInLabel", 
                                 label.getId(), 
                                 index, 
                                 1 );
        return results.size() == 1 ? results.get( 0 ) 
                                   : null;
    }

    public int getNumberOfMoleculesWithLabel( TextAnnotation label ) {
        return (Integer) getSqlMapClientTemplate()
                         .queryForObject( 
                             "DBMolecule.numberOfMoleculesWithLabel", 
                             label.getId() );
    }
}
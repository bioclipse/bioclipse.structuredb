/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.persistency.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import net.bioclipse.core.util.TimeCalculater;
import net.bioclipse.structuredb.FileStoreKeeper;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;

/**
 * The annotationDao persists and loads libraries
 * 
 * @author jonalv
 *
 */
public abstract class AnnotationDao<T extends Annotation> extends GenericDao<T> 
                                      implements IAnnotationDao<T> {

    public AnnotationDao(Class<T> type) {
        super( type );
    }

    @Override
    public void insert(T annotation) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(T annotation) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.update", 
                                          annotation );
        fixStructureAnnotation( annotation );
    }
    
    private void fixStructureAnnotation( final T annotation ) {

        getSqlMapClientTemplate()
            .delete( "Annotation.deleteDBMoleculeCoupling", annotation );
        
        for ( final DBMolecule s : annotation.getDBMolecules() ) {
            Map<String, String> params = new HashMap<String, String>() {

                private static final long serialVersionUID = 1L;

                {
                    put( "annotationId", annotation.getId() );
                    put( "dBMoleculeId", s.getId()          );
                }
            };
            if ( (Integer) getSqlMapClientTemplate()
                 .queryForObject( "DBMoleculeAnnotation.hasConnection", 
                                 params ) == 0 ) {
                getSqlMapClientTemplate()
                    .update( "DBMoleculeAnnotation.connect", params );
            }
        }
    }

    public Annotation getByName(String name) {
        return (Annotation)getSqlMapClientTemplate()
               .queryForObject( "Annotation.getByName", name);
    }
    
    public void deleteWithStructures( Annotation annotation, 
                                      IProgressMonitor monitor ) {
        if ( monitor == null ) {
            monitor = new NullProgressMonitor();
        }
        try {
            int ticks = (int) 1E6;
            monitor.beginTask( "Deleting Annotation", ticks );
            
            int molecules 
                = (Integer) getSqlMapClientTemplate().queryForObject(
                                "DBMolecule.numberOfMoleculesWithLabel",
                                annotation.getId() );
            monitor.worked( (int) (ticks * 0.01) );
            
            List<String> fileStoreKeys = getSqlMapClientTemplate().queryForList(
                                             "Annotation.fileStoreKeys",
                                             annotation.getId() );
            List<String> dbMoleculeIds = getSqlMapClientTemplate().queryForList(
                                             "Annotation.moleculeIds",
                                             annotation.getId() );
            monitor.worked( (int) (ticks * 0.04) );
            
            int tick = (int) ( (ticks - ticks * 0.05) / dbMoleculeIds.size() );
            assert fileStoreKeys.size() == dbMoleculeIds.size();
            int size = dbMoleculeIds.size();
            long startTime = System.currentTimeMillis();
            for ( int i = 0; i < fileStoreKeys.size(); i++ ) {
                String key        = fileStoreKeys.get( i );
                String moleculeId = dbMoleculeIds.get( i );
                
                FileStoreKeeper.FILE_STORE.delete( UUID.fromString( key ) );
                getSqlMapClientTemplate().delete( "BaseObject.delete", 
                                                  moleculeId );
                monitor.worked( tick );
                if ( (i+1) % 50 == 0 ) {
                    monitor.subTask( "Molecules deleted: " + (i+1) + "/" 
                                     + dbMoleculeIds.size() + " ( " 
                                     + TimeCalculater.generateTimeRemainEst( 
                                         startTime, 
                                         i+1, 
                                         size ) + ")" );
                }
                if ( monitor.isCanceled() ) {
                    throw new OperationCanceledException();
                }
            }
            getSqlMapClientTemplate().delete( 
                "Annotation.deleteDBMoleculeCoupling", 
                annotation );
            getSqlMapClientTemplate().delete( "BaseObject.delete", 
                                              annotation.getId() );
        }
        finally {
            monitor.done();
        }
    }   
}

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

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.Structure;

/**
 * The annotationDao persists and loads libraries
 * 
 * @author jonalv
 *
 */
public class AnnotationDao extends GenericDao<Annotation> 
                           implements IAnnotationDao {

    public AnnotationDao() {
        super(Annotation.class);
    }

    @Override
    public void insert(Annotation annotation) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(Annotation annotation) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.update", 
                                          annotation );
        fixStructureAnnotation( annotation );
    }
    
    private void fixStructureAnnotation( final Annotation annotation ) {

        getSqlMapClientTemplate()
        .delete( "Annotation.deleteStructureCoupling", annotation );
        
        for ( final Structure s : annotation.getStructures() ) {
            Map<String, String> params = new HashMap<String, String>() {

                private static final long serialVersionUID = 1L;

                {
                    put( "annotationId", annotation.getId() );
                    put( "structureId",  s.getId()          );
                }
            };
            if ( (Integer) getSqlMapClientTemplate()
                 .queryForObject( "StructureAnnotation.hasConnection", 
                                 params ) == 0 ) {
                getSqlMapClientTemplate()
                .update( "StructureAnnotation.connect", params );
            }
        }
    }

    public Annotation getByName(String name) {
        return (Annotation)getSqlMapClientTemplate()
               .queryForObject( "Annotation.getByName", name);
    }
}

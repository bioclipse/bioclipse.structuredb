/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
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


import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.TextAnnotation;


/**
 * @author jonalv
 *
 */
public class TextAnnotationDao extends GenericDao<TextAnnotation> implements
        ITextAnnotationDao {

    public TextAnnotationDao() {
        super( TextAnnotation.class );
    }

    @Override
    public void insert(TextAnnotation annotation) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.insert",
                                          annotation );
        getSqlMapClientTemplate().update( "TextAnnotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(TextAnnotation annotation) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          annotation );
        getSqlMapClientTemplate().update( "Annotation.update", 
                                          annotation );
        getSqlMapClientTemplate().update( "TextAnnotation.update", 
                                          annotation );
    }    
}

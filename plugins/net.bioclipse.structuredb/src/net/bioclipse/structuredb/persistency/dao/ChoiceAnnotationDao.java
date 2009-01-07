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

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.ChoiceProperty;


/**
 * @author jonalv
 *
 */
public class ChoiceAnnotationDao 
       extends AnnotationDao<ChoiceAnnotation> 
       implements IChoiceAnnotationDao {

    private IChoicePropertyDao choicePropertyDao;
    
    public ChoiceAnnotationDao() {
        super( ChoiceAnnotation.class );
    }
    
    @Override
    public void insert(ChoiceAnnotation annotation) {
        super.insert( annotation );
        insertOrUpdateProperty( annotation.getProperty() );
        getSqlMapClientTemplate().update( "ChoiceAnnotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(ChoiceAnnotation annotation) {
        super.update( annotation );
        insertOrUpdateProperty( annotation.getProperty() );
        getSqlMapClientTemplate().update( "ChoiceAnnotation.update", 
                                          annotation );
    }    

    private void insertOrUpdateProperty( ChoiceProperty property ) {
        ChoiceProperty loaded = choicePropertyDao.getById( property.getId() );
        if (loaded == null) {
            choicePropertyDao.insert( property );
        }
        else {
            if ( !property.hasValuesEqualTo( loaded ) ) {
                choicePropertyDao.update( property );
            }
        }
    }

    public void setChoicePropertyDao( IChoicePropertyDao choicePropertyDao ) {
        this.choicePropertyDao = choicePropertyDao;
    }

    public IChoicePropertyDao getChoicePropertyDao() {
        return choicePropertyDao;
    }
}

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

import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.PropertyChoice;
import net.bioclipse.structuredb.domain.RealNumberProperty;


/**
 * @author jonalv
 *
 */
public class ChoicePropertyDao
       extends GenericDao<ChoiceProperty> 
       implements IChoicePropertyDao {

    private IPropertyChoiceDao propertyChoiceDao;
    
    public ChoicePropertyDao() {
        super( ChoiceProperty.class );
    }
    
    private void persistRelatedPropertyChoices( ChoiceProperty property ) {
        for ( PropertyChoice p : property.getPropertyChoices() ) {
            PropertyChoice loaded = propertyChoiceDao.getById( p.getId() );
            if ( loaded == null ) {
                propertyChoiceDao.insert( p );
            }
            else if ( !p.hasValuesEqualTo( loaded ) ) {
                propertyChoiceDao.update( p );
            }
        }
    }
    
    @Override
    public void insert(ChoiceProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          property );
        getSqlMapClientTemplate().update( "ChoiceProperty.insert",
                                          property );
        persistRelatedPropertyChoices( property );
    }
    
    @Override
    public void update(ChoiceProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          property );
        getSqlMapClientTemplate().update( "ChoiceProperty.update", 
                                          property );
        persistRelatedPropertyChoices( property );
    }

    public void setPropertyChoiceDao( IPropertyChoiceDao propertyChoiceDao ) {

        this.propertyChoiceDao = propertyChoiceDao;
    }

    public IPropertyChoiceDao getPropertyChoiceDao() {

        return propertyChoiceDao;
    }

    public ChoiceProperty getByName( String name ) {

        return (ChoiceProperty)getSqlMapClientTemplate().queryForObject( 
               "ChoiceProperty.getByName", name );
    }    
}

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

import net.bioclipse.structuredb.domain.PropertyChoice;


/**
 * @author jonalv
 */
public class PropertyChoiceDao
       extends GenericDao<PropertyChoice> 
       implements IPropertyChoiceDao {

    public PropertyChoiceDao() {
        super( PropertyChoice.class );
    }
 
    @Override
    public void insert(PropertyChoice propertyChoice) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          propertyChoice );
        if ( propertyChoice.getProperty() == null ) {
            getSqlMapClientTemplate().update( "PropertyChoice.insert",
                                              propertyChoice );
        }
        else {
            getSqlMapClientTemplate().update( "PropertyChoice.insertWithChoiceProperty",
                                              propertyChoice );
        }
    }

    @Override
    public void update(PropertyChoice propertyChoice) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          propertyChoice );
        if ( propertyChoice.getProperty() == null ) {
            getSqlMapClientTemplate().update( "PropertyChoice.update",
                                              propertyChoice );
        }
        else {
            getSqlMapClientTemplate().update( "PropertyChoice.updateWithChoiceProperty",
                                              propertyChoice );
        }
    }
}

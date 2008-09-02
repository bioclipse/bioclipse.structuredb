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


/**
 * @author jonalv
 *
 */
public class ChoicePropertyDao
       extends GenericDao<ChoiceProperty> 
       implements IChoicePropertyDao {

    public ChoicePropertyDao() {
        super( ChoiceProperty.class );
    }
    
    @Override
    public void insert(ChoiceProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          property );
        getSqlMapClientTemplate().update( "ChoiceProperty.insert",
                                          property );
    }
    
    @Override
    public void update(ChoiceProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          property );
        getSqlMapClientTemplate().update( "ChoiceProperty.update", 
                                          property );
    }    
}

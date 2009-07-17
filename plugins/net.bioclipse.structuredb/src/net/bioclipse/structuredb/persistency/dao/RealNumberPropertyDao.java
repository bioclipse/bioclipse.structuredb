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

import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;


/**
 * @author jonalv
 *
 */
public class RealNumberPropertyDao 
       extends GenericDao<RealNumberProperty> 
       implements IRealNumberPropertyDao {

    public RealNumberPropertyDao() {
        super( RealNumberProperty.class );
    }

    @Override
    public void insert(RealNumberProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          property );
        getSqlMapClientTemplate().update( "RealNumberProperty.insert",
                                          property );
    }
    
    @Override
    public void update(RealNumberProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          property );
        getSqlMapClientTemplate().update( "RealNumberProperty.update", 
                                          property );
    }

    public RealNumberProperty getByName( String name ) {

        return (RealNumberProperty)getSqlMapClientTemplate().queryForObject( 
                   "RealNumberProperty.getByName", name );
    }    
}

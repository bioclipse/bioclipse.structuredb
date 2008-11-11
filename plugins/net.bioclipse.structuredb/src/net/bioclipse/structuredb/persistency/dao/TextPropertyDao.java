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
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextProperty;


/**
 * @author jonalv
 *
 */
public class TextPropertyDao 
       extends GenericDao<TextProperty> 
       implements ITextPropertyDao {

    public TextPropertyDao() {
        super( TextProperty.class );
    }
    
    @Override
    public void insert(TextProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.insert", 
                                          property );
        getSqlMapClientTemplate().update( "TextProperty.insert",
                                          property );
    }
    
    @Override
    public void update(TextProperty property) {
        getSqlMapClientTemplate().update( "BaseObject.update", 
                                          property );
        getSqlMapClientTemplate().update( "TextProperty.update", 
                                          property );
    }

    public TextProperty getByName( String name ) {
        return (TextProperty)getSqlMapClientTemplate().queryForObject( 
                "TextProperty.getByName", name );
    }    
}

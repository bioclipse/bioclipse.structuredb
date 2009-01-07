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
package net.bioclipse.structuredb.persistence.dao;

import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.persistency.dao.IRealNumberPropertyDao;
import net.bioclipse.structuredb.persistency.dao.ITextPropertyDao;


/**
 * @author jonalv
 *
 */
public class TextPropertyDaoTest 
             extends GenericDaoTest<TextProperty> {

    private ITextPropertyDao textPropertyDao;
    
    public TextPropertyDaoTest() {
        super( TextProperty.class );
    }
    
    @Override
    public void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        textPropertyDao 
            = (ITextPropertyDao) 
              applicationContext.getBean( "textPropertyDao" );
    }
    
    public void testGetByName() {
        TextProperty property = new TextProperty();
        dao.insert( property );
        TextProperty loaded 
            = textPropertyDao.getByName( property.getName() );
        assertTrue( property.hasValuesEqualTo( loaded ) );
    }
}

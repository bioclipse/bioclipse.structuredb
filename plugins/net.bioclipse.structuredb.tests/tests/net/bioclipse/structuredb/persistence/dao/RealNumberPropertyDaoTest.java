/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson    
 *******************************************************************************/
package net.bioclipse.structuredb.persistence.dao;

import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.persistency.dao.IRealNumberPropertyDao;


/**
 * @author jonalv
 *
 */
public class RealNumberPropertyDaoTest 
             extends GenericDaoTest<RealNumberProperty> {

    private IRealNumberPropertyDao realNumberPropertyDao;

    public RealNumberPropertyDaoTest() {
        super( RealNumberProperty.class );
    }
    
    @Override
    public void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        realNumberPropertyDao 
            = (IRealNumberPropertyDao) 
              applicationContext.getBean( "realNumberPropertyDao" );
    }
    
    public void testGetByName() {
        RealNumberProperty property = new RealNumberProperty();
        dao.insert( property );
        RealNumberProperty loaded 
            = realNumberPropertyDao.getByName( property.getName() );
        assertTrue( property.hasValuesEqualTo( loaded ) );
    }
}

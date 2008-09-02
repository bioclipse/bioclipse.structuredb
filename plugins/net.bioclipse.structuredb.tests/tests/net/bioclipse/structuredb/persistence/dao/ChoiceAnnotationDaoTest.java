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

import net.bioclipse.structuredb.domain.ChoiceAnnotation;


/**
 * @author jonalv
 *
 */
public class ChoiceAnnotationDaoTest 
             extends GenericDaoTest<ChoiceAnnotation> {

    public ChoiceAnnotationDaoTest() {
        super( ChoiceAnnotation.class );
    }
    
    @Override
    public void testUpdate() {
        ChoiceAnnotation loadedObject1 = dao.getById( object1.getId() );
        assertFalse(loadedObject1.getName().equals("edited"));
        loadedObject1.setName("edited");
        loadedObject1.setValue("edited");
        dao.update(loadedObject1);
        loadedObject1 = dao.getById( object1.getId() );
        assertEquals( "name should have changed", 
                      "edited", loadedObject1.getName() );
        assertEquals( "value should have changed", 
                      "edited", loadedObject1.getValue() );
    }
}

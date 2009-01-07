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

import java.sql.Timestamp;

import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;


/**
 * @author jonalv
 *
 */
public class ChoiceAnnotationDaoTest 
             extends AnnotationDaoTest<ChoiceAnnotation> {

    public ChoiceAnnotationDaoTest() {
        super( ChoiceAnnotation.class );
    }
    
    @Override
    public void testUpdate() {
        ChoiceAnnotation loadedObject1 = dao.getById( object1.getId() );
        Timestamp before = loadedObject1.getEdited();
        loadedObject1.setValue("edited");
        dao.update(loadedObject1);
        loadedObject1 = dao.getById( object1.getId() );
        assertEquals( "value should have changed", 
                      "edited", loadedObject1.getValue() );
        assertFalse( "Edited timestamp should ahve changed",
                     before.equals( loadedObject1.getEdited() ) );
    }
    
    public void testWithChoiceProperty() {
        ChoiceProperty property = new ChoiceProperty();
        object1.setProperty( property );
        dao.update( object1 );
        ChoiceAnnotation loaded = dao.getById( object1.getId() );
        assertTrue( object1.hasValuesEqualTo(loaded) );
        
        ChoiceAnnotation newAnnotation = new ChoiceAnnotation();
        newAnnotation.setProperty( property );
        dao.insert( newAnnotation );
        loaded = dao.getById( newAnnotation.getId() );
        assertTrue( newAnnotation.hasValuesEqualTo( loaded ) );
    }
}

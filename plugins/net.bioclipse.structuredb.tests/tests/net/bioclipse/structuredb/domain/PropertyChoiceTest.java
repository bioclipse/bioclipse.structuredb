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
package net.bioclipse.structuredb.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author jonalv
 *
 */
public class PropertyChoiceTest {

    @Test
    public void testHasValuesEqualTo() {
        
        PropertyChoice propertyChoice1 
            = new PropertyChoice( "propertyChoicename");
        PropertyChoice propertyChoice2 
            = new PropertyChoice(propertyChoice1);
        PropertyChoice propertyChoice3 
            = new PropertyChoice( "propertyChoicename");
        
        assertTrue(  propertyChoice1.hasValuesEqualTo(propertyChoice2) );
        assertFalse( propertyChoice1.hasValuesEqualTo(propertyChoice3) );
    }
    
    @Test
    public void testDoubleReferences() {

        ChoiceProperty choiceProperty = new ChoiceProperty();
        PropertyChoice propertyChoice = new PropertyChoice();
        
        propertyChoice.setProperty( choiceProperty );
    
        assertTrue( propertyChoice.getProperty() == choiceProperty );
        assertTrue( choiceProperty.getPropertyChoices()
                                  .contains( propertyChoice ) );
    }
}

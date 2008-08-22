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
public class ChoicePropertyTest {

    @Test
    public void testHasValuesEqualTo() {
        
        ChoiceProperty choiceProperty1 
            = new ChoiceProperty( "choicePropertyname");
        ChoiceProperty choiceProperty2 
            = new ChoiceProperty(choiceProperty1);
        ChoiceProperty choiceProperty3 
            = new ChoiceProperty( "choicePropertyname");
        
        assertTrue(  choiceProperty1.hasValuesEqualTo(choiceProperty2) );
        assertFalse( choiceProperty1.hasValuesEqualTo(choiceProperty3) );
    }
    
    @Test
    public void testDoubleReferences() {
        ChoiceAnnotation annotation = new ChoiceAnnotation();
        
        ChoiceProperty choiceProperty = new ChoiceProperty();
        
        choiceProperty.addAnnotation(annotation);
    
        assertTrue( choiceProperty.getAnnotations()
                                  .contains( annotation ) );
        assertTrue( annotation.getProperty() == choiceProperty );
    }
}

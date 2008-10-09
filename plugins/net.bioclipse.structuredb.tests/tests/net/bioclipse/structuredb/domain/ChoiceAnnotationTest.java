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


public class ChoiceAnnotationTest {

    @Test
    public void testHasValuesEqualTo() {
        
        ChoiceProperty property = new ChoiceProperty();
        property.addPropertyChoice( new PropertyChoice("choice") );
        
        ChoiceAnnotation choiceAnnotation1 
            = new ChoiceAnnotation( "choice", property );
        ChoiceAnnotation choiceAnnotation2 
            = new ChoiceAnnotation(choiceAnnotation1);
        ChoiceAnnotation choiceAnnotation3 
            = new ChoiceAnnotation( "choice", property );
        
        assertTrue(  choiceAnnotation1
                     .hasValuesEqualTo(choiceAnnotation2) );
        assertFalse( choiceAnnotation1
                     .hasValuesEqualTo(choiceAnnotation3) );
    }
    
    @Test
    public void testDoubleReferences() {
        ChoiceProperty property = new ChoiceProperty();
        
        ChoiceAnnotation choiceAnnotation = new ChoiceAnnotation();
        
        choiceAnnotation.setProperty(property);
    
        assertTrue( choiceAnnotation.getProperty() == property );
        assertTrue( property.getAnnotations().contains(choiceAnnotation) );
    }
}

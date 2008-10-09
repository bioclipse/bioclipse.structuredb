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


public class TextAnnotationTest {
    
    @Test
    public void testHasValuesEqualTo() {

        TextProperty property = new TextProperty();
        
        TextAnnotation textAnnotation1 
            = new TextAnnotation( "textAnnotationname", property );
        TextAnnotation textAnnotation2 
            = new TextAnnotation(textAnnotation1);
        TextAnnotation textAnnotation3 
            = new TextAnnotation( "textAnnotationname", property );
        
        assertTrue(  textAnnotation1.hasValuesEqualTo(textAnnotation2) );
        assertFalse( textAnnotation1.hasValuesEqualTo(textAnnotation3) );
    }
    
    @Test
    public void testDoubleReferences() {
        TextProperty property = new TextProperty();
        
        TextAnnotation textAnnotation = new TextAnnotation();
        
        textAnnotation.setProperty(property);
    
        assertTrue( textAnnotation.getProperty() == property );
        assertTrue( property.getAnnotations().contains(textAnnotation) );
    }
}

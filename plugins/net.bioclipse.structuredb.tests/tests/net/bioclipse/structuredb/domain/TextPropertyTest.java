/* *****************************************************************************
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
public class TextPropertyTest {

    @Test
    public void testHasValuesEqualTo() {
        
        TextProperty textProperty1 
            = new TextProperty( "textPropertyname");
        TextProperty textProperty2 
            = new TextProperty(textProperty1);
        TextProperty textProperty3 
            = new TextProperty( "textPropertyname");
        
        assertTrue(  textProperty1.hasValuesEqualTo(textProperty2) );
        assertFalse( textProperty1.hasValuesEqualTo(textProperty3) );
    }
    
    @Test
    public void testDoubleReferences() {
        TextAnnotation annotation = new TextAnnotation();
        
        TextProperty textProperty = new TextProperty();
        
        textProperty.addAnnotation(annotation);
    
        assertTrue( textProperty.getAnnotations()
                                .contains( annotation ) );
        assertTrue( annotation.getProperty() == textProperty );
    }

}

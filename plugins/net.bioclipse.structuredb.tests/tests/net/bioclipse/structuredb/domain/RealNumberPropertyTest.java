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
public class RealNumberPropertyTest {

    @Test
    public void testHasValuesEqualTo() {
        
        RealNumberProperty realNumberProperty1 
            = new RealNumberProperty( "realNumberPropertyname");
        RealNumberProperty realNumberProperty2 
            = new RealNumberProperty(realNumberProperty1);
        RealNumberProperty realNumberProperty3 
            = new RealNumberProperty( "realNumberPropertyname");
        
        assertTrue(  realNumberProperty1
                     .hasValuesEqualTo(realNumberProperty2) );
        assertFalse( realNumberProperty1
                     .hasValuesEqualTo(realNumberProperty3) );
    }
    
    @Test
    public void testDoubleReferences() {
        RealNumberAnnotation annotation = new RealNumberAnnotation();
        
        RealNumberProperty realNumberProperty = new RealNumberProperty();
        
        realNumberProperty.addAnnotation(annotation);
    
        assertTrue( realNumberProperty.getAnnotations()
                                      .contains( annotation ) );
        assertTrue( annotation.getProperty() == realNumberProperty );
    }
}

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
public class RealNumberAnnotationTest {
    @Test
    public void testHasValuesEqualTo() {
        RealNumberProperty property = new RealNumberProperty();
        RealNumberAnnotation realNumberAnnotation1 
            = new RealNumberAnnotation( 3.14, property );
        RealNumberAnnotation realNumberAnnotation2 
            = new RealNumberAnnotation(realNumberAnnotation1);
        RealNumberAnnotation realNumberAnnotation3 
            = new RealNumberAnnotation( 3.14, property );
        assertTrue(  realNumberAnnotation1
                     .hasValuesEqualTo(realNumberAnnotation2) );
        assertFalse( realNumberAnnotation1
                     .hasValuesEqualTo(realNumberAnnotation3) );
    }
    @Test
    public void testDoubleReferences() {
        RealNumberProperty property = new RealNumberProperty();
        RealNumberAnnotation realNumberAnnotation 
            = new RealNumberAnnotation();
        realNumberAnnotation.setProperty(property);
        assertTrue( realNumberAnnotation.getProperty() == property );
        assertTrue( property.getAnnotations()
                            .contains(realNumberAnnotation) );
    }
}

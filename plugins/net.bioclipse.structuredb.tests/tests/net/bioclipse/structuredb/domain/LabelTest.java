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
public class LabelTest {

    @Test
    public void testHasValuesEqualsTo() {

        Structure s1 = new Structure();
        Structure s2 = new Structure();
        s2.setName("s2");
        
        Label library1 = new Label();
        library1.addStructure(s1);
        Label library2 = new Label(library1);
        Label library3 = new Label();
        library3.addStructure(s2);
        
        assertTrue(  library1.hasValuesEqualTo(library2) );
        assertFalse( library1.hasValuesEqualTo(library3) );
    }
    
    @Test
    public void testDoubleReferences() {

        Structure structure = new Structure();
        
        Label label = new Label();
        
        label.addStructure( structure );
    
        assertTrue( label.getStructures().contains(structure) );
        assertTrue( structure.getLabels().contains( label ) );
        
        label.removeStructure( structure );
        
        assertFalse( label.getStructures().contains(structure) );
        assertFalse( structure.getLabels().contains(label) );
    }
}

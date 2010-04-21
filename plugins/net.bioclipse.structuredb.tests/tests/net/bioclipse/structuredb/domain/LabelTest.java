/* *****************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
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
import org.openscience.cdk.exception.CDKException;

import testData.TestData;

/**
 * @author jonalv
 *
 */
public class LabelTest {

    @Test
    public void testHasValuesEqualsTo() throws CDKException {

        DBMolecule s1 = new DBMolecule( "CycloOctan",
                                        TestData.getCycloOctan() );        
        DBMolecule s2 = new DBMolecule( "CycloPropan", 
                                        TestData.getCycloPropane() );
        s2.setName("s2");
        
        TextAnnotation annotation1 = new TextAnnotation();
        annotation1.addDBMolecule(s1);
        TextAnnotation annotation2 = new TextAnnotation(annotation1);
        TextAnnotation annotation3 = new TextAnnotation();
        annotation3.addDBMolecule(s2);
        assertTrue(  annotation1.hasValuesEqualTo(annotation2) );
        assertFalse( annotation1.hasValuesEqualTo(annotation3) );
    }
    
    @Test
    public void testDoubleReferences() {

        DBMolecule dBMolecule = new DBMolecule();
        
        TextAnnotation annotation = new TextAnnotation();
        
        annotation.addDBMolecule( dBMolecule );
    
        assertTrue( annotation.getDBMolecules().contains(dBMolecule) );
        assertTrue( dBMolecule.getAnnotations().contains( annotation ) );
        
        annotation.removeDBMolecule( dBMolecule );
        
        assertFalse( annotation.getDBMolecules().contains(dBMolecule) );
        assertFalse( dBMolecule.getAnnotations().contains(annotation) );
    }
}

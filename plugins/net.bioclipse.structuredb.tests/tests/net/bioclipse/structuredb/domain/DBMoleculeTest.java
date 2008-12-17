/*******************************************************************************
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import testData.TestData;
public class DBMoleculeTest {
    static {
        System.setProperty(
            "javax.xml.parsers.SAXParserFactory", 
            "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        );
        System.setProperty(
            "javax.xml.parsers.DocumentBuilderFactory", 
            "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );
    }
    @Test
    public void testHasValuesEqualTo() throws CDKException {
        AtomContainer cycloPropane = TestData.getCycloPropane();
        AtomContainer cycloOctan   = TestData.getCycloOctan();
        DBMolecule structure1 = new DBMolecule( "cycloPropane", cycloPropane );
        DBMolecule structure2 = new DBMolecule(structure1);
        DBMolecule structure3 = new DBMolecule( "cycloOctane", cycloOctan );
        assertTrue(  structure1.hasValuesEqualTo(structure2) );
        assertFalse( structure1.hasValuesEqualTo(structure3) );
    }
    @Test
    public void testDoubleReferences() throws CDKException {
        AtomContainer testMolecule = TestData.getCycloPropane();
        Annotation annotation  = new TextAnnotation();
        Annotation annotation2 = new TextAnnotation();
        DBMolecule dBMolecule  = new DBMolecule("Cyclopropane", testMolecule);
        dBMolecule.addAnnotation( annotation );
        assertTrue( dBMolecule.getAnnotations().contains(annotation) );
        assertTrue( annotation.getDBMolecules().contains(dBMolecule) );
        dBMolecule.addAnnotation( annotation2 );
        assertTrue( dBMolecule.getAnnotations().contains(annotation2) );
        assertTrue( annotation2.getDBMolecules().contains(dBMolecule) );
        assertTrue( annotation.getDBMolecules().contains(dBMolecule)  );
        dBMolecule.removeAnnotation( annotation );
        assertFalse( dBMolecule.getAnnotations().contains(annotation) );
        assertFalse( annotation.getDBMolecules().contains(dBMolecule) );        
    }
}

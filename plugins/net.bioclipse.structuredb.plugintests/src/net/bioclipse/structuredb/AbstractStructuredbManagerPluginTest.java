/* *****************************************************************************
 * Copyright (c) 2009  jonalv <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.MockIFile;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IMolecule;
import net.bioclipse.core.api.domain.IMolecule.Property;
import net.bioclipse.core.api.jobs.BioclipseJobUpdateHook;
import net.bioclipse.core.api.jobs.IReturner;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.business.StructuredbManager;
import net.bioclipse.structuredb.business.IStructuredbManager.ImportStatistics;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.ui.business.IUIManager;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractStructuredbManagerPluginTest {

    protected static IStructuredbManager structuredb;
    protected static ICDKManager cdk;
    protected static IUIManager ui;
    
    protected final String database1 = "database1";
    protected final String database2 = "database2";
    protected String sdfile = "/Virtual/test.sdf";

    private static boolean fileCreated = false;
    
    
    @Before
    public void createDatabases() throws Exception {
        structuredb.createDatabase( database1 );
        structuredb.createDatabase( database2 );
        
        if (!fileCreated) {
            List<IMolecule> molecules = new ArrayList<IMolecule>();
            molecules.add( cdk.fromSMILES( "C" ) );
            molecules.add( cdk.fromSMILES( "CC" ) );
            cdk.saveSDFile( sdfile, molecules, new NullProgressMonitor() );
            fileCreated = true;
        }
    }
    
    @Test 
    public void testCreateDataBases() {
        
        List<String> allDatabaseNames = structuredb.allDatabaseNames();
        assertEquals( 2, allDatabaseNames.size() );
        assertTrue( allDatabaseNames.contains( database1 ) );
        assertFalse( allDatabaseNames.contains( "OH HAI" ) );
    }
    
    @Test
    public void testDoingInchiCalculation() throws BioclipseException {
        DBMolecule m = structuredb.createMolecule( database1, 
                                                   "a molecule", 
                                                   cdk.fromSMILES( "CCCC" ) );
        assertEquals( "InChI=1/C4H10/c1-3-4-2/h3-4H2,1-2H3", 
                      m.getInChI( Property.USE_CALCULATED ) );
    }
    
    
    @Test
    public void testDeleteStructure() throws Exception {
        DBMolecule dBMolecule 
            = structuredb.createMolecule( database1, 
                                          "test", 
                                          cdk.fromSMILES( "CC" ) );
        assertTrue( structuredb.allMolecules( database1 )
                               .contains( dBMolecule ) );
        structuredb.deleteStructure( database1, dBMolecule );
        assertFalse( structuredb.allMolecules( database1 )
                                .contains( dBMolecule ) );
    }

    @Test
    public void createTwoAnnotationsInTwoDatabases() {

        Annotation a1 = structuredb.createTextAnnotation( database1, 
                                                          "test",
                                                          "testAnnotation1" );
        Annotation a2 = structuredb.createTextAnnotation( database2, 
                                                          "test", 
                                                          "testAnnotation2" );
        assertNotNull(a2);
        assertNotNull(a1);

        assertTrue( structuredb.allAnnotations( database2 ).contains( a2 ) );
        assertTrue( structuredb.allAnnotations( database1 ).contains( a1 ) );
        
    }

    @Test
    public void testSubstructureSearch() throws Exception {

        ICDKMolecule mol1 = cdk.fromSMILES( "CCCC=O" );
        assertNotNull(mol1);

        DBMolecule dbMol = structuredb.createMolecule( database1,
                                                       "1",
                                                       mol1 );
        assertNotNull(dbMol);

        DBMolecule dbMol2 
            = structuredb.createMolecule( database1,
                                          "2",
                                          cdk.fromSMILES( "CCCCCC" ) );

        assertNotNull(dbMol2);

        assertTrue( structuredb
                    .allMoleculesByName( database1,
                                         dbMol.getName() ).contains(dbMol) );
        assertTrue( structuredb
                    .allMoleculesByName( database1,
                                         dbMol2.getName() ).contains(dbMol2) );

        List<DBMolecule> dBMolecules = structuredb.allMolecules(database1);

        assertTrue( dBMolecules.contains(dbMol) );
        assertTrue( dBMolecules.contains(dbMol2) );

        List<DBMolecule> list 
            = structuredb.subStructureSearch( database1, 
                                              cdk.fromSMILES( "C=O" ) );
        
        assertTrue(  list.contains( dbMol  ) );
        assertFalse( list.contains( dbMol2 ) );

        Iterator<DBMolecule> iterator 
            = structuredb.subStructureSearchIterator( database1, 
                                                      cdk.fromSMILES( "C=O" ) );
        boolean found = false;
        while ( iterator.hasNext() ) {
            if ( iterator.next().equals( dbMol ) ) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void testCreatingAndRetrievingMolecules() throws Exception {

        
        ICDKMolecule mol1 = cdk.fromSMILES( "CCC" );
        assertNotNull(mol1);

        DBMolecule dbMol1 = structuredb.createMolecule( database1,
                                                        "1",
                                                        mol1 );
        assertNotNull(dbMol1);

        DBMolecule dbMol2
            = structuredb.createMolecule( database1,
                                          "0106",
                                          cdk.fromSMILES( "CCC=O" ) );

        assertNotNull(dbMol2);

        assertTrue( structuredb.allMoleculesByName( database1,
                                                    dbMol1.getName() )
                               .contains(dbMol1) );
        assertTrue( structuredb.allMoleculesByName( database1,
                                                    dbMol2.getName() )
                               .contains(dbMol2) );

        List<DBMolecule> dBMolecules = structuredb.allMolecules(database1);

        assertTrue( dBMolecules.contains(dbMol1) );
        assertTrue( dBMolecules.contains(dbMol2) );
    }

    @Test
    public void testCreatingTextAnnotation() {
        TextAnnotation annotation1 = 
            structuredb.createTextAnnotation( database1,
                                              "test",
                                              "annotation1" );
        TextAnnotation annotation2 = 
            structuredb.createTextAnnotation( database1,
                                              "test",
                                              "annotation2" );
        TextAnnotation annotation3 =
            structuredb.createTextAnnotation( database1,
                                              "test2",
                                              "annotation3" );
        
        assertTrue( structuredb.allAnnotations( database1 )
                              .contains( annotation1 ) );
        
        assertNotNull( annotation1 );
        assertNotNull( annotation2 );
        assertNotNull( annotation3 );
        assertTrue( annotation1.getProperty()
                               .hasValuesEqualTo( 
                                   annotation2.getProperty() ) );
        assertFalse( annotation1.getProperty()
                                .hasValuesEqualTo( 
                                   annotation3.getProperty() ) );
    }

    @Test
    public void testCreatingRealNumberAnnotation() {
        RealNumberAnnotation annotation1 =
            structuredb.createRealNumberAnnotation( database1,
                                                    "testProperty",
                                                    1 );
        RealNumberAnnotation annotation2 = 
            structuredb.createRealNumberAnnotation( database1,
                                                    "testProperty",
                                                    1 );
        RealNumberAnnotation annotation3 =
            structuredb.createRealNumberAnnotation( database1,
                                                    "testProperty2",
                                                    2 );
        
        assertTrue( structuredb.allAnnotations( database1 )
                    .contains( annotation1 ) );
        
        assertNotNull( annotation1 );
        assertNotNull( annotation2 );
        assertNotNull( annotation3 );
        assertTrue( annotation1.getProperty()
                               .hasValuesEqualTo(
                                   annotation2.getProperty() ) );
        assertFalse( annotation1.getProperty()
                                .hasValuesEqualTo(
                                   annotation3.getProperty() ) );
    }

    @Test
    public void testImportingSDFFile() throws Exception {
        structuredb.addMoleculesFromSDF( 
            database1, 
            sdfile,
            new NullProgressMonitor() );
        boolean foundAnnotation = false;
        List<Annotation> l = structuredb.allAnnotations( database1 );
        for ( Annotation annotation : l ) {
            if ( annotation instanceof TextAnnotation ) {
                if ( ( (TextAnnotation)annotation )
                                       .getValue()
                                       .equals( "test" ) ) {
                    foundAnnotation = true;
                    assertEquals( 2, annotation.getDBMolecules().size() );
                }
            }
        }
        assertTrue(foundAnnotation);
    }
    
    @Test
    public void testCreatingAndRetrievingAnnotations() {
        Annotation annotation1 = structuredb.createTextAnnotation( database1,
                                                                  "test",
                                                                  "1" );
        Annotation annotation2 = structuredb.createTextAnnotation( database1,
                                                                  "test",
                                                                  "2" );
        assertNotNull(annotation1);
        assertNotNull(annotation2);
        List<Annotation> annotations = structuredb.allAnnotations(database1);
        assertTrue( annotations.contains(annotation1) );
        assertTrue( annotations.contains(annotation2) );
    }

    public void testDeleteAnnotation() {
        Annotation annotation 
            = structuredb.createTextAnnotation( database1,
                                                "test",
                                                "annotation" );
        assertTrue( structuredb.allAnnotations( database1 )
                               .contains( annotation ) );
        structuredb.deleteAnnotation( database1, annotation );
        assertFalse( structuredb.allAnnotations( database1 )
                                .contains( annotation ) );
    }
    
    @Test
    public void testDatabasesFilesAreLoaded() {
        HsqldbUtil.getInstance().stopAllDatabaseInstances();
        StructuredbManager anotherManager = new StructuredbManager();
        assertTrue( anotherManager.allDatabaseNames()
                                  .contains(database1) );
        assertTrue( anotherManager.allDatabaseNames()
                                  .contains(database2) );
        assertEquals( 2, anotherManager.allDatabaseNames().size() );
    }
    
    public void testUsingUnknownDatabase() {
        try {
            structuredb.createTextAnnotation( "unknown database",
                                              "test",
                                              "some name" );
            fail("should throw exception");
        }
        catch (IllegalArgumentException e) {
            //this is what we want
        }
    }
    
    @Test
    public void testEditDBMolecule() throws BioclipseException {
        DBMolecule m = structuredb.createMolecule( database1, 
                                                   "test", 
                                                   cdk.fromSMILES( "CCC" ) );
        Annotation a = structuredb.createTextAnnotation( database1, 
                                                         "test",
                                                         "annotation" );
        m.setName( "edited" );
        m.addAnnotation( a );
        structuredb.save( database1, m );
        List<DBMolecule> loaded = structuredb.allMoleculesByName( database1, 
                                                                  "edited" );
        assertEquals( 1, loaded.size() );
        
        List<Annotation> annotations = loaded.get( 0 ).getAnnotations();
        assertEquals( 1, annotations.size() );
        
        assertEquals( a, annotations.get( 0 ) );
        
        m.removeAnnotation(a);
        structuredb.save( database1, m );
        loaded = structuredb.allMoleculesByName( database1, 
                                                 "edited" );
        assertEquals( 1, loaded.size() );
        
        annotations = loaded.get( 0 ).getAnnotations();
        assertEquals( 0, annotations.size() );
    }
    
    @Test
    public void testEditTextAnnotation() throws BioclipseException {
        DBMolecule m = structuredb.createMolecule( database1, 
                                                   "test", 
                                                   cdk.fromSMILES( "CCC" ) );
        TextAnnotation annotation 
            = structuredb.createTextAnnotation( database1, 
                                                "test",
                                                "annotation" );
        annotation.setValue( "edited" );
        annotation.addDBMolecule( m );
        structuredb.save( database1, annotation );
        Annotation loaded = annotationByValue( annotation.getValue() );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( m, dBMolecules.get( 0 ) );
        
        annotation.removeDBMolecule( m );
        structuredb.save( database1, annotation );
        loaded = annotationByValue( annotation.getValue() );
        
        assertEquals( 0, loaded.getDBMolecules().size() );
    }
    
    private Annotation annotationByValue( Object value ) {
        
        List<Annotation> l = structuredb.allAnnotations( database1 );
        for ( Annotation a : l ) {
            if ( a instanceof TextAnnotation && 
                     value.equals( ((TextAnnotation)a).getValue() ) ||
                 a instanceof RealNumberAnnotation && 
                     Double.compare( (Double)value, 
                                     ( (RealNumberAnnotation)a )
                                         .getValue() ) == 0 ) {
                return a;
            }
        }
        throw new RuntimeException("No such annotation found");
    }

    @Test
    public void testEditRealNumberAnnotation() throws BioclipseException {
        DBMolecule m = structuredb.createMolecule( database1, 
                                                   "test", 
                                                   cdk.fromSMILES( "CCC" ) );
        RealNumberAnnotation annotation 
            = structuredb.createRealNumberAnnotation( database1, 
                                                      "testRealNumberProperty",
                                                      1 );
        annotation.setValue( -56.56 );
        annotation.addDBMolecule( m );
        structuredb.save( database1, annotation );
        Annotation loaded = annotationByValue( annotation.getValue() );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( m, dBMolecules.get( 0 ) );
        
        annotation.removeDBMolecule( m );
        structuredb.save( database1, annotation );
        loaded = annotationByValue( annotation.getValue() );

        assertEquals( 0, loaded.getDBMolecules().size() );
    }
    
    @Test
    public void testListSMARTSQueryResults() 
                throws IOException, BioclipseException {
        
        String propaneSmiles = "CCC";
        String butaneSmiles  = "CCCC"; 
        ICDKMolecule butane  = cdk.fromSMILES( butaneSmiles  );
        
        DBMolecule butaneStructure = structuredb.createMolecule( database1, 
                                                                 "indole", 
                                                                 butane );
        
        List<DBMolecule> list = structuredb.smartsQuery( database1, 
                                                         propaneSmiles );
        
        assertTrue( list.contains(butaneStructure) );
    }
    
    @Test
    public void testSmartsQueryIterator() throws BioclipseException, 
                                                 IOException {
        
        String propaneSmiles = "CCC";
        String butaneSmiles  = "CCCC"; 
        ICDKMolecule butane  = cdk.fromSMILES( butaneSmiles );
        
        DBMolecule butaneStructure = structuredb.createMolecule( database1, 
                                                                 "butane", 
                                                                 butane );
        
        Iterator<DBMolecule> iterator 
            = structuredb.smartsQueryIterator( database1, 
                                               propaneSmiles );
        boolean found = false;
        while ( iterator.hasNext() ) {
            if ( iterator.next().equals( butaneStructure ) ) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void testDeletingAnnotationWithMolecules() 
                throws BioclipseException {
        Annotation a = structuredb.createTextAnnotation( database1, 
                                                         "test", 
                                                         "annotation1" );
        DBMolecule s = structuredb.createMolecule( database1, 
                                                   "test", 
                                                   cdk.fromSMILES( "CCC" ) );
        a.addDBMolecule( s );
        structuredb.save( database1, a );
        assertTrue( structuredb.allMolecules(   database1 ).contains( s ) );
        assertTrue( structuredb.allAnnotations( database1 ).contains( a ) );
        structuredb.deleteWithMolecules( database1, a );
        assertFalse( structuredb.allMolecules(   database1 ).contains( s ) );
        assertFalse( structuredb.allAnnotations( database1 ).contains( a ) );
    }
    
    @Test
    public void testAllLabels() {
        Annotation a = structuredb.createTextAnnotation( database1, 
                                                         "label", 
                                                         "a label" );
        Annotation b = structuredb.createTextAnnotation( database1, 
                                                         "no label", 
                                                         "not a label" );
        assertTrue(  structuredb.allLabels( database1 ).contains( a ) );
        assertFalse( structuredb.allLabels( database1 ).contains( b ) );
    }
    
    @Test
    public void testAddMoleculesFromSDF() throws BioclipseException, 
                                                 FileNotFoundException, 
                                                 InterruptedException {
        structuredb.addMoleculesFromSDF( 
            database1,
            sdfile,
            new NullProgressMonitor() );
        boolean foundAnnotation = false;
        Annotation annotation = null;
        List<Annotation> l = structuredb.allAnnotations( database1 );
        for ( Annotation a : l ) {
            if ( a.getValue().equals( "test" ) ) {
                foundAnnotation = true;
                annotation = a;
            }
        }
        assertTrue( foundAnnotation );
        assertEquals( 2, annotation.getDBMolecules().size() );
    }
    
    @Test
    public void testAnnotate() throws Exception {
        Annotation a = structuredb.createTextAnnotation( database1, 
                                                         "test", 
                                                         "annotation1" );
        DBMolecule m = structuredb.createMolecule( database1, 
                                                   "test", 
                                                   cdk.fromSMILES( "CCC" ) );
        structuredb.annotate( database1, m, a );
        List<DBMolecule> molecules = structuredb.allMolecules( database1 );
        DBMolecule loaded = null;
        for ( DBMolecule ml : molecules ) {
            if (ml.equals( m )) {
                loaded = ml;
            }
        }
        assertNotNull( loaded );
        assertTrue( structuredb.allAnnotations( database1 ).contains( a ) );
        assertTrue( loaded.getAnnotations().contains( a ) );
    }
    
    @Test
    public void testGetAvailablePropertiesFromAnnotation() 
                throws BioclipseException {
        
        DBMolecule m1 = structuredb.createMolecule( database1, 
                                                    "C", 
                                                    cdk.fromSMILES( "C" ) );
        DBMolecule m2 = structuredb.createMolecule( database1, 
                                                    "CC", 
                                                    cdk.fromSMILES( "CC" ) );
        DBMolecule m3 = structuredb.createMolecule( database1, 
                                                    "CCC", 
                                                    cdk.fromSMILES( "CCC" ) );
        TextAnnotation ta 
            = structuredb.createTextAnnotation( database1, "label", "label" );
        structuredb.annotate( database1, m1, ta );
        structuredb.annotate( database1, m2, ta );
        
        TextAnnotation a1 
            = structuredb.createTextAnnotation( database1, "p1", "a1" );
        structuredb.annotate( database1, m1, a1 );
        
        RealNumberAnnotation a2 
            = structuredb.createRealNumberAnnotation( database1, "p2", 12 );
        structuredb.annotate( database1, m2, a2 );
        
        TextAnnotation a3
            = structuredb.createTextAnnotation( database1, "p3", "a3" );
        structuredb.annotate( database1, m3, a3 );
        
        Collection<Object> properties 
            = structuredb.getAvailableProperties( database1, ta );
        
        assertTrue(  properties.contains( "p1" ) );
        assertTrue(  properties.contains( "p2" ) );
        assertFalse( properties.contains( "p3" ) );
    }
    
    @Test
    public void testBug1798RenameLabel() throws Exception {
        DBMolecule m = structuredb.createMolecule( database1, 
                                                   "test", 
                                                   cdk.fromSMILES( "CCC" ) );
        TextAnnotation a 
            = structuredb.createTextAnnotation( database1, 
                                                "test",
                                                "annotation" );
        structuredb.annotate( database1, m, a );
        a = (TextAnnotation) 
            structuredb.getAnnotationById( database1, a.getId() );
        
        a.setValue( "edited" );
        structuredb.save( database1, a );

        Annotation loaded = annotationByValue( a.getValue() );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( m, dBMolecules.get( 0 ) );
        
        a.removeDBMolecule( m );
        structuredb.save( database1, a );
        loaded = annotationByValue( a.getValue() );
        
        assertEquals( 0, loaded.getDBMolecules().size() );
    }
}

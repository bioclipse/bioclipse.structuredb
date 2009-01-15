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
package net.bioclipse.structuredb.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.MockIFile;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;

import org.eclipse.core.resources.IFile;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.annotation.DirtiesContext;

import testData.TestData;

public class StructuredbManagerTest
       extends AbstractDependencyInjectionSpringContextTests {

    private IStructuredbManager manager;
    private String database1 = "database1";
    private String database2 = "database2";

    private static boolean setUpWasRun = false;

    private ICDKManager cdk = new CDKManager();
    
    static {
        // workaround for bug in java 1.5 on OS X
        if(Thread.currentThread().getContextClassLoader()==null)
            Thread.currentThread().setContextClassLoader(
                StructuredbManagerTest.class.getClassLoader());
        System.setProperty(
          "javax.xml.parsers.SAXParserFactory", 
          "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        );
        System.setProperty(
          "javax.xml.parsers.DocumentBuilderFactory", 
          "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );
        deepDelete( HsqldbUtil.getInstance().getDatabaseFilesDirectory() );
    }
    
    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();

        manager = (IStructuredbManager) applicationContext
                                        .getBean("structuredbManagerTarget");
        assertNotNull(manager);

        if (setUpWasRun) {
            return;
        }
        setUpWasRun = true;

        manager.createDatabase(database1);
        manager.createDatabase(database2);

        for ( ApplicationContext context :
             ((StructuredbManager)manager).applicationContexts.values() ) {

            setALoggedInUser(context);
        }
    }
    
    @Override
    protected void onTearDown() throws Exception {
        
    }

    private static void deepDelete( File file ) {
        
        File[] files = file.listFiles();
        
        if(files != null) {
          for( File f : files ) {
              deepDelete( f );
          }
        }
 
        file.delete();
    }

    @Override
    protected String[] getConfigLocations() {
        String loc = new File(".").getAbsolutePath();
        loc = loc.substring(0, loc.lastIndexOf(".tests"));
        loc += File.separator
            + "META-INF"
            + File.separator
            + "spring"
            + File.separator
            + "context.xml";

        return new String[] {"file:" + loc};
    }

    public void testCreatingTwoAnnotationsInTwoDatabases() {

        Annotation a2 = manager.createTextAnnotation( database2, 
                                                      "test", 
                                                      "testAnnotation2" );
        assertNotNull(a2);
        Annotation a1 = manager.createTextAnnotation( database1, 
                                                      "test",
                                                      "testAnnotation1" );
        assertNotNull(a1);

        assertTrue( manager.allAnnotations( database2 ).contains( a2 ) );
        assertTrue( manager.allAnnotations( database1 ).contains( a1 ) );
    }

    public void testListSubstructureSearchResults() throws Exception {
        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( 
            new MockIFile( TestData.class
                                   .getClassLoader()
                                   .getResourceAsStream("testData/0037.cml")
                         ) );
        assertNotNull(mol1);

        DBMolecule structure1 = manager.createMolecule( database1,
                                                        "0037",
                                                        mol1 );
        assertNotNull(structure1);

        DBMolecule structure2 
            = manager.createMolecule( database1,
                                      "0106",
                                      cdk.loadMolecule(
                                          new MockIFile( 
                                              TestData.class
                                                      .getClassLoader()
                                                      .getResourceAsStream(
                                                          "testData/0106.cml") 
                                     ) ) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allMoleculesByName( database1,
                                          structure1.getName() )
                                          .contains(structure1) );
        assertTrue( manager
                    .allMoleculesByName( database1,
                                          structure2.getName() )
                                          .contains(structure2) );

        List<DBMolecule> dBMolecules = manager.allMolecules(database1);

        assertTrue( dBMolecules.contains(structure1) );
        assertTrue( dBMolecules.contains(structure2) );

        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator.createSMILES( 
                                   MoleculeFactory.makeIndole() );
        String pyrroleSmiles = generator.createSMILES( 
                                   MoleculeFactory.makePyrrole() );
        ICDKMolecule indole  = cdk.fromSMILES( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSMILES( pyrroleSmiles );

        DBMolecule indoleStructure = manager.createMolecule( database1, 
                                                             "indole", 
                                                             indole );
        
        List<DBMolecule> list = manager.subStructureSearch( database1, 
                                                            pyrrole );
        
        assertTrue( list.contains( indoleStructure ));
    }
    
    public void testSubstructureSearch() throws Exception {

        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( 
              new MockIFile ( TestData.class
                                      .getClassLoader()
                                      .getResourceAsStream(
                                          "testData/0037.cml") ) );
        assertNotNull(mol1);

        DBMolecule structure1 = manager.createMolecule( database1,
                                                        "0037",
                                                        mol1 );
        assertNotNull(structure1);

        DBMolecule structure2 
            = manager.createMolecule( 
                database1,
                "0106",
                cdk.loadMolecule( 
                    new MockIFile(
                        TestData.class
                                .getClassLoader()
                                .getResourceAsStream(
                                    "testData/0106.cml") ) ) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allMoleculesByName( database1,
                                         structure1.getName() )
                                                   .contains(structure1) );
        assertTrue( manager
                    .allMoleculesByName( database1,
                                         structure2.getName() )
                                                   .contains(structure2) );

        List<DBMolecule> dBMolecules = manager.allMolecules(database1);

        assertTrue( dBMolecules.contains(structure1) );
        assertTrue( dBMolecules.contains(structure2) );

        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory.makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( MoleculeFactory.makePyrrole() );
        ICDKMolecule indole  = cdk.fromSMILES( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSMILES( pyrroleSmiles );

        DBMolecule indoleStructure = manager.createMolecule( database1, 
                                                             "indole", 
                                                             indole );
        
        Iterator<DBMolecule> iterator = manager
                                       .subStructureSearchIterator( database1, 
                                                                    pyrrole );
        boolean foundIndole = false;
        while (iterator.hasNext()) {
            if ( iterator.next().equals( indoleStructure ) ) {
                foundIndole = true;
            }
        }
        assertTrue(foundIndole);
    }
    
    public void testCreatingAndRetrievingStructures() throws Exception {
        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( 
            new MockIFile( TestData.class
                                   .getClassLoader()
                                   .getResourceAsStream(
                                       "testData/0037.cml") ) );
        assertNotNull(mol1);

        DBMolecule structure1 = manager
                                .createMolecule( database1,
                                                 "0037",
                                                 mol1 );
        assertNotNull(structure1);

        DBMolecule structure2 
            = manager.createMolecule(
                database1,
                "0106",
                cdk.loadMolecule( 
                    new MockIFile( TestData
                                   .class
                                   .getClassLoader()
                                   .getResourceAsStream(
                                       "testData/0106.cml") ) ) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allMoleculesByName( database1,
                                         structure1.getName() )
                    .contains(structure1) );
        assertTrue( manager
                    .allMoleculesByName( database1,
                                         structure2.getName() )
                    .contains(structure2) );

        List<DBMolecule> dBMolecules = manager.allMolecules(database1);

        assertTrue( dBMolecules.contains(structure1) );
        assertTrue( dBMolecules.contains(structure2) );
    }
    
    public void testCreatingTextAnnotation() {
        TextAnnotation annotation1 = 
            manager.createTextAnnotation( database1, "test", "annotation1" );
        TextAnnotation annotation2 = 
            manager.createTextAnnotation( database1, "test", "annotation2" );
        TextAnnotation annotation3 =
            manager.createTextAnnotation( database1, "test2", "annotation3" );
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
    
    public void testCreatingRealNumberAnnotation() {
        RealNumberAnnotation annotation1 = 
            manager.createRealNumberAnnotation( database1, 
                                                "testProperty", 
                                                1 );
        RealNumberAnnotation annotation2 = 
            manager.createRealNumberAnnotation( database1, 
                                                "testProperty", 
                                                1 );
        RealNumberAnnotation annotation3 =
            manager.createRealNumberAnnotation( database1, 
                                                "testProperty2", 
                                                2 );
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
    
    public void testCreatingChoiceAnnotation() {
        ChoiceAnnotation annotation1 = 
            manager.createChoiceAnnotation( database1, 
                                            "testChoiceProperty", 
                                            "annotation1" );
        ChoiceAnnotation annotation2 = 
            manager.createChoiceAnnotation( database1, 
                                            "testChoiceProperty", 
                                            "annotation2" );
        ChoiceAnnotation annotation3 =
            manager.createChoiceAnnotation( database1, 
                                            "testChoiceProperty2", 
                                            "annotation3" );
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

    private void setALoggedInUser(ApplicationContext context) {
        LoggedInUserKeeper keeper = (LoggedInUserKeeper)
        context.getBean("loggedInUserKeeper");
        IStructuredbInstanceManager internalManager
            = (IStructuredbInstanceManager)
        context.getBean("structuredbInstanceManager");
        keeper.setLoggedInUser( internalManager
                                .retrieveUserByUsername("local") );
    }

    public void testImportingSDFFile() throws BioclipseException, 
                                              FileNotFoundException {
        IFile file = new MockIFile( TestData.getTestSDFFilePath() );
        manager.addMoleculesFromSDF( database1, file );
        boolean foundAnnotation = false;
        for ( Annotation annotation : manager.allAnnotations( database1 ) ) {
            if ( annotation instanceof TextAnnotation ) {
                if ( ( (TextAnnotation)annotation )
                                       .getValue()
                                       .equals( 
                                           file.getName()
                                               .replaceAll("\\..*?$", "") ) ) {
                    foundAnnotation = true;
                    assertEquals( 2, annotation.getDBMolecules().size() );
                }
            }
        }
        assertTrue(foundAnnotation);
    }

    public void testCreatingAndRetrievingAnnotations() {
        Annotation annotation1 = manager.createTextAnnotation( database1,
                                                               "test",
                                                               "folder1" );
        Annotation annotation2 = manager.createTextAnnotation( database1,
                                                               "test",
                                                               "folder2" );
        assertNotNull(annotation1);
        assertNotNull(annotation2);
        List<Annotation> annotations = manager.allAnnotations(database1);
        assertTrue( annotations.contains(annotation1) );
        assertTrue( annotations.contains(annotation2) );
    }

    public void testDeleteAnnotation() {
        Annotation annotation = manager.createTextAnnotation( database1,
                                                              "test",
                                                              "annotation" );
        assertTrue( manager.allAnnotations( database1 )
                           .contains( annotation ) );
        manager.deleteAnnotation(database1, annotation);
        assertFalse( manager.allAnnotations( database1 )
                            .contains( annotation ) );
    }
    
    public void testDeleteStructure() throws BioclipseException {
        ICDKManager cdk = new CDKManager();
        DBMolecule dBMolecule 
            = manager.createMolecule( database1, 
                                       "test", 
                                       cdk.fromSMILES( "CC" ) );
        assertTrue( manager.allMolecules( database1 )
                           .contains( dBMolecule ) );
        manager.deleteStructure( database1, dBMolecule );
    }
    
    public void testCreatingAndRetrievingUsers() {
        User user1 = manager.createUser(database1, "user1", "", true);
        User user2 = manager.createUser(database1, "user2", "", true);
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals( user1,
                      manager.userByName(database1, user1.getUserName()) );
        List<User> users = manager.allUsers(database1);
        assertTrue( users.contains(user1) );
        assertTrue( users.contains(user2) );
    }
    
    public void testDatabasesFilesAreLoaded() {
        HsqldbUtil.getInstance().stopAllDatabaseInstances();
        StructuredbManager anotherManager = new StructuredbManager();
        assertTrue( anotherManager.allDatabaseNames()
                                  .contains(database1) );
        assertTrue( anotherManager.allDatabaseNames()
                                  .contains(database1) );
        assertEquals( 2, anotherManager.allDatabaseNames().size() );
    }

    @DirtiesContext
    public void testRemovingDatabaseInstance() {
    	try {
	        assertTrue( manager.allDatabaseNames().contains(database1) );
	        manager.deleteDatabase( database1 );
	        assertFalse( manager.allDatabaseNames().contains(database1) );
	        
	        StructuredbManager anotherManager = new StructuredbManager();
	        assertFalse( anotherManager.allDatabaseNames()
	                                   .contains(database1) );
    	}
    	finally {
    		manager.createDatabase( database1 ); // restore order
    	}
    	assertTrue( manager.allDatabaseNames().contains( database1 ) );
    }
    
    public void testUsingUnknownDatabase() {
        try {
            manager.createTextAnnotation( "unknown database",
                                          "test",
                                          "some folder name" );
            fail("should throw exception");
        }
        catch (IllegalArgumentException e) {
            //this is what we want
        }
    }

    public void testEditDBMolecule() throws BioclipseException {
        DBMolecule s = manager.createMolecule( database1, 
                                               "test", 
                                               cdk.fromSMILES( "CCC" ) );
        Annotation l = manager.createTextAnnotation( database1, 
                                                     "test",
                                                     "annotation" );
        s.setName( "edited" );
        s.addAnnotation( l );
        manager.save( database1, s );
        List<DBMolecule> loaded = manager.allMoleculesByName( database1, 
                                                              "edited" );
        assertEquals( 1, loaded.size() );
        
        List<Annotation> annotations = loaded.get( 0 ).getAnnotations();
        assertEquals( 1, annotations.size() );
        
        assertEquals( l, annotations.get( 0 ) );
        
        s.removeAnnotation(l);
        manager.save( database1, s );
        loaded = manager.allMoleculesByName( database1, 
                                              "edited" );
        assertEquals( 1, loaded.size() );

        annotations = loaded.get( 0 ).getAnnotations();
        assertEquals( 0, annotations.size() );
    }
    
    public void testEditTextAnnotation() throws BioclipseException {
        DBMolecule s = manager.createMolecule( database1, 
                                               "test", 
                                               cdk.fromSMILES( "CCC" ) );
        TextAnnotation annotation = manager.createTextAnnotation( database1, 
                                                                  "test",
                                                                  "annotation" );
        annotation.setValue( "edited" );
        annotation.addDBMolecule( s );
        manager.save( database1, annotation );
        Annotation loaded = annotationByValue( annotation.getValue() );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( s, dBMolecules.get( 0 ) );
        
        annotation.removeDBMolecule( s );
        manager.save( database1, annotation );
        loaded = annotationByValue( annotation.getValue() );

        assertEquals( 0, loaded.getDBMolecules().size() );
    }
    
    private Annotation annotationByValue( Object value ) {

        for ( Annotation a : manager.allAnnotations( database1 ) ) {
            if ( a instanceof TextAnnotation && 
                     value.equals( ((TextAnnotation)a).getValue() ) ||
                 a instanceof RealNumberAnnotation && 
                     Double.compare( (Double)value, 
                                     ( (RealNumberAnnotation)a )
                                         .getValue() ) == 0 ||
                 a instanceof ChoiceAnnotation && 
                     value.equals( ((ChoiceAnnotation)a ).getValue() ) ) {
                return a;
            }
        }
        throw new RuntimeException("No such annotation found");
    }

    public void testEditRealNumberAnnotation() throws BioclipseException {
        DBMolecule s = manager.createMolecule( database1, 
                                               "test", 
                                               cdk.fromSMILES( "CCC" ) );
        RealNumberAnnotation annotation 
            = manager.createRealNumberAnnotation( database1, 
                                                  "testRealNumberProperty",
                                                  1 );
        annotation.setValue( -56.56 );
        annotation.addDBMolecule( s );
        manager.save( database1, annotation );
        Annotation loaded = annotationByValue( annotation.getValue() );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( s, dBMolecules.get( 0 ) );
        
        annotation.removeDBMolecule( s );
        manager.save( database1, annotation );
        loaded = annotationByValue( annotation.getValue() );

        assertEquals( 0, loaded.getDBMolecules().size() );
    }
    
    public void testEditChoiceAnnotation() throws BioclipseException {
        DBMolecule s = manager.createMolecule( database1, 
                                               "test", 
                                               cdk.fromSMILES( "CCC" ) );
        ChoiceAnnotation annotation 
            = manager.createChoiceAnnotation( database1, 
                                              "testChoiceProperty",
                                              "annotation" );
        annotation.setValue( "edited" );
        annotation.addDBMolecule( s );
        manager.save( database1, annotation );
        Annotation loaded = annotationByValue( annotation.getValue() );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( s, dBMolecules.get( 0 ) );
        
        annotation.removeDBMolecule( s );
        manager.save( database1, annotation );
        loaded = annotationByValue( annotation.getId() );

        assertEquals( 0, loaded.getDBMolecules().size() );
    }
    
    public void testListSMARTSQueryResults() 
                throws IOException, BioclipseException {

        String propaneSmiles = "CCC";
        String butaneSmiles  = "CCCC"; 
        ICDKMolecule butane  = cdk.fromSMILES( butaneSmiles  );

        DBMolecule butaneStructure = manager.createMolecule( database1, 
                                                             "indole", 
                                                             butane );
        
        List<DBMolecule> list = manager.smartsQuery( database1, 
                                                    propaneSmiles );
        
        assertTrue( list.contains(butaneStructure) );
    }
    
    public void testSmartsQueryIterator() throws BioclipseException, 
                                                 IOException {

        String propaneSmiles = "CCC";
        String butaneSmiles  = "CCCC"; 
        ICDKMolecule butane  = cdk.fromSMILES( butaneSmiles  );

        DBMolecule butaneStructure = manager.createMolecule( database1, 
                                                             "indole", 
                                                             butane );
        
        Iterator<DBMolecule> iterator 
            = manager.smartsQueryIterator( database1, 
                                           propaneSmiles );
        boolean found = false;
        while ( iterator.hasNext() ) {
            if ( iterator.next().equals( butaneStructure ) ) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    public void testDeletingAnnotationWithMolecules() 
                throws BioclipseException {
        Annotation a = manager.createTextAnnotation( database1, 
                                                     "test", 
                                                     "annotation1" );
        DBMolecule s = manager.createMolecule( database1, 
                                               "test", 
                                               cdk.fromSMILES( "CCC" ) );
        a.addDBMolecule( s );
        manager.save( database1, a );
        assertTrue( manager.allMolecules(   database1 ).contains( s ) );
        assertTrue( manager.allAnnotations( database1 ).contains( a ) );
        manager.deleteWithMolecules( database1, a );
        assertFalse( manager.allMolecules(   database1 ).contains( s ) );
        assertFalse( manager.allAnnotations( database1 ).contains( a ) );
    }
    
    public void testAllLabels() {
        Annotation a = manager.createTextAnnotation( database1, 
                                                     "label", 
                                                     "a label" );
        Annotation b = manager.createTextAnnotation( database1, 
                                                     "no label", 
                                                     "not a label" );
        assertTrue( manager.allLabels( database1 ).contains( a ) );
        assertFalse( manager.allLabels( database1 ).contains( b ) );
    }
    
    public void testAddMoleculesFromSDF() {
        fail("Not yet implemented");
    }
    
}

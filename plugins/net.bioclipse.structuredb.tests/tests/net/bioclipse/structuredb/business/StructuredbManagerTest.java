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
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;

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

        if(setUpWasRun) {
            return;
        }
        setUpWasRun = true;

        manager.createDatabase(database1);
        manager.createDatabase(database2);

        for( ApplicationContext context :
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

        Annotation f2 = manager.createAnnotation(database2, "testAnnotation2");
        assertNotNull(f2);
        Annotation f1 = manager.createAnnotation(database1, "testAnnotation1");
        assertNotNull(f1);

        assertEquals( f2,
                      manager.annotationByName( database2, f2.getName() ) );
        assertEquals( f1,
                      manager.annotationByName( database1, f1.getName() ) );
    }

    public void testListSubstructureSearchResults() throws Exception {
        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( 
            new MockIFile( TestData
                           .class
                           .getClassLoader()
                           .getResource("testData/0037.cml")
                           .getPath() ) );
        assertNotNull(mol1);

        DBMolecule structure1 = manager
                               .createStructure( database1,
                                                 "0037",
                                                 mol1 );
        assertNotNull(structure1);

        DBMolecule structure2 
            = manager
              .createStructure( 
                   database1,
                   "0106",
                   cdk.loadMolecule(
                       new MockIFile( 
                           TestData.class
                                  .getClassLoader()
                                  .getResource("testData/0106.cml")
                                  .getPath() ) ) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure1.getName() )
                                          .contains(structure1) );
        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure2.getName() )
                                          .contains(structure2) );

        List<DBMolecule> dBMolecules = manager.allStructures(database1);

        assertTrue( dBMolecules.contains(structure1) );
        assertTrue( dBMolecules.contains(structure2) );

        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( 
                                   MoleculeFactory.makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( 
                                   MoleculeFactory.makePyrrole() );
        ICDKMolecule indole  = cdk.fromSmiles( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSmiles( pyrroleSmiles );

        DBMolecule indoleStructure = manager.createStructure( database1, 
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
                                      .getResource("testData/0037.cml")
                                      .getPath() ) );
        assertNotNull(mol1);

        DBMolecule structure1 = manager
                               .createStructure( database1,
                                                 "0037",
                                                 mol1 );
        assertNotNull(structure1);

        DBMolecule structure2 
            = manager.createStructure( 
                database1,
                "0106",
                cdk.loadMolecule( 
                    new MockIFile(
                        TestData.class
                                .getClassLoader()
                                .getResource("testData/0106.cml")
                                .getPath() ) ) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure1.getName() )
                                          .contains(structure1) );
        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure2.getName() )
                                          .contains(structure2) );

        List<DBMolecule> dBMolecules = manager.allStructures(database1);

        assertTrue( dBMolecules.contains(structure1) );
        assertTrue( dBMolecules.contains(structure2) );

        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory.makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( MoleculeFactory.makePyrrole() );
        ICDKMolecule indole  = cdk.fromSmiles( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSmiles( pyrroleSmiles );

        DBMolecule indoleStructure = manager.createStructure( database1, 
                                                             "indole", 
                                                             indole );
        
        Iterator<DBMolecule> iterator = manager
                                       .subStructureSearchIterator( database1, 
                                                                    pyrrole );
        boolean foundIndole = false;
        while(iterator.hasNext()) {
            if( iterator.next().equals( indoleStructure ) ) {
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
                                   .getResource("testData/0037.cml")
                                   .getPath() ) );
        assertNotNull(mol1);

        DBMolecule structure1 = manager
                              .createStructure( database1,
                                                "0037",
                                                mol1 );
        assertNotNull(structure1);

        DBMolecule structure2 
            = manager.createStructure(
                database1,
                "0106",
                cdk.loadMolecule( 
                    new MockIFile( TestData
                                   .class
                                   .getClassLoader()
                                   .getResource("testData/0106.cml")
                                   .getPath() ) ) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allStructuresByName( database1,
                                              structure1.getName() )
                    .contains(structure1) );
        assertTrue( manager
                    .allStructuresByName( database1,
                                                structure2.getName() )
                    .contains(structure2) );

        List<DBMolecule> dBMolecules = manager.allStructures(database1);

        assertTrue( dBMolecules.contains(structure1) );
        assertTrue( dBMolecules.contains(structure2) );
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
        manager.addStructuresFromSDF( database1, file );
        Annotation annotation
            = manager.annotationByName( database1, 
                                        file.getName()
                                            .replaceAll("\\..*?$", "") );
        assertNotNull(annotation);
        assertEquals( 2, annotation.getDBMolecules().size() );
    }

    public void testCreatingAndRetrievingAnnotations() {
        Annotation folder1 = manager.createAnnotation(database1, "folder1");
        Annotation folder2 = manager.createAnnotation(database1, "folder2");
        assertNotNull(folder1);
        assertNotNull(folder2);
        assertEquals( folder1,
                      manager
                      .annotationByName( database1, folder1.getName() ) );
        List<Annotation> annotations = manager.allAnnotations(database1);
        assertTrue( annotations.contains(folder1) );
        assertTrue( annotations.contains(folder2) );
    }

    public void testDeleteAnnotation() {
        Annotation annotation = manager.createAnnotation( database1, "annotation" );
        assertTrue( manager.allAnnotations( database1 ).contains( annotation ) );
        manager.deleteAnnotation(database1, annotation);
        assertFalse( manager.allAnnotations( database1 ).contains( annotation ) );
    }
    
    public void testDeleteStructure() throws BioclipseException {
        ICDKManager cdk = new CDKManager();
        DBMolecule dBMolecule 
            = manager.createStructure( database1, 
                                       "test", 
                                       cdk.fromSmiles( "CC" ) );
        assertTrue( manager.allStructures( database1 )
                           .contains( dBMolecule ) );
        manager.deleteStructure( database1, dBMolecule );
    }
    
    public void testCreatingAndRetrievingUsers() {
        User user1 = manager.createUser(database1, "user1", "", true);
        User user2 = manager.createUser(database1, "user2", "", true);
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals( user1,
                      manager.userByName(database1, user1.getName()) );
        List<User> users = manager.allUsers(database1);
        assertTrue( users.contains(user1) );
        assertTrue( users.contains(user2) );
    }
    
    public void testDatabasesFilesAreLoaded() {
        HsqldbUtil.getInstance().stopAllDatabaseInstances();
        StructuredbManager anotherManager = new StructuredbManager();
        assertTrue( anotherManager.listDatabaseNames()
                                  .contains(database1) );
        assertTrue( anotherManager.listDatabaseNames()
                                  .contains(database1) );
        assertEquals( 2, anotherManager.listDatabaseNames().size() );
    }

    @DirtiesContext
    public void testRemovingDatabaseInstance() {
        assertTrue( manager.listDatabaseNames().contains(database1) );
        manager.removeDatabase( database1 );
        assertFalse( manager.listDatabaseNames().contains(database1) );
        
        StructuredbManager anotherManager = new StructuredbManager();
        assertFalse( anotherManager.listDatabaseNames()
                                   .contains(database1) );
        manager.createDatabase( database1 ); // restore order
    }
    
    public void testUsingUnknownDatabase() {
        try {
            manager.createAnnotation( "unknown database", 
                                 "some folder name" );
            fail("should throw exception");
        }
        catch (IllegalArgumentException e) {
            //this is what we want
        }
    }

    public void testCreatingCDKMoleculeFromStructure() throws Exception {

        ICDKMolecule mol1 = cdk.loadMolecule( 
            new MockIFile( TestData.class
                                   .getClassLoader()
                                   .getResource("testData/0037.cml")
                                   .getPath() ) );
        assertNotNull(mol1);

        DBMolecule structure1 = new DBMolecule( "0037", mol1 );
        assertNotNull(structure1);
        
        ICDKMolecule newMolecule = manager.toCDKMolecule(structure1);
        assertEquals( mol1.getSmiles(), newMolecule.getSmiles() );
        assertEquals( mol1.getFingerprint( false ), 
                      newMolecule.getFingerprint( false ) );
        assertEquals( mol1.getSmiles(), newMolecule.getSmiles() );
        assertEquals( mol1.getCML(), newMolecule.getCML() );
    }
    
    public void testEditStructure() throws BioclipseException {
        DBMolecule s = manager.createStructure( database1, 
                                               "test", 
                                               cdk.fromSmiles( "CCC" ) );
        Annotation l = manager.createAnnotation( database1, "annotation" );
        s.setName( "edited" );
        s.addAnnotation( l );
        manager.save( database1, s );
        List<DBMolecule> loaded = manager.allStructuresByName( database1, 
                                                              "edited" );
        assertEquals( 1, loaded.size() );
        
        List<Annotation> annotations = loaded.get( 0 ).getAnnotations();
        assertEquals( 1, annotations.size() );
        
        assertEquals( l, annotations.get( 0 ) );
        
        s.removeAnnotation(l);
        manager.save( database1, s );
        loaded = manager.allStructuresByName( database1, 
                                              "edited" );
        assertEquals( 1, loaded.size() );

        annotations = loaded.get( 0 ).getAnnotations();
        assertEquals( 0, annotations.size() );
    }
    
    public void testEditAnnotation() throws BioclipseException {
        DBMolecule s = manager.createStructure( database1, 
                                               "test", 
                                               cdk.fromSmiles( "CCC" ) );
        Annotation annotation = manager.createAnnotation( database1, "a annotation" );
        annotation.setName( "edited" );
        annotation.addDBMolecule( s );
        manager.save( database1, annotation );
        Annotation loaded = manager.annotationByName( database1, "edited" );
        
        List<DBMolecule> dBMolecules = loaded.getDBMolecules();
        assertEquals( 1, dBMolecules.size() );
        
        assertEquals( s, dBMolecules.get( 0 ) );
        
        annotation.removeDBMolecule( s );
        manager.save( database1, annotation );
        loaded = manager.annotationByName( database1, "edited" );

        assertEquals( 0, loaded.getDBMolecules().size() );
    }
    
    public void testListSMARTSQueryResults() 
                throws IOException, BioclipseException {

        String propaneSmiles = "CCC";
        String butaneSmiles  = "CCCC"; 
        ICDKMolecule butane  = cdk.fromSmiles( butaneSmiles  );

        DBMolecule butaneStructure = manager.createStructure( database1, 
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
        ICDKMolecule butane  = cdk.fromSmiles( butaneSmiles  );

        DBMolecule butaneStructure = manager.createStructure( database1, 
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
    
    public void testRetrieveingAnnotationByName() {
        
        Annotation l = manager.createAnnotation( database1, "name" );
        Annotation loaded = manager.retrieveAnnotationByName( database1,  
                                                    l.getName() );
        assertTrue( l.hasValuesEqualTo( loaded ) );
    }
    
    public void testDeletingAnnotationWithStructures() 
                throws BioclipseException {
        Annotation l = manager.createAnnotation( database1, "annotation1" );
        DBMolecule s = manager.createStructure( database1, 
                                               "test", 
                                               cdk.fromSmiles( "CCC" ) );
        l.addDBMolecule( s );
        manager.save( database1, l );
        assertTrue( manager.allStructures( database1 ).contains( s ) );
        assertTrue( manager.allAnnotations(     database1 ).contains( l ) );
        manager.deleteWithStructures( database1, l );
        assertFalse( manager.allStructures( database1 ).contains( s ) );
        assertFalse( manager.allAnnotations(     database1 ).contains( l ) );
    }
}

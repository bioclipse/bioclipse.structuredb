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
package net.bioclipse.structuredb.business;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;

import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import testData.TestData;

public class StructuredbManagerTest
       extends AbstractDependencyInjectionSpringContextTests {

    private IStructuredbManager manager;
    private String database1 = "database1";
    private String database2 = "database2";

    private static boolean setUpWasRun = false;

    static {
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

        manager.createLocalInstance(database1);
        manager.createLocalInstance(database2);

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
        String loc = Structuredb.class
                                .getClassLoader()
                                .getResource(".")
                                .toString();
        loc = loc.substring(0, loc.lastIndexOf(".tests"));
        loc += File.separator
            + "META-INF"
            + File.separator
            + "spring"
            + File.separator
            + "context.xml";

        return new String[] {loc};
    }

    public void testCreatingTwoLabelsInTwoDatabases() {

        Label f2 = manager.createLabel(database2, "testLabel2");
        assertNotNull(f2);
        Label f1 = manager.createLabel(database1, "testLabel1");
        assertNotNull(f1);

        assertEquals( f2,
                      manager.labelByName( database2, f2.getName() ) );
        assertEquals( f1,
                      manager.labelByName( database1, f1.getName() ) );
    }

    public void testListSubstructureSearchResults() throws IOException, BioclipseException {
        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( TestData
                                              .class
                                              .getClassLoader()
                                              .getResourceAsStream(
                                              "testData/0037.cml") );
        assertNotNull(mol1);

        Structure structure1 = manager
                               .createStructure( database1,
                                                 "0037",
                                                 mol1 );
        assertNotNull(structure1);

        Structure structure2 = manager
                               .createStructure( 
                                   database1,
                                   "0106",
                                   cdk.loadMolecule( 
                                       TestData.class
                                               .getClassLoader()
                                               .getResourceAsStream(
                                                   "testData/0106.cml")) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure1.getName() )
                                          .contains(structure1) );
        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure2.getName() )
                                          .contains(structure2) );

        List<Structure> structures = manager.allStructures(database1);

        assertTrue( structures.contains(structure1) );
        assertTrue( structures.contains(structure2) );

        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory.makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( MoleculeFactory.makePyrrole() );
        ICDKMolecule indole  = cdk.fromSmiles( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSmiles( pyrroleSmiles );

        Structure indoleStructure = manager.createStructure( database1, 
                                                             "indole", 
                                                             indole );
        
        List<Structure> list = manager.subStructureSearch( database1, 
                                                           pyrrole );
        
        assertTrue( list.contains( indoleStructure ));
    }
    
    public void testSubstructureSearch() throws BioclipseException, 
                                                IOException {

        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( TestData
                                              .class
                                              .getClassLoader()
                                              .getResourceAsStream(
                                              "testData/0037.cml") );
        assertNotNull(mol1);

        Structure structure1 = manager
                               .createStructure( database1,
                                                 "0037",
                                                 mol1 );
        assertNotNull(structure1);

        Structure structure2 = manager
                               .createStructure( 
                                   database1,
                                   "0106",
                                   cdk.loadMolecule( 
                                       TestData.class
                                               .getClassLoader()
                                               .getResourceAsStream(
                                                   "testData/0106.cml")) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure1.getName() )
                                          .contains(structure1) );
        assertTrue( manager
                    .allStructuresByName( database1,
                                          structure2.getName() )
                                          .contains(structure2) );

        List<Structure> structures = manager.allStructures(database1);

        assertTrue( structures.contains(structure1) );
        assertTrue( structures.contains(structure2) );

        SmilesGenerator generator = new SmilesGenerator();
        String indoleSmiles  = generator
                               .createSMILES( MoleculeFactory.makeIndole() );
        String pyrroleSmiles = generator
                               .createSMILES( MoleculeFactory.makePyrrole() );
        ICDKMolecule indole  = cdk.fromSmiles( indoleSmiles );
        ICDKMolecule pyrrole = cdk.fromSmiles( pyrroleSmiles );

        Structure indoleStructure = manager.createStructure( database1, 
                                                             "indole", 
                                                             indole );
        
        Iterator<Structure> iterator = manager
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
    
    public void testCreatingAndRetrievingStructures() throws BioclipseException,
                                                             IOException {
        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( TestData
                                              .class
                                              .getClassLoader()
                                              .getResourceAsStream(
                                              "testData/0037.cml") );
        assertNotNull(mol1);

        Structure structure1 = manager
                              .createStructure( database1,
                                                "0037",
                                                mol1 );
        assertNotNull(structure1);

        Structure structure2 = manager
                               .createStructure(
                                      database1,
                                      "0106",
                                      cdk.loadMolecule(
                                              TestData
                                              .class
                                              .getClassLoader()
                                              .getResourceAsStream(
                                              "testData/0106.cml")) );

        assertNotNull(structure2);

        assertTrue( manager
                    .allStructuresByName( database1,
                                              structure1.getName() )
                    .contains(structure1) );
        assertTrue( manager
                    .allStructuresByName( database1,
                                                structure2.getName() )
                    .contains(structure2) );

        List<Structure> structures = manager.allStructures(database1);

        assertTrue( structures.contains(structure1) );
        assertTrue( structures.contains(structure2) );
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

    public void testImportingSDFFile() throws BioclipseException {
        IStructuredbManager manager
            = (IStructuredbManager) applicationContext
                                    .getBean("structuredbManagerTarget");
        manager.addStructuresFromSDF( database1,
                                      TestData.getTestSDFFilePath() );
        Label label
            = manager.labelByName( database1,
                                    "sdfTestFile" );
        assertNotNull(label);
        assertEquals( 2, label.getStructures().size() );
    }

    public void testCreatingAndRetrievingLabels() {
        Label folder1 = manager.createLabel(database1, "folder1");
        Label folder2 = manager.createLabel(database1, "folder2");
        assertNotNull(folder1);
        assertNotNull(folder2);
        assertEquals( folder1,
                      manager
                      .labelByName( database1, folder1.getName() ) );
        List<Label> labels = manager.allLabels(database1);
        assertTrue( labels.contains(folder1) );
        assertTrue( labels.contains(folder2) );
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
        assertTrue( anotherManager.listDatabaseNames().contains(database1) );
        assertTrue( anotherManager.listDatabaseNames().contains(database1) );
        assertEquals( 2, anotherManager.listDatabaseNames().size() );
    }
    
    public void testRemovingDatabaseInstance() {
        assertTrue( manager.listDatabaseNames().contains(database1) );
        manager.removeLocalInstance( database1 );
        assertFalse( manager.listDatabaseNames().contains(database1) );
        
        StructuredbManager anotherManager = new StructuredbManager();
        assertFalse( anotherManager.listDatabaseNames().contains(database1) );
    }
    
    public void testUsingUnknownDatabase() {
        try {
            manager.createLabel( "unknown database", "some folder name" );
            fail("should throw exception");
        }
        catch (IllegalArgumentException e) {
            //this is what we want
        }
    }
    
    public void testCreatingCDKMoleculeFromStructure() 
                throws IOException, BioclipseException {
        ICDKManager cdk = new CDKManager();

        ICDKMolecule mol1 = cdk.loadMolecule( TestData
                                              .class
                                              .getClassLoader()
                                              .getResourceAsStream(
                                              "testData/0037.cml") );
        assertNotNull(mol1);

        Structure structure1 = new Structure( "0037", mol1 );
        assertNotNull(structure1);
        
        ICDKMolecule newMolecule = manager.toCDKMolecule(structure1);
        assertEquals( mol1.getSmiles(), newMolecule.getSmiles() );
        assertEquals( mol1.getFingerprint( false ), 
                      newMolecule.getFingerprint( false ) );
        assertEquals( mol1.getSmiles(), newMolecule.getSmiles() );
        assertEquals( mol1.getCML(), newMolecule.getCML() );
    }
}

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

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;

import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import testData.TestData;

public class StructuredbManagerTest
       extends AbstractDependencyInjectionSpringContextTests {

    private IStructuredbManager manager;
    private String database1 = "database1";
    private String database2 = "database2";

    private static boolean setUpWasRun = false;

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

    public void testCreatingTwoFoldersInTwoDatabases() {

        Folder f2 = manager.createFolder(database2, "testFolder2");
        assertNotNull(f2);
        Folder f1 = manager.createFolder(database1, "testFolder1");
        assertNotNull(f1);

        assertEquals( f2,
                      manager.retrieveFolderByName( database2, f2.getName() ) );
        assertEquals( f1,
                      manager.retrieveFolderByName( database1, f1.getName() ) );
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
                    .retrieveStructuresByName( database1,
                                              structure1.getName() )
                    .contains(structure1) );
        assertTrue( manager
                    .retrieveStructuresByName( database1,
                                                structure2.getName() )
                    .contains(structure2) );

        List<Structure> structures = manager.retrieveAllStructures(database1);

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
        Folder folder
            = manager.retrieveFolderByName( database1,
                                            "test" );
        assertNotNull(folder);
        assertEquals( 2, folder.getStructures().size() );
    }

    public void testCreatingAndRetrievingFolders() {
        Folder folder1 = manager.createFolder(database1, "folder1");
        Folder folder2 = manager.createFolder(database1, "folder2");
        assertNotNull(folder1);
        assertNotNull(folder2);
        assertEquals( folder1,
                      manager
                      .retrieveFolderByName( database1, folder1.getName() ) );
        List<Folder> folders = manager.retrieveAllFolders(database1);
        assertTrue( folders.contains(folder1) );
        assertTrue( folders.contains(folder2) );
    }

    public void testCreatingAndRetrievingUsers() {
        User user1 = manager.createUser(database1, "user1", "", true);
        User user2 = manager.createUser(database1, "user2", "", true);
        assertNotNull(user1);
        assertNotNull(user2);
        assertEquals( user1,
                      manager.retrieveUserByName(database1, user1.getName()) );
        List<User> users = manager.retrieveAllUsers(database1);
        assertTrue( users.contains(user1) );
        assertTrue( users.contains(user2) );
    }
}

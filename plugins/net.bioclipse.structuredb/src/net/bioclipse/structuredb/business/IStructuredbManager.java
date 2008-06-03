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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.ui.views.JsConsoleView.ConsoleProgressMonitor;

/**
 * @author jonalv
 */
@PublishedClass ("Handles structure databases")
public interface IStructuredbManager extends IBioclipseManager {

    /**
     * Creates a local database instance.
     *
     * @param databaseName the name of the created database
     * @throws IllegalArgumentException if a database with the given
     *                                  name already exists
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Creates a local structure database " +
                                        "with the given name if no such " +
                                        "database already exists." )
    public void createLocalInstance( String databaseName )
        throws IllegalArgumentException;

    /**
     * Removes a local database instance with a given name if such a database
     * exists, otherwise does nothing.
     *
     * @param databaseName name of the database instance to be deleted
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Removes a local database with the " +
                                       "given name if such exists, otherwise " +
                                       "does nothing" )
    public void removeLocalInstance( String databaseName );

    /**
     * Retrieves all Structures with a given name from a database with a
     * given name.
     *
     * @param databaseName
     * @param structureName
     * @return
     */
    @PublishedMethod ( params = "String databaseName, String name",
                       methodSummary = "Fetches all structures by a given " +
                                       "name from a database with a given name")
    public List<Structure> allStructuresByName( String databaseName,
                                                String structureName );

    /**
     * Retrieves a folder with a given name from a database with a given name.
     *
     * @param databaseName
     * @param labelName
     * @return a folder
     */
    @PublishedMethod ( params = "String databaseName, String labelName",
                       methodSummary = "Fetches a label by a given name" +
                                       "from a database with a given name" )
    public Label labelByName( String databaseName,
                                String labelName );

    /**
     * Retrieves a user with a given username from a database with a given name.
     *
     * @param databaseName
     * @param username
     * @return
     */
    @PublishedMethod ( params = "String databaseName, String username",
                       methodSummary = "Fetches a user with a given username " +
                                       "from a database with a given name")
    public User userByName( String databaseName,
                            String username );

    /**
     * Creates a structure with the given name from the given cdkmolecule and
     * persists it in the database with the given name
     *
     * @param databaseName
     * @param moleculeName
     * @param cdkMolecule
     * @return the structure
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "String databaseName, String moleculeName, " +
                                "ICDKMolecule cdkMolecule",
                       methodSummary = "Creates a structure with the given " +
                                       "name from the given cdkmolecule and " +
                                       "saves it in the database with the " +
                                       "given name" )
    public Structure createStructure( String databaseName,
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule )
                                      throws BioclipseException;

    /**
     * Creates a folder with the given name and persists it in the database
     * with the given name.
     *
     * @param databaseName
     * @param labelName
     * @return the folder
     * @throws IllegalArgumentException
     */
    @PublishedMethod ( params = "String databaseName, String labelName",
                       methodSummary = "Creates a label with the given name " +
                                       "and saves it in the database with " +
                                       "the given name" )
    public Label createLabel( String databaseName,
                                String labelName )
                                throws IllegalArgumentException;

    /**
     * Creates a user with the given username, password and sudoer flag and
     * persists it in the database with the given name.
     *
     * @param databaseName
     * @param username
     * @param password
     * @param sudoer
     * @return the user
     * @throws IllegalArgumentException
     */
    @PublishedMethod ( params = "String databaseName, String username, " +
                                "String password, boolean administrator",
                       methodSummary = "Creates a user with the given " +
                                       "username and password and with " +
                                       "administrator rights if that " +
                                       "variable is true")
    public User createUser( String databaseName,
                            String username,
                            String password,
                            boolean sudoer ) throws IllegalArgumentException;

    /**
     * Retrieves all structures from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all structures from a " +
                                       "database with a given name")
    public List<Structure> allStructures( String databaseName );

    /**
     * Retrieves all folders from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all folders from a database " +
                                       "with a given name")
    public List<Label> allLabels( String databaseName );

    /**
     * Retrieves all users from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all users from a database " +
                                       "with a given name")
    public List<User> allUsers( String databaseName );

    /**
     * Persists all structures in a sdf file in the specified database in
     * a folder named after the file.
     *
     * @param path
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "String databaseName, String filePath",
                       methodSummary = "Saves all structures in a given sdf " +
                                       "file in the database with the given " +
                                       "name. The strucutres are stored in " +
                                       "library named after the name of the " +
                                       "sdf file")
    public void addStructuresFromSDF(String databaseName, String filePath) 
                throws BioclipseException;
    
    /**
     * Persists all structures in a sdf file in the specified database in
     * a folder named after the file with a progressmonitor
     * 
     * @param databaseName
     * @param filePath
     * @param monitor
     * @throws BioclipseException
     */
    public void addStructuresFromSDF( String databaseName, 
                                      String filePath, 
                                      IProgressMonitor monitor ) 
                throws BioclipseException;
    
    /**
     * @return a list of names of the names of all databases
     */
    @PublishedMethod ( methodSummary = "Returns a list with the names of all " +
    		                               "structuredb database instances." )
    public List<String> listDatabaseNames();

    /**
     * Returns an iterator to all structures in the given database that contains
     * the given molecule
     * 
     * @param databaseName
     * @param molecule
     * @return
     * @throws BioclipseException
     */
    @PublishedMethod (params = "String databaseName, IMolecule molecule",
                      methodSummary = "Returns an iterator to all structures " +
                      		          "in the given database that contains " +
                      		          "the given molecule")
    public Iterator<Structure> subStructureSearchIterator(String databaseName,
                                                          IMolecule molecule)
                               throws BioclipseException;

    /**
     * Returns a list of all structures in the given database that contains
     * the given molecule
     * 
     * @param databaseName
     * @param molecule
     * @return
     * @throws BioclipseException
     */
    @PublishedMethod (params = "String databaseName, IMolecule molecule",
                      methodSummary = "Returns a list of all structures " +
                                      "in the given database that contains " +
                                      "the given molecule")
    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule ) 
                           throws BioclipseException;
    
    /**
     * Substructuresearch with progressmonitor
     *
     * @param databaseName
     * @param molecule
     * @param monitor
     * @return
     * @throws BioclipseException
     */
    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule, 
                                               IProgressMonitor monitor ) 
                           throws BioclipseException;
    
    /**
     * Creates a cdk molecule from a structure
     * 
     * @param structure1
     * @return
     */
    @PublishedMethod (params = "Structure structure",
                      methodSummary = "Creates a cdk molecule from the given " +
                      		          "structure" )
    public ICDKMolecule toCDKMolecule( Structure structure );

    
    /**
     * Deletes the given label from the given database
     * 
     * @param database
     * @param label
     */
    @PublishedMethod ( params = "String database, Label label", 
                       methodSummary = "Deletes the given label from the " +
                       		           "given database" )
    public void delete( String database, Label label );

    /**
     * Deletes the given structure from the given database
     * 
     * @param database1
     * @param structure
     */
    @PublishedMethod ( params = "String database, Structure structure",
                       methodSummary = "Deletes the given structure from the " +
                       		           "given database" )
    public void delete( String database, Structure structure );

    
    /**
     * Saves the changes on the given structure back to the database. The 
     * structure must come from the given database.
     * 
     * @param database1
     * @param s
     */
    @PublishedMethod ( params = "String database, Structure structure",
                       methodSummary = "Saves changes on a structure " +
                       		           "retrieved from the database back to " +
                       		           "the database.")
    public void save( String database, Structure structure );

    
    /**
     * Saves the changes on the given label back to the database. The 
     * label must come from the given database.
     * 
     * @param database
     * @param label
     */
    @PublishedMethod ( params = "String database, Label label", 
                       methodSummary = "Saves changes on a label retrieved " +
                       		           "from the database back to the " +
                       		           "database.")
    public void save( String database, Label label );

    
    /**
     * @param database
     * @param smarts
     * @return an iterator to the hitting structures
     */
    @PublishedMethod ( params = "String database, String smarts", 
                       methodSummary = "Performs a SMARTS query returning an " +
                       		           "iterator to the hitting Structures " +
                       		           "in the database")
    public Iterator<Structure> smartsQueryIterator( String database,
                                                    String smarts );

    /**
     * @param database
     * @param smarts
     * @return a List of hitting structures
     */
    @PublishedMethod ( params = "String database, String smarts",
                       methodSummary = "Performs a SMARTS query returning a " +
                       		           "list of hitting Structures in the " +
                       		           "database")
    public List<Structure> smartsQuery( String database, String smarts );

    /**
     * @param database
     * @param smarts
     * @param monitor
     * @return an iterator to hitting structures
     */
    public Iterator<Structure> smartsQueryIterator( String database,
                                                    String smarts,
                                                    IProgressMonitor monitor );

    /**
     * @param database
     * @param smarts
     * @param monitor
     * @return a List of hitting structures
     */
    public List<Structure> smartsQuery( String database,
                                        String smarts,
                                        IProgressMonitor monitor );
}

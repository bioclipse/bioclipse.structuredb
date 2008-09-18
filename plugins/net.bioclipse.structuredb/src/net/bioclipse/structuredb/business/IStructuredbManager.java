/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *
 *******************************************************************************/
package net.bioclipse.structuredb.business;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.jobs.Job;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.User;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

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
                       methodSummary = "Creates a local structure " +
                       		           "database with the given name " +
                       		           "if no such database already " +
                       		           "exists." )
    public void createDatabase( String databaseName )
        throws IllegalArgumentException;

    /**
     * Removes a local database instance with a given name if such 
     * a database exists, otherwise does nothing.
     *
     * @param databaseName name of the database instance to be deleted
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Removes a local database " +
                       		           "with the given name if such " +
                       		           "exists, otherwise does nothing" )
    public void removeDatabase( String databaseName );

    /**
     * Retrieves all Structures with a given name from a database with a
     * given name.
     *
     * @param databaseName
     * @param structureName
     * @return
     */
    @PublishedMethod ( params = "String databaseName, String name",
                       methodSummary = "Fetches all structures by a " +
                       		           "given name from a database " +
                       		           "with a given name")
    public List<DBMolecule> allStructuresByName( String databaseName,
                                                String structureName );

    /**
     * Retrieves a folder with a given name from a database with 
     * a given name.
     *
     * @param databaseName
     * @param annotationName
     * @return a folder
     */
    @PublishedMethod ( params = "String databaseName, " +
    		                    "String annotationName",
                       methodSummary = "Fetches a annotation by a " +
                       		           "given name from a database " +
                       		           "with a given name" )
    public Annotation annotationByName( String databaseName,
                                        String annotationName );

    /**
     * Retrieves a user with a given username from a database 
     * with a given name.
     *
     * @param databaseName
     * @param username
     * @return
     */
    @PublishedMethod ( params = "String databaseName, String username",
                       methodSummary = "Fetches a user with a given " +
                       		           "username from a database " +
                       		           "with a given name")
    public User userByName( String databaseName,
                            String username );

    /**
     * Creates a structure with the given name from the given 
     * cdkmolecule and persists it in the database with the given name
     *
     * @param databaseName
     * @param moleculeName
     * @param cdkMolecule
     * @return the structure
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "String databaseName, " +
    		                    "String moleculeName, " +
                                "ICDKMolecule cdkMolecule",
                       methodSummary = "Creates a structure with the " +
                       		           "given name from the given " +
                       		           "cdkmolecule and saves it in " +
                       		           "the database with the given " +
                       		           "name" )
    public DBMolecule createStructure( String databaseName,
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule )
                                      throws BioclipseException;

    /**
     * Creates a folder with the given name and persists it in the 
     * database with the given name.
     *
     * @param databaseName
     * @param annotationName
     * @return the folder
     * @throws IllegalArgumentException
     */
    @PublishedMethod ( params = "String databaseName, " +
    		                    "String annotationName",
                       methodSummary = "Creates a annotation with " +
                       		           "the given name and saves it " +
                       		           "in the database with the " +
                       		           "given name" )
    public Annotation createAnnotation( String databaseName,
                              String annotationName )
                 throws IllegalArgumentException;

    /**
     * Creates a user with the given username, password and sudoer flag 
     * and persists it in the database with the given name.
     *
     * @param databaseName
     * @param username
     * @param password
     * @param sudoer
     * @return the user
     * @throws IllegalArgumentException
     */
    @PublishedMethod ( params = "String databaseName, " +
    		                    "String username, " +
                                "String password, " +
                                "boolean administrator",
                       methodSummary = "Creates a user with the " +
                       		           "given username and password " +
                       		           "and with administrator " +
                       		           "rights if that variable is " +
                       		           "true" )
    public User createUser( String databaseName,
                            String username,
                            String password,
                            boolean sudoer ) 
                throws IllegalArgumentException;

    /**
     * Retrieves all structures from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all structures from a " +
                                       "database with a given name")
    public List<DBMolecule> allStructures( String databaseName );

    /**
     * Retrieves all folders from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all folders from a " +
                       		           "database with a given name" )
    public List<Annotation> allAnnotations( String databaseName );

    /**
     * Retrieves all users from a database with a given name.
     *
     * @param databaseName
     * @return
     */
    @PublishedMethod ( params = "String databaseName",
                       methodSummary = "Fetches all users from a " +
                       		           "database with a given name" )
    public List<User> allUsers( String databaseName );

    /**
     * Persists all structures in a sdf file in the specified database in
     * a folder named after the file.
     *
     * @param path
     * @throws BioclipseException
     */
    @PublishedMethod ( params = "String databaseName, String filePath",
                       methodSummary = "Saves all structures in a " +
                       		           "given sdf file in the " +
                       		           "database with the given " +
                                       "name. The strucutres are " +
                                       "stored in library named " +
                                       "after the name of the " +
                                       "sdf file")
    @Job
    public void addStructuresFromSDF( String databaseName, 
                                      String filePath ) 
                throws BioclipseException;

    /**
     * @param databaseName
     * @param file
     * @param monitor
     * @throws BioclipseException
     */
    public void addStructuresFromSDF( String databaseName, 
                                      IFile file, 
                                      IProgressMonitor monitor ) 
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
    @PublishedMethod ( methodSummary = "Returns a list with the " +
    		                           "names of all structuredb " +
    		                           "database instances." )
    public List<String> listDatabaseNames();

    /**
     * Returns an iterator to all structures in the given database that 
     * contains the given molecule
     * 
     * @param databaseName
     * @param molecule
     * @return
     * @throws BioclipseException
     */
    @PublishedMethod (params = "String databaseName, IMolecule molecule",
                      methodSummary = "Returns an iterator to all " +
                      		          "structures in the given " +
                      		          "database that contains the " +
                      		          "given molecule")
    public Iterator<DBMolecule> subStructureSearchIterator(
        String databaseName, IMolecule molecule )
        throws BioclipseException;

    /**
     * Returns a list of all structures in the given database that 
     * contains the given molecule
     * 
     * @param databaseName
     * @param molecule
     * @return
     * @throws BioclipseException
     */
    @PublishedMethod (params = "String databaseName, IMolecule molecule",
                      methodSummary = "Returns a list of all " +
                      		          "structures in the given " +
                      		          "database that contains " +
                                      "the given molecule")
    public List<DBMolecule> subStructureSearch( String databaseName,
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
    public List<DBMolecule> subStructureSearch( String databaseName,
                                               IMolecule molecule, 
                                               IProgressMonitor monitor ) 
                           throws BioclipseException;
    
    /**
     * Deletes the given annotation from the given database
     * 
     * @param database
     * @param annotation
     */
    @PublishedMethod ( params = "String database, Annotation annotation", 
                       methodSummary = "Deletes the given annotation " +
                       		           "from the given database" )
    public void deleteAnnotation( String database, Annotation annotation );

    /**
     * Deletes the given structure from the given database
     * 
     * @param database1
     * @param dBMolecule
     */
    @PublishedMethod ( params = "String database, DBMolecule structure",
                       methodSummary = "Deletes the given structure " +
                       		           "from the given database" )
    public void deleteStructure( String database, DBMolecule dBMolecule );

    
    /**
     * Saves the changes on the given structure back to the database. The 
     * structure must come from the given database.
     * 
     * @param database1
     * @param s
     */
    @PublishedMethod ( params = "String database, DBMolecule structure",
                       methodSummary = "Saves changes on a structure " +
                       		           "retrieved from the database " +
                       		           "back to the database." )
    public void save( String database, DBMolecule dBMolecule );

    
    /**
     * Saves the changes on the given annotation back to the database. 
     * The annotation must come from the given database.
     * 
     * @param database
     * @param annotation
     */
    @PublishedMethod ( params = "String database, Annotation annotation", 
                       methodSummary = "Saves changes on a " +
                       		           "annotation retrieved from " +
                       		           "the database back to the " +
                       		           "database.")
    public void save( String database, Annotation annotation );

    /**
     * @param database
     * @param smarts
     * @return an iterator to the hitting structures
     */
    @PublishedMethod ( params = "String database, String smarts", 
                       methodSummary = "Performs a SMARTS query " +
                       		           "returning an iterator to the " +
                       		           "hitting Structures in the " +
                       		           "database" )
    public Iterator<DBMolecule> smartsQueryIterator( String database,
                                                    String smarts );

    /**
     * @param database
     * @param smarts
     * @return a List of hitting structures
     */
    @PublishedMethod ( params = "String database, String smarts",
                       methodSummary = "Performs a SMARTS query " +
                       		           "returning a list of hitting " +
                       		           "Structures in the database" )
    public List<DBMolecule> smartsQuery( String database, String smarts );

    /**
     * @param database
     * @param smarts
     * @param monitor
     * @return an iterator to hitting structures
     */
    public Iterator<DBMolecule> smartsQueryIterator( 
        String database, String smarts, IProgressMonitor monitor );

    /**
     * @param database
     * @param smarts
     * @param monitor
     * @return a List of hitting structures
     */
    public List<DBMolecule> smartsQuery( String database,
                                        String smarts,
                                        IProgressMonitor monitor );

    /**
     * @param listener
     */
    public void addListener( IDatabaseListener listener );
    
    /**
     * @param listener
     */
    public void removeListener( IDatabaseListener listener );

    /**
     * @param name
     * @param string 
     * @return
     */
    @PublishedMethod ( params = "String databaseName, " +
    		                    "String annotationName", 
                       methodSummary = "Retrives the annotation with " +
                       		           "the given name from the " +
                       		           "database with the given name" )
    public Annotation retrieveAnnotationByName( String databaseName, 
                                      String annotationName );

    
    /**
     * Deletes a annotation and all structures having that annotation
     * @param name
     * @param annotation
     */
    @PublishedMethod ( params =  "String name, Annotation annotation", 
                       methodSummary = "Deletes the given annotation " +
                       		           "from the specified database" )
    public void deleteWithStructures( String databaseName, 
                                      Annotation annotation );

    /**
     * @param name
     * @param annotation
     * @param monitor
     */
    public void deleteWithStructures( String name, Annotation annotation,
                                      IProgressMonitor monitor );

    /**
     * @param databaseName
     * @param file
     * @throws BioclipseException 
     */
    public void addStructuresFromSDF( String databaseName, IFile file ) 
                throws BioclipseException;
}

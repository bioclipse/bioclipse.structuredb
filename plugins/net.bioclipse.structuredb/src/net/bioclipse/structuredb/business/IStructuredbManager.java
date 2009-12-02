/*******************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.business;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.TextAnnotation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;


/**
 * @author jonalv
 *
 */
public interface IStructuredbManager extends IBioclipseManager {

        /**
         * Creates a local database instance.
         *
         * @param databaseName the name of the created database
         * @throws IllegalArgumentException if a database with the given
         *                                  name already exists
         */
        @TestMethods("testRemovingDatabaseInstance")
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
        @TestMethods("testRemovingDatabaseInstance")
        @PublishedMethod ( params = "String databaseName",
                           methodSummary = "Removes a local database " +
                                           "with the given name if such " +
                                           "exists, otherwise does nothing" )
        public void deleteDatabase( String databaseName );

        /**
         * Removes a local database instance with a given name if such 
         * a database exists, otherwise does nothing.
         *
         * @param databaseName name of the database instance to be deleted
         */
        @TestMethods("testRemovingDatabaseInstance")
        public BioclipseJob<Void> deleteDatabase( String databaseName, 
                                                  BioclipseJobUpdateHook<Void> h );
        
        /**
         * Retrieves all Molecules with a given name from a database with a
         * given name.
         *
         * @param databaseName
         * @param structureName
         * @return
         */
        @TestMethods("testListSubstructureSearchResults")
        @PublishedMethod ( params = "String databaseName, String name",
                           methodSummary = "Fetches all structures by a " +
                                           "given name from a database " +
                                           "with a given name")
        public List<DBMolecule> allMoleculesByName( String databaseName,
                                                    String structureName );

        /**
         * Creates a structure with the given name from the given 
         * cdkmolecule and persists it in the database with the given name
         *
         * @param databaseName
         * @param moleculeName
         * @param cdkMolecule
         * @return the structure
         * @throws BioclipseException 
         * @throws BioclipseException
         */
        @TestMethods("testListSubstructureSearchResults")
        @PublishedMethod ( params = "String databaseName, " +
                                    "String moleculeName, " +
                                    "ICDKMolecule cdkMolecule",
                           methodSummary = "Creates a database molecule with the "+
                                           "given name from the given " +
                                           "cdkmolecule and saves it in " +
                                           "the database with the given " +
                                           "name" )
        public DBMolecule createMolecule( String databaseName,
                                          String moleculeName,
                                          ICDKMolecule cdkMolecule ) 
                          throws BioclipseException;

        /**
         * Creates a RealNumberAnnotation with the given property and name and 
         * persists it in the database with the given name.
         *
         * @param databaseName
         * @param value
         * @param propertyName must be a RealNumberProperty
         * @return the folder
         * @throws IllegalArgumentException
         */
        @TestMethods("testCreatingRealNumberAnnotation")
        @PublishedMethod ( params = "String databaseName, " +
                                    "String propertyName, " +
                                    "String value",
                           methodSummary = "Creates a RealNumberAnnotation with " +
                                           "the given property (which must be a " +
                                           "RealNumberProperty), " +
                                           "name and saves it in the database" +
                                           " with the given name" )
        public RealNumberAnnotation createRealNumberAnnotation( 
                                                      String databaseName,
                                                      String propertyName,
                                                      double value )
                     throws IllegalArgumentException;

        /**
         * Creates a TextAnnotation with the given property and name and 
         * persists it in the database with the given name.
         *
         * @param databaseName
         * @param value
         * @param propertyName must be a TextProperty
         * @return the folder
         * @throws IllegalArgumentException
         */
        @TestMethods("testCreatingTextAnnotation")
        @PublishedMethod ( params = "String databaseName, " +
                                    "String propertyName, " +
                                    "String value",
                           methodSummary = "Creates a TextAnnotation with " +
                                           "the given property (which must be a " +
                                           "TextProperty), " +
                                           "name and saves it in the database" +
                                           " with the given name" )
        public TextAnnotation createTextAnnotation( String databaseName,
                                                    String propertyName,
                                                    String value )
                     throws IllegalArgumentException;
        
        /**
         * Retrieves all molecules from a database with a given name.
         *
         * @param databaseName
         * @return
         */
        @TestMethods("testListSubstructureSearchResults")
        @PublishedMethod ( params = "String databaseName",
                           methodSummary = "Fetches all molecules from a " +
                                           "database with a given name")
        public List<DBMolecule> allMolecules( String databaseName );

        /**
         * Retrieves all Annotations from a database with a given name.
         *
         * @param databaseName
         * @return
         */
        @TestMethods("testCreatingAndRetrievingAnnotations")
        @PublishedMethod ( params = "String databaseName",
                           methodSummary = "Fetches all annotations from a " +
                                           "database with a given name" )
        public List<Annotation> allAnnotations( String databaseName );

        /**
         * Persists all molecules in an sdf file in the specified database in
         * a folder named after the file.
         *
         * @param path
         * @throws BioclipseException
         */
        
        @PublishedMethod ( params = "String databaseName, String filePath",
                           methodSummary = "Saves all molecules in a " +
                                           "given sdf file in the " +
                                           "database with the given " +
                                           "name. The molecules are " +
                                           "annotated with file_origin" )
        @TestMethods("testAddMoleculesFromSDF")
        public BioclipseJob<Void> addMoleculesFromSDF( String databaseName, 
                                                       String filePath ) 
                                  throws BioclipseException;

        /**
         * @param databaseName
         * @param file
         * @param monitor
         * @throws BioclipseException
         */
        public void addMoleculesFromSDF( String databaseName, 
                                         IFile file, 
                                         IProgressMonitor monitor ) 
                    throws BioclipseException;
        
        
        /**
         * @return a list of the names of all databases
         */
        @TestMethods("testRemovingDatabaseInstance")
        @PublishedMethod ( methodSummary = "Returns a list with the " +
                                           "names of all structuredb " +
                                           "database instances." )
        public List<String> allDatabaseNames();

        /**
         * Returns an iterator to all molecules in the given database that 
         * contains the given molecule
         * 
         * @param databaseName
         * @param molecule
         * @return
         * @throws BioclipseException
         */
        @TestMethods("testSubstructureSearch")
        @PublishedMethod (params = "String databaseName, IMolecule molecule",
                          methodSummary = "Returns an iterator to all " +
                                          "molecules in the given " +
                                          "database that contains the " +
                                          "given molecule")
        public Iterator<DBMolecule> subStructureSearchIterator(
            String databaseName, IMolecule molecule )
            throws BioclipseException;

        /**
         * Returns a list of all molecules in the given database that 
         * contains the given molecule
         * 
         * @param databaseName
         * @param molecule
         * @return
         * @throws BioclipseException
         */
        @TestMethods("testListSubstructureSearchResults")
        @PublishedMethod (params = "String databaseName, IMolecule molecule",
                          methodSummary = "Returns a list of all " +
                                          "molecules in the given " +
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
        @TestMethods("testDeleteAnnotation")
        @PublishedMethod ( params = "String database, Annotation annotation", 
                           methodSummary = "Deletes the given annotation " +
                                           "from the given database" )
        public void deleteAnnotation( String database, Annotation annotation );

        /**
         * Deletes the given molecule from the given database
         * 
         * @param database1
         * @param dBMolecule
         */
        @TestMethods("testDeleteStructure")
        @PublishedMethod ( params = "String database, DBMolecule molecule",
                           methodSummary = "Deletes the given molecule " +
                                           "from the given database" )
        public void deleteStructure( String database, DBMolecule dBMolecule );

        /**
         * Saves the changes on the given molecule to the database. The 
         * molecule must come from the given database.
         * 
         * @param database1
         * @param s
         */
        @TestMethods("testEditDBMolecule")
        @PublishedMethod ( params = "String database, DBMolecule molecule",
                           methodSummary = "Saves changes on a molecule " +
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
        @TestMethods("testEditTextAnnotation")
        @PublishedMethod ( params = "String database, Annotation annotation", 
                           methodSummary = "Saves changes on a " +
                                           "annotation retrieved from " +
                                           "the database back to the " +
                                           "database.")
        public void save( String database, Annotation annotation );

        /**
         * @param database
         * @param smarts
         * @return an iterator to the hitting molecules
         */
        @TestMethods("testSmartsQueryIterator")
        @PublishedMethod ( params = "String database, String smarts", 
                           methodSummary = "Performs a SMARTS query " +
                                           "returning an iterator to the " +
                                           "hitting molecules in the " +
                                           "database" )
        public Iterator<DBMolecule> smartsQueryIterator( String database,
                                                         String smarts );

        /**
         * @param database
         * @param smarts
         * @return a List of hitting molecules
         */
        @TestMethods("testListSMARTSQueryResults")
        @PublishedMethod ( params = "String database, String smarts",
                           methodSummary = "Performs a SMARTS query " +
                                           "returning a list of hitting " +
                                           "molecules in the database" )
        public List<DBMolecule> smartsQuery( String database, String smarts );

        /**
         * @param database
         * @param smarts
         * @param monitor
         * @return an iterator to hitting molecules
         */
        public Iterator<DBMolecule> smartsQueryIterator( 
            String database, String smarts, IProgressMonitor monitor );

        /**
         * @param database
         * @param smarts
         * @param monitor
         * @return a List of hitting molecules
         */
        public List<DBMolecule> smartsQuery( String database,
                                            String smarts,
                                            IProgressMonitor monitor );

        /**
         * @param listener
         */
        public void addListener( IStructureDBChangeListener listener );
        
        /**
         * @param listener
         */
        public void removeListener( IStructureDBChangeListener listener );
       
        /**
         * Deletes an annotation and all molecules having that annotation
         * @param name
         * @param annotation
         */
        @TestMethods("testDeletingAnnotationWithMolecules")
        @PublishedMethod ( params =  "String name, Annotation annotation", 
                           methodSummary = "Deletes the given annotation " +
                                           "from the specified database and all " +
                                           "molecules annotated with that " +
                                           "annotation" )
        public void deleteWithMolecules( String databaseName, 
                                         Annotation annotation );

        /**
         * @param name
         * @param annotation
         * @param monitor
         */
        public void deleteWithMolecules( String name, Annotation annotation,
                                         IProgressMonitor monitor );

        /**
         * @param databaseName
         * @param file
         * @throws BioclipseException 
         * @throws BioclipseException 
         */
        public void addMoleculesFromSDF( String databaseName, IFile file ) 
                    throws BioclipseException;

        /**
         * Gives all labels in the database with the given name. That is: all 
         * TextAnnotations with the corresponding Property being "label"
         * 
         * @param databaseName
         * @return
         */
        @TestMethods("testAllLabels")
        @PublishedMethod( params = "String databaseName",
                          methodSummary = "Returns all labels in a " +
                                          "database. " +
                                          "That is: all TextAnnotations with " +
                                          "the corresponding Property being " +
                                          "\"label\"")
        public List<TextAnnotation> allLabels( String databaseName );

        public int numberOfMoleculesInLabel( String databaseName, 
                                             TextAnnotation annotation );

        public DBMolecule moleculeAtIndexInLabel( String databaseName, 
                                                  int index, 
                                                  TextAnnotation annotation );

        public int numberOfMoleculesInDatabaseInstance( String databaseName );

        
        @PublishedMethod( params = "String databaseName, java.util.List files",
                          methodSummary = "Imports molecules from a list of " +
                                          "files containing molecules." )
        /**
         * @param dbName
         * @param files a list containing <code>String</code> or <code>IFile</code>
         */
        public void addMoleculesFromFiles( String dbName, 
                                           List<?> files );
        
        /**
         * @param dbName
         * @param files
         * @param monitor
         */
        public void addMoleculesFromFiles( String dbName, 
                                           List<?> files,
                                           IProgressMonitor monitor );

        /**
         * @param dbName
         * @param smarts
         * @param uiJob
         * @return
         */
        public void smartsQuery( String dbName,
                                 String smarts,
                                 BioclipseUIJob<List<?>> uiJob );

        /**
         * @param dbName
         * @param molecule
         * @param uijob
         */
        public void subStructureSearch( String dbName,
                                        IMolecule molecule,
                                        BioclipseUIJob<List<?>> uijob );
        
        public void deleteDatabase(String databaseName, IProgressMonitor monitor);
     
        @PublishedMethod( params = "String databaseName",
                          methodSummary = "returns an iterator to all molecules " +
                                          "in the database" )
        /**
         * @param dbName
         * 
         * @return an iterator for all molecules in the database
         */
        public Iterator<DBMolecule> allStructuresIterator( String databaseName );
        
        /**
         * @param dbName
         * @param molecule
         */
        @PublishedMethod( params = "String dbName, DBMolecule molecule",
                          methodSummary = "Persists changes on a molecule in the " +
                                          "database" )
        public void updateMolecule(String dbName, DBMolecule molecule);

        /**
         * @param database1
         * @param sdfile
         * @param monitor
         */
        public void addMoleculesFromSDF( String database1,
                                         String sdfile,
                                         NullProgressMonitor monitor );
    
        @PublishedMethod( methodSummary = "Removes all StructureDB databases" )
        public void deleteAllDatabases(); 
}

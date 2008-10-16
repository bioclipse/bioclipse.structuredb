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
package net.bioclipse.structuredb.internalbusiness;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.domain.User;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author jonalv
 *
 */
public interface IStructuredbInstanceManager {

    /**
     * Persists changes in a given library retrieved from the database.
     * 
     * @param annotation
     */
    public void update(Annotation annotation);
    
    /**
     * Persists changes in a given user retrieved from the database.
     * 
     * @param user
     */
    public void update(User user);
    
    /**
     * Persists changes in a given user retrieved from the database.
     * 
     * @param dBMolecule
     */
    public void update(DBMolecule dBMolecule);
    
    /**
     * Insert a user into the database
     * 
     * @param user to be inserted
     */
    public void insertUser( User user );
    
    /**
     * Insert a folder into the database
     * 
     * @param annotation to be inserted
     */
    public void insertAnnotation( Annotation annotation );
    
    /**
     * Insert a structure into the database
     * 
     * @param dBMolecule to be inserted
     */
    public void insertStructure( DBMolecule dBMolecule ); 
    
    /**
     * Removes the given library from the database. 
     * Doesn't delete any structures.
     * 
     * @param annotation
     */
    public void delete(Annotation annotation);
    
    /**
     * Removes the given user from the database
     * 
     * @param user
     */
    public void delete(User user);
    
    /**
     * Removes the given DBMolecule from the database
     * 
     * @param dBMolecule
     */
    public void delete(DBMolecule dBMolecule);
    
    /**
     * @return all structures
     */
    public List<DBMolecule> retrieveAllStructures();
    
    /**
     * @return all libraries
     */
    public List<Annotation> retrieveAllAnnotations();
    
    /**
     * @return all users
     */
    public List<User> retrieveAllUsers();
    
    /**
     * Loads the user with the given username from the database
     * 
     * @param username
     * @return a user or null if no such user exists
     */
    public User retrieveUserByUsername(String username);
    
    /**
     * Loads all structures with a given name from the database
     * 
     * @param name
     * @return all structures with the given name
     */
    public List<DBMolecule> retrieveStructureByName(String name);

    /**
     * @return the logged in user
     */
    public User getLoggedInUser();
    
    /**
     * @param user to set as logged in
     */
    public void setLoggedInUser(User user);

    /**
     * @return iterator for all structures in the database
     */
    public Iterator<DBMolecule> allStructuresIterator();

    /**
     * Inserts structure in the folder with the given id
     * 
     * @param s
     * @param folderId
     */
    public void insertStructureInAnnotation( DBMolecule s, 
                                             String folderId );

    /**
     * Returns an int representing the number of structures 
     * in the database
     * 
     * @return
     */
    public int numberOfStructures();

    /**
     * @param s
     * @return an iterator to the structures with a for substructure
     * matching fingerprint
     */
    public Iterator<DBMolecule> fingerprintSubstructureSearchIterator(
        DBMolecule s );
    
    /**
     * @param queryStructure
     * @return the number of structures in the databaes matching a 
     *         fingerprintsubstructure search
     */
    public int numberOfFingerprintMatches( DBMolecule queryStructure );

    
    /**
     * Deletes the given label from the database
     * @param annotation
     * @param monitor 
     */
    public void deleteWithStructures( Annotation annotation, 
                                      IProgressMonitor monitor );
    
    /**
     * Insert textproperty into database
     * 
     * @param textProperty
     */
    public void insertTextProperty( TextProperty textProperty );

    
    /**
     * Insert choiceProperty into database
     * 
     * @param choiceProperty
     */
    public void insertChoiceProperty( ChoiceProperty choiceProperty );

    /**
     * Insert realNumberProperty into database
     * 
     * @param realNumberProperty
     */
    public void insertRealNumberProperty( 
        RealNumberProperty realNumberProperty );

    /**
     * Insert choiceAnnotation into database
     * 
     * @param choiceAnnotation
     */
    public void insertChoiceAnnotation( ChoiceAnnotation choiceAnnotation );

    /**
     * Insert realNumberAnnotation into database
     * 
     * @param realNumberAnnotation
     */
    public void insertRealNumberAnnotation( 
        RealNumberAnnotation realNumberAnnotation );

    /**
     * Insert textAnnotation into database
     * 
     * @param textAnnotation
     */
    public void insertTextAnnotation( TextAnnotation textAnnotation );

    /**
     * Delete choiceProperty from the database
     * 
     * @param choiceProperty
     */
    public void delete( ChoiceProperty choiceProperty );

    /**
     * Delete realNumberProperty from the database
     * 
     * @param realNumberProperty
     */
    public void delete( RealNumberProperty realNumberProperty );

    /**
     * Delete textProperty from database
     * 
     * @param textProperty
     */
    public void delete( TextProperty textProperty );
}

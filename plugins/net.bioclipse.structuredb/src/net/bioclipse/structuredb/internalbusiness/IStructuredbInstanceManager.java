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
import net.bioclipse.structuredb.domain.Structure;
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
     * @param structure
     */
    public void update(Structure structure);
    
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
     * @param structure to be inserted
     */
    public void insertStructure( Structure structure ); 
    
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
     * Removes the given Structure from the database
     * 
     * @param structure
     */
    public void delete(Structure structure);
    
    /**
     * @return all structures
     */
    public List<Structure> retrieveAllStructures();
    
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
    public List<Structure> retrieveStructureByName(String name);
    
    /**
     * Loads the Annotation with the given name from the database
     * 
     * @param name
     * @return a Annotation or null if no such Annotation exists
     */
    public Annotation retrieveAnnotationByName(String name);

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
    public Iterator<Structure> allStructuresIterator();

    /**
     * Inserts structure in the folder with the given id
     * 
     * @param s
     * @param folderId
     */
    public void insertStructureInAnnotation( Structure s, 
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
    public Iterator<Structure> fingerprintSubstructureSearchIterator(
        Structure s );
    
    /**
     * @param queryStructure
     * @return the number of structures in the databaes matching a 
     *         fingerprintsubstructure search
     */
    public int numberOfFingerprintMatches( Structure queryStructure );

    
    /**
     * Deletes the given label from the database
     * @param annotation
     * @param monitor 
     */
    public void deleteWithStructures( Annotation annotation, 
                                      IProgressMonitor monitor );
}

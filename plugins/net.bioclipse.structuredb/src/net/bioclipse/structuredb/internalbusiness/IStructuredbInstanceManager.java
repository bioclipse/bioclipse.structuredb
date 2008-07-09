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
package net.bioclipse.structuredb.internalbusiness;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openscience.cdk.AtomContainer;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

/**
 * @author jonalv
 *
 */
public interface IStructuredbInstanceManager {

    /**
     * Persists changes in a given library retrieved from the database.
     * 
     * @param label
     */
    public void update(Label label);
    
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
     * @param label to be inserted
     */
    public void insertLabel( Label label );
    
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
     * @param label
     */
    public void delete(Label label);
    
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
    public List<Label> retrieveAllLabels();
    
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
     * Loads the Label with the given name from the database
     * 
     * @param name
     * @return a Label or null if no such Label exists
     */
    public Label retrieveLabelByName(String name);

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
    public void insertStructureInLabel( Structure s, String folderId );

    /**
     * Returns an int representing the number of structures in the database
     * 
     * @return
     */
    public int numberOfStructures();

    /**
     * @param s
     * @return an iterator to the structures with a for substructure
     * matching fingerprint
     */
    public Iterator<Structure> fingerprintSubstructureSearchIterator(Structure s);

    
    /**
     * @param queryStructure
     * @return the number of structures in the databaes matching a 
     *         fingerprintsubstructure search
     */
    public int numberOfFingerprintMatches( Structure queryStructure );

    
    /**
     * Deletes the given label from the database
     * @param label
     * @param monitor 
     */
    public void deleteWithStructures( Label label, IProgressMonitor monitor );
}

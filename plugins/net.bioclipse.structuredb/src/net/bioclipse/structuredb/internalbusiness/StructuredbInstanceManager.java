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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

public class StructuredbInstanceManager 
       extends AbstractStructuredbInstanceManager 
       implements IStructuredbInstanceManager {

    private User loggedInUser;
    
    public Label createLabel(String name) {
        Label label = new Label(name);
        labelDao.insert(label);
        return label;
    }

    public void insertLabel(Label label) {
        labelDao.insert(label);
        persistRelatedStructures(label);
    }
    
    private void persistRelatedStructures(Label label) {
        for( Structure s : label.getStructures() ) {
            if( structureDao.getById(s.getId()) == null) {
                structureDao.insert(s);
            }
            else {
                structureDao.update(s);
            }
        }
    }

    public void insertStructure(Structure structure) {
        structureDao.insert(structure);
    }

    public void insertUser(User user) {
        userDao.insert(user);
    }

    public void delete(Label label) {
        labelDao.delete( label.getId() );
    }

    public void delete(User user) {
        userDao.delete( user.getId() );
    }

    public void delete(Structure structure) {
        structureDao.delete( structure.getId() );
    }

    public List<Label> retrieveAllLabels() {
        return new BioList<Label>( labelDao.getAll() );
    }

    public List<Structure> retrieveAllStructures() {
        return new BioList<Structure>( structureDao.getAll() );
    }

    public List<User> retrieveAllUsers() {
        return new BioList<User>( userDao.getAll() );
    }

    public Label retrieveLabelByName(String name) {
        return labelDao.getByName(name);
    }

    public List<Structure> retrieveStructureByName(String name) {
        return structureDao.getByName(name);
    }

    public User retrieveUserByUsername(String username) {
        return userDao.getByUserName(username);
    }

    public void update(Label label) {
        labelDao.update(label);
        persistRelatedStructures(label);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void update(Structure structure) {
        structureDao.update(structure);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public Iterator<Structure> allStructuresIterator() {

        return structureDao.allStructuresIterator();
    }

    public void insertStructureInLabel( Structure s, String folderId ) {

        structureDao.insertWithLabel( s, folderId );
    }

    public int numberOfStructures() {

        return structureDao.numberOfStructures();
    }

    public Iterator<Structure> 
           fingerprintSubstructureSearchIterator(Structure s) {

        return structureDao
               .fingerPrintSubsetSearch( s.getPersistedFingerprint() );
    }

    public int numberOfFingerprintMatches( Structure queryStructure ) {

        return structureDao.numberOfFingerprintSubstructureMatches( 
            queryStructure.getPersistedFingerprint() );
    }

    public void deleteWithStructures( Label label, 
                                      IProgressMonitor monitor ) {

        int ticks = 1000;
        if(monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask( "Deleting Structures", ticks); 
        IProgressMonitor sub 
            = new SubProgressMonitor(monitor, (int) (0.1 * ticks));
        sub.beginTask( "Preparing to delete", 1 );
        List<Structure> structures = label.getStructures();
        int tick = ticks / label.getStructures().size();
        sub.worked( 1 );
        sub.done();
        for ( Structure s : structures ) {
            structureDao.delete( s.getId() );
            monitor.worked( tick );
        }
        labelDao.delete( label.getId() );
        monitor.done();
    }
}

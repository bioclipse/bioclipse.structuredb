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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.User;

/**
 * @author jonalv
 *
 */
public class StructuredbInstanceManager 
       extends AbstractStructuredbInstanceManager 
       implements IStructuredbInstanceManager {

    private User loggedInUser;

    public void insertAnnotation(Annotation annotation) {
        annotationDao.insert(annotation);
        persistRelatedStructures(annotation);
    }
    
    private void persistRelatedStructures(Annotation annotation) {
        for( DBMolecule s : annotation.getDBMolecules() ) {
            if( dBMoleculeDao.getById(s.getId()) == null) {
                dBMoleculeDao.insert(s);
            }
            else {
                dBMoleculeDao.update(s);
            }
        }
    }

    public void insertStructure(DBMolecule dBMolecule) {
        dBMoleculeDao.insert(dBMolecule);
    }

    public void insertUser(User user) {
        userDao.insert(user);
    }

    public void delete(Annotation annotation) {
        annotationDao.delete( annotation.getId() );
    }

    public void delete(User user) {
        userDao.delete( user.getId() );
    }

    public void delete(DBMolecule dBMolecule) {
        dBMoleculeDao.delete( dBMolecule.getId() );
    }

    public List<Annotation> retrieveAllAnnotations() {
        return new BioList<Annotation>( annotationDao.getAll() );
    }

    public List<DBMolecule> retrieveAllStructures() {
        return new BioList<DBMolecule>( dBMoleculeDao.getAll() );
    }

    public List<User> retrieveAllUsers() {
        return new BioList<User>( userDao.getAll() );
    }

    public List<DBMolecule> retrieveStructureByName(String name) {
        return dBMoleculeDao.getByName(name);
    }

    public User retrieveUserByUsername(String username) {
        return userDao.getByUserName(username);
    }

    public void update(Annotation annotation) {
        annotationDao.update(annotation);
        persistRelatedStructures(annotation);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void update(DBMolecule dBMolecule) {
        dBMoleculeDao.update(dBMolecule);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public Iterator<DBMolecule> allStructuresIterator() {

        return dBMoleculeDao.allStructuresIterator();
    }

    public void insertStructureInAnnotation( DBMolecule s, 
                                             String folderId ) {

        dBMoleculeDao.insertWithAnnotation( s, folderId );
    }

    public int numberOfStructures() {

        return dBMoleculeDao.numberOfStructures();
    }

    public Iterator<DBMolecule> 
           fingerprintSubstructureSearchIterator(DBMolecule s) {

        return dBMoleculeDao
               .fingerPrintSubsetSearch( s.getPersistedFingerprint() );
    }

    public int numberOfFingerprintMatches( DBMolecule queryStructure ) {

        return dBMoleculeDao.numberOfFingerprintSubstructureMatches( 
            queryStructure.getPersistedFingerprint() );
    }

    public void deleteWithStructures( Annotation annotation, 
                                      IProgressMonitor monitor ) {

        int ticks = 1000;
        if(monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask( "Deleting Structures", ticks); 
        IProgressMonitor sub 
            = new SubProgressMonitor(monitor, (int) (0.1 * ticks));
        sub.beginTask( "Preparing to delete", 1 );
        List<DBMolecule> dBMolecules = annotation.getDBMolecules();
        int molecules = annotation.getDBMolecules().size();
        int tick = molecules <= 0 ? 0 : ticks / molecules;
        sub.worked( 1 );
        sub.done();
        for ( DBMolecule s : dBMolecules ) {
            dBMoleculeDao.delete( s.getId() );
            monitor.worked( tick );
        }
        annotationDao.delete( annotation.getId() );
        monitor.done();
    }
}

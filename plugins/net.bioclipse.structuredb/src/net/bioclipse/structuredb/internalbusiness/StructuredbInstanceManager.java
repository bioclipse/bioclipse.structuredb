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
import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.Property;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.persistency.dao.AnnotationDao;
import net.bioclipse.structuredb.persistency.dao.ChoicePropertyDao;

/**
 * @author jonalv
 *
 */
public class StructuredbInstanceManager 
       extends AbstractStructuredbInstanceManager 
       implements IStructuredbInstanceManager {

    private User loggedInUser;

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

    public void insertMolecule(DBMolecule dBMolecule) {
        dBMoleculeDao.insert(dBMolecule);
    }

    public void insertUser(User user) {
        userDao.insert(user);
    }

    public void delete(User user) {
        userDao.delete( user.getId() );
    }

    public void delete(DBMolecule dBMolecule) {
        dBMoleculeDao.delete( dBMolecule.getId() );
    }

    public List<Annotation> retrieveAllAnnotations() {
        BioList<Annotation> result = new BioList<Annotation>();
        result.addAll( textAnnotationDao.getAll()       );
        result.addAll( realNumberAnnotationDao.getAll() );
        result.addAll( choiceAnnotationDao.getAll()     );
        return result;
    }

    public List<DBMolecule> retrieveAllMolecules() {
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

    public void insertMoleculeInAnnotation( DBMolecule s, 
                                            String folderId ) {

        dBMoleculeDao.insertWithAnnotation( s, folderId );
    }

    public int numberOfMolecules() {

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

    public void deleteWithMolecules( Annotation annotation, 
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
        if ( annotation instanceof ChoiceAnnotation) {
            choiceAnnotationDao.delete( annotation.getId() );
        }
        else if ( annotation instanceof RealNumberAnnotation ) {
            realNumberAnnotationDao.delete( annotation.getId() );
        }
        else if ( annotation instanceof TextAnnotation ) {
            textAnnotationDao.delete( annotation.getId() );
        }
        monitor.done();
    }

    public void insertChoiceAnnotation( ChoiceAnnotation choiceAnnotation ) {
        choiceAnnotationDao.insert( choiceAnnotation );
    }

    public void insertChoiceProperty( ChoiceProperty choiceProperty ) {
        choicePropertyDao.insert( choiceProperty );
    }

    public void insertRealNumberAnnotation(
        RealNumberAnnotation realNumberAnnotation ) {

        realNumberAnnotationDao.insert( realNumberAnnotation );
    }

    public void insertRealNumberProperty( 
        RealNumberProperty realNumberProperty ) {

        realNumberPropertyDao.insert( realNumberProperty );
    }

    public void insertTextAnnotation( TextAnnotation textAnnotation ) {
        textAnnotationDao.insert( textAnnotation );
    }

    public void insertTextProperty( TextProperty textProperty ) {
        textPropertyDao.insert( textProperty );
    }

    public void delete( ChoiceProperty choiceProperty ) {
        choicePropertyDao.delete( choiceProperty.getId() );
    }

    public void delete( RealNumberProperty realNumberProperty ) {
        realNumberPropertyDao.delete( realNumberProperty.getId() );
    }

    public void delete( TextProperty textProperty ) {
        textPropertyDao.delete( textProperty.getId() );
    }

    public void update( ChoiceProperty choiceProperty ) {
        choicePropertyDao.update( choiceProperty );
    }

    public void update( RealNumberProperty realNumberProperty ) {
        realNumberPropertyDao.update( realNumberProperty );
    }

    public void update( TextProperty textProperty ) {
        textPropertyDao.update( textProperty );
    }

    public void delete( Annotation annotation ) {
        if ( annotation instanceof ChoiceAnnotation ) {
            choiceAnnotationDao.delete( annotation.getId() );
        }
        else if ( annotation instanceof RealNumberAnnotation ) {
            realNumberPropertyDao.delete( annotation.getId() );
        }
        else if ( annotation instanceof TextAnnotation ) {
            textAnnotationDao.delete( annotation.getId() );
        }
    }

    public void update( ChoiceAnnotation choiceAnnotation ) {
        choiceAnnotationDao.update( choiceAnnotation );
    }

    public void update( RealNumberAnnotation realNumberAnnotation ) {
        realNumberAnnotationDao.update( realNumberAnnotation );
    }

    public void update( TextAnnotation textAnnotation ) {
        textAnnotationDao.update( textAnnotation );
    }

    public Property retrievePropertyByName( String propertyName ) {
        return fallback( 
                   fallback( choicePropertyDao.getByName(propertyName), 
                             realNumberPropertyDao.getByName(propertyName) ), 
                   textPropertyDao.getByName(propertyName) ); 
    }

    private Property fallback(Property p1, Property p2){
        return p1 != null ? p1 : p2 ;
    }
}

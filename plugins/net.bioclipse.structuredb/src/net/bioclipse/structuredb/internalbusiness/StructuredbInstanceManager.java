/* *****************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.bioclipse.core.domain.RecordableList;
import net.bioclipse.core.util.TimeCalculator;
import net.bioclipse.structuredb.FileStoreKeeper;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.Property;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.persistency.dao.AnnotationDao;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author jonalv
 *
 */
public class StructuredbInstanceManager 
       extends AbstractStructuredbInstanceManager 
       implements IStructuredbInstanceManager {

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

    public void delete(DBMolecule dBMolecule) {
        dBMoleculeDao.delete( dBMolecule.getId() );
        FileStoreKeeper.FILE_STORE
                       .delete( UUID.fromString( 
                                         dBMolecule.getFileStoreKey() ) );
    }

    public List<Annotation> retrieveAllAnnotations() {
        RecordableList<Annotation> result = new RecordableList<Annotation>();
        result.addAll( textAnnotationDao.getAll()       );
        result.addAll( realNumberAnnotationDao.getAll() );
        return result;
    }

    public List<DBMolecule> retrieveAllMolecules() {
        return new RecordableList<DBMolecule>( dBMoleculeDao.getAll() );
    }

    public List<DBMolecule> retrieveStructureByName(String name) {
        return dBMoleculeDao.getByName(name);
    }

    public void update(DBMolecule dBMolecule) {
        dBMoleculeDao.update(dBMolecule);
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
        // any annotaion dao would do so just picked one...
        realNumberAnnotationDao.deleteWithStructures( annotation, monitor );
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

    public void delete( RealNumberProperty realNumberProperty ) {
        realNumberPropertyDao.delete( realNumberProperty.getId() );
    }

    public void delete( TextProperty textProperty ) {
        textPropertyDao.delete( textProperty.getId() );
    }

    public void update( RealNumberProperty realNumberProperty ) {
        realNumberPropertyDao.update( realNumberProperty );
    }

    public void update( TextProperty textProperty ) {
        textPropertyDao.update( textProperty );
    }

    public void delete( Annotation annotation ) {
        if ( annotation instanceof RealNumberAnnotation ) {
            realNumberPropertyDao.delete( annotation.getId() );
        }
        else if ( annotation instanceof TextAnnotation ) {
            textAnnotationDao.delete( annotation.getId() );
        }
    }

    public void update( RealNumberAnnotation realNumberAnnotation ) {
        realNumberAnnotationDao.update( realNumberAnnotation );
    }

    public void update( TextAnnotation textAnnotation ) {
        textAnnotationDao.update( textAnnotation );
    }

    public Property retrievePropertyByName( String propertyName ) {
        return fallback( realNumberPropertyDao.getByName(propertyName) ,
                         textPropertyDao.getByName(propertyName) ); 
    }

    private Property fallback(Property p1, Property p2){
        return p1 != null ? p1 : p2 ;
    }

    public List<TextAnnotation> allLabels() {
        return textAnnotationDao.getAllLabels(); 
    }

    public DBMolecule moleculeAtIndexInLabel( int index, 
                                              TextAnnotation annotation ) {

        return dBMoleculeDao.getMoleculeAtIndexInLabel( annotation, index );
    }

    public int numberOfMoleculesInLabel( TextAnnotation annotation ) {

        return dBMoleculeDao.getNumberOfMoleculesWithAnnotation( annotation );
    }

    public void dropDataBase( IProgressMonitor monitor ) {

        Iterator<DBMolecule> iterator = dBMoleculeDao.allStructuresIterator();
        int ticks = dBMoleculeDao.numberOfStructures();
        monitor.beginTask( "Dropping database", ticks );
        long startTime = System.currentTimeMillis();
        int i = 0;
        while ( iterator.hasNext() ) {
            FileStoreKeeper.FILE_STORE
                           .delete( UUID.fromString( 
                                        iterator.next().getFileStoreKey() ) );
            monitor.worked( 1 );
            monitor.subTask( "(Estimating " + 
                TimeCalculator.generateTimeRemainEst( startTime, 
                                                      i++, 
                                                      ticks ) + " remaining" );
        }
        monitor.subTask( "Almost done" );
    }

    public void annotate( DBMolecule s, Annotation a ) {

       dBMoleculeDao.annotate(s, a); 
    }

    public Collection<String> 
           getAvailableProperties( TextAnnotation annotation ) {

//        return textAnnotationDao.getAvailableProperties( annotation );
        Collection<String> results = new ArrayList<String>();
        for ( TextProperty p : textPropertyDao.getAll() ) {
            results.add( p.getName() );
        }
        for ( RealNumberProperty p : realNumberPropertyDao.getAll() ) {
            results.add( p.getName() );
        }
        return results;
    }

    public Annotation getAnnotationById( String id ) {
        Annotation result;
        result = textAnnotationDao.getById( id );
        if ( result != null ) {
            return result;
        }
        result = realNumberAnnotationDao.getById( id );
        if ( result != null ) {
            return result;
        }
        return null;
    }
}

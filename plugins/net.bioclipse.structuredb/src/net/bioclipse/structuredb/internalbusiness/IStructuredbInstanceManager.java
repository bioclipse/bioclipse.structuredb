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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.Property;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author jonalv
 *
 */
public interface IStructuredbInstanceManager {

    /**
     * Persists changes in a given molecule from the database.
     * 
     * @param dBMolecule
     */
    public void update(DBMolecule dBMolecule);
    
    /**
     * Insert a structure into the database
     * 
     * @param dBMolecule to be inserted
     */
    public void insertMolecule( DBMolecule dBMolecule ); 
    
    /**
     * Removes the given library from the database. 
     * Doesn't delete any structures.
     * 
     * @param annotation
     */
    public void delete(Annotation annotation);
    
    /**
     * Removes the given DBMolecule from the database
     * 
     * @param dBMolecule
     */
    public void delete(DBMolecule dBMolecule);
    
    /**
     * @return all structures
     */
    public List<DBMolecule> retrieveAllMolecules();
    
    /**
     * @return all libraries
     */
    public List<Annotation> retrieveAllAnnotations();
    
    /**
     * Loads all structures with a given name from the database
     * 
     * @param name
     * @return all structures with the given name
     */
    public List<DBMolecule> retrieveStructureByName(String name);

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
    public void insertMoleculeInAnnotation( DBMolecule s, 
                                             String folderId );

    /**
     * Returns an int representing the number of structures 
     * in the database
     * 
     * @return
     */
    public int numberOfMolecules();

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
    public void deleteWithMolecules( Annotation annotation, 
                                      IProgressMonitor monitor );
    
    /**
     * Insert textproperty into database
     * 
     * @param textProperty
     */
    public void insertTextProperty( TextProperty textProperty );

    /**
     * Insert realNumberProperty into database
     * 
     * @param realNumberProperty
     */
    public void insertRealNumberProperty( 
        RealNumberProperty realNumberProperty );

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

    /**
     * Update realNumberProperty in database
     * 
     * @param realNumberProperty
     */
    public void update( RealNumberProperty realNumberProperty );

    /**
     * Update textProperty in database
     * 
     * @param textProperty
     */
    public void update( TextProperty textProperty );

    public void update( RealNumberAnnotation realNumberAnnotation );

    public void update( TextAnnotation textAnnotation );

    public Property retrievePropertyByName( String propertyName );

    public List<TextAnnotation> allLabels();

    public DBMolecule moleculeAtIndexInLabel( int index, 
                                              TextAnnotation annotation );

    public int numberOfMoleculesInLabel( TextAnnotation annotation );

    /**
     * @param monitor 
     * 
     */
    public void dropDataBase(IProgressMonitor monitor);

    /**
     * Makes the DBMolecule s annotated with the Annotation a in the database. 
     * Used when it is important that the Annotation does not keep a complete
     * list of all molecules with it.
     * 
     * @param s
     * @param a
     */
    public void annotate( DBMolecule s, Annotation a );

    /**
     * @param annotation
     * @return
     */
    public Collection<String> 
           getAvailableProperties( TextAnnotation annotation );

    /**
     * @param id
     * @return
     */
    public Annotation getAnnotationById( String id );
    
}

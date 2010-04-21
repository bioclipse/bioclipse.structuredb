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
package net.bioclipse.structuredb.persistency.dao;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;

/**
 * @author jonalv
 */
public interface IDBMoleculeDao extends IGenericDao<DBMolecule> {

    public List<DBMolecule> getByName(String name);
    
    /** returns an iterator for all structures in the database */
    public Iterator<DBMolecule> allStructuresIterator();

    /**
     * Inserts the structure into the database and the annotation 
     * with the given id
     * 
     * @param s
     * @param annotationId
     */
    public void insertWithAnnotation( DBMolecule s, String annotationId );

    /**
     * @return number of structures in the database
     */
    public int numberOfStructures();

    
    /**
     * @param fingerprint
     * @return iterator for all subset matching structures
     */
    public Iterator<DBMolecule> fingerPrintSubsetSearch( 
        byte[] fingerprint );

    /**
     * The number of matching substructures fingerprints in the database
     */
    public int numberOfFingerprintSubstructureMatches(
        byte[] fingerPrint );
    
    public DBMolecule getMoleculeAtIndexInLabel( TextAnnotation label, 
                                                 int index );

    public int getNumberOfMoleculesWithAnnotation( Annotation label );

    /**
     * @param s
     * @param a
     */
    public void annotate( DBMolecule s, Annotation a );
}

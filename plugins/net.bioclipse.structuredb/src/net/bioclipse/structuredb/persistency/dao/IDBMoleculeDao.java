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
package net.bioclipse.structuredb.persistency.dao;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.DBMolecule;

/**
 * @author jonalv
 */
public interface IDBMoleculeDao extends IGenericDao<DBMolecule> {

    public List<DBMolecule> getByName(String name);
    
    /** returns an iterator for all structures in the database */
    public Iterator<DBMolecule> allStructuresIterator();

    /**
     * Inserts the structure into the database and the folder 
     * with the given id
     * 
     * @param s
     * @param folderId
     */
    public void insertWithAnnotation( DBMolecule s, String folderId );

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
}

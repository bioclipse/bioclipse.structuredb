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
package net.bioclipse.structuredb.persistency.dao;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.Structure;

/**
 * @author jonalv
 */
public interface IStructureDao extends IGenericDao<Structure> {

    public List<Structure> getByName(String name);
    
    /** returns an iterator for all structures in the database */
    public Iterator<Structure> allStructuresIterator();

    /**
     * Inserts the structure into the database and the folder with the given id
     * 
     * @param s
     * @param folderId
     */
    public void insertInFolder( Structure s, String folderId );
}

/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb;

import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.domain.Annotation;

/*
 * TODO: This implementation loads all molecules from the database 
 * directly and saves all molecules back. There is very much room for 
 * optimisation by only loading the needed molecules and only saving the 
 * edited ones.  
 */

/**
 * @author jonalv
 *
 */
public class DBMoleculesEditorModel implements IMoleculesEditorModel {

    private IStructuredbManager structuredb = Activator
                                              .getDefault()
                                              .getStructuredbManager();
    private List<? extends ICDKMolecule> list;
    private Annotation annotation;
    private String databaseName;
    
    public DBMoleculesEditorModel( String databaseName, 
                                   String annotationName ) {
        annotation = structuredb.annotationByName( databaseName, 
                                                   annotationName );
        list = annotation.getDBMolecules();
        this.databaseName = databaseName;
    }
    
    public Object getMoleculeAt( int index ) {
        return list.get( index );
    }

    public int getNumberOfMolecules() {
        return list.size();
    }

    public void save() {
        structuredb.save( databaseName, annotation );
    }
}

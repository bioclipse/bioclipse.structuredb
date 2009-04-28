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
import net.bioclipse.cdk.domain.SDFElement;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;

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
    private TextAnnotation annotation;
    private String databaseName;
    
    public DBMoleculesEditorModel( String databaseName, 
                                   TextAnnotation annotation ) {
        this.annotation = annotation;
        this.databaseName = databaseName;
    }
    
    public ICDKMolecule getMoleculeAt( int index ) {
        final DBMolecule mol= structuredb.moleculeAtIndexInLabel( databaseName, 
                                                                  index, 
                                                                  annotation );
        return mol;
    }

    public int getNumberOfMolecules() {
        return structuredb.numberOfMoleculesInLabel(databaseName, annotation);
    }

    public void save( int index, ICDKMolecule moleculeToSave ) {

        // TODO Implement this method

    }
    public void save() {
        structuredb.save( databaseName, annotation );
    }
}

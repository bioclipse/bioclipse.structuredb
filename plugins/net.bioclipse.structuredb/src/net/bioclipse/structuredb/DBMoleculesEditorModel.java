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
package net.bioclipse.structuredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;

/**
 * @author jonalv
 *
 */
public class DBMoleculesEditorModel implements IMoleculesEditorModel {

    private IJavaStructuredbManager structuredb 
        = Activator.getDefault().getStructuredbManager();
    private TextAnnotation annotation;
    private String databaseName;
    private int numberOfMolecules = -1;
    
    public DBMoleculesEditorModel( String databaseName,
                                   TextAnnotation annotation ) {
        this.annotation = annotation;
        this.databaseName = databaseName;
    }
    
    public ICDKMolecule getMoleculeAt( int index ) {
        final DBMolecule mol = structuredb.moleculeAtIndexInLabel( databaseName,
                                                                   index,
                                                                   annotation );
        return mol;
    }

    public int getNumberOfMolecules() {
        if ( numberOfMolecules == -1 ) {
            numberOfMolecules = structuredb.numberOfMoleculesInLabel(
                                    databaseName, 
                                    annotation);
        }
        return numberOfMolecules;
    }

    @Override
    public boolean isDirty( int index ) {

        // TODO Auto-generated method stub
        return false;
    }
    public void markDirty( int index, ICDKMolecule moleculeToSave ) {

        // TODO Implement this method

    }

    public void save() {
        structuredb.save( databaseName, annotation );
    }

    public Collection<Object> getAvailableProperties() {
//        return new LinkedList();
        return structuredb.getAvailableProperties(databaseName, annotation);
    }

    public <T> void setPropertyFor( int index, String property, T value ) {
        throw new UnsupportedOperationException();

    }

    public void delete( int index ) {
        throw new UnsupportedOperationException();
    }

    public void instert( ICDKMolecule... molecules ) {
        throw new UnsupportedOperationException();
    }
}

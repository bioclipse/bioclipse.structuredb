/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/

package net.bioclipse.structuredb.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jonalv
 */
public abstract class Annotation extends BaseObject {

    private List<DBMolecule> dBMolecules;

    public abstract String getSortOf();
    
    public Annotation() {
        super();
        dBMolecules = new ArrayList<DBMolecule>();
    }

    public Annotation(Annotation annotation) {
        super(annotation);
        this.dBMolecules 
            = new ArrayList<DBMolecule>( annotation.getDBMolecules() );
    }

    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof Annotation) ) {
            return false;
        }
        Annotation annotation = (Annotation)obj;
        return objectsInHasSameValues( annotation.getDBMolecules(), 
                                       dBMolecules );
    }

    /**
     * @return
     */
    public List<DBMolecule> getDBMolecules() {
        return dBMolecules;
    }

    /**
     * @param dBMolecules the dBMolecules to set
     */
    public void setDBMolecules(List<DBMolecule> dBMolecules) {
        this.dBMolecules = dBMolecules;
    }

    /**
     * @param dBMolecule
     */
    public void addDBMolecule(DBMolecule dBMolecule) {
        dBMolecules.add(dBMolecule);
        if ( dBMolecule != null && 
             !dBMolecule.getAnnotations().contains( this ) ) {
            
            dBMolecule.addAnnotation(this);
        }
    }

    /**
     * Removes this annotation from a molecule
     * 
     * @param dBMolecule the molecule to remove
     */
    public void removeDBMolecule(DBMolecule dBMolecule) {
        dBMolecules.remove(dBMolecule);
        if( dBMolecule != null ) {
            dBMolecule.getAnnotations().remove( this );
        }
    }

    /**
     * @return the Annotations property
     */
    public abstract Property getProperty();

    public abstract Object getValue();
}

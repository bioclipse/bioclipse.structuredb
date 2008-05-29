/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jonathan Alvarsson
 *     
 *******************************************************************************/

package net.bioclipse.structuredb.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jonalv
 */
public class Label extends BaseObject {

    private List<Structure> structures;
    
    public Label() {
        super();
        structures = new ArrayList<Structure>();
    }

    public Label(Label label) {
        super(label);
        this.structures = new ArrayList<Structure>( label.getStructures() );
    }

    public Label(String name) {
        super(name);
        structures = new ArrayList<Structure>();
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.structuredb.domain.BaseObject#hasValuesEqualTo(net.bioclipse.structuredb.domain.BaseObject)
     */
    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof Label) ) {
            return false;
        }
        Label label = (Label)obj;
        return objectsInHasSameValues(label.getStructures(), structures);
    }

    /**
     * @return the structures having the label 
     */
    public List<Structure> getStructures() {
        return structures;
    }

    /**
     * @param structures the structures to set
     */
    public void setStructures(List<Structure> structures) {
        this.structures = structures;
    }

    /**
     * Gives a structure this label
     * 
     * @param structure the structure to add
     */
    public void addStructure(Structure structure) {
        structures.add(structure);
        if( structure.getLabel() != this ) {
            structure.setLabel(this);
        }
    }

    /**
     * Removes this label from a structure
     * 
     * @param structure the structure to remove
     */
    public void removeStructure(Structure structure) {
        structures.remove(structure);
        if( structure.getLabel() != null ) {
            structure.setLabel(null);
        }
    }
}

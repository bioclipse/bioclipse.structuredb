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

package net.bioclipse.structuredb.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.AtomContainer;

public class Library extends BaseObject {

	private List<Structure> structures;
	
	public Library() {
		super();
		structures = new ArrayList<Structure>();
	}

	public Library(Library library) {
		super(library);
		this.structures = new ArrayList<Structure>( library.getStructures() );
	}

	public Library(String name) {
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
		if( !(obj instanceof Library) ) {
			return false;
		}
		Library library = (Library)obj;
		return objectsInHasSameValues(library.getStructures(), structures);
	}

	/**
	 * @return the structures in the library 
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
	 * Adds a structure to the library
	 * 
	 * @param structure the structure to add
	 */
	public void addStructure(Structure structure) {
		structures.add(structure);
		if( structure.getLibrary() != this ) {
			structure.setLibrary(this);
		}
	}

	/**
	 * Removes a structure from the library
	 * 
	 * @param structure the structure to remove
	 */
	public void removeStructure(Structure structure) {
		structures.remove(structure);
		if( structure.getLibrary() != null ) {
			structure.setLibrary(null);
		}
	}
}

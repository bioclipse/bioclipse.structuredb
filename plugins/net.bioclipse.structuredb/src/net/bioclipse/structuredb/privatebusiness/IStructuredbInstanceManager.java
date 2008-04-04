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
package net.bioclipse.structuredb.privatebusiness;

import java.util.List;

import net.bioclipse.structuredb.StructuredbDataSource;
import net.bioclipse.structuredb.domain.Library;

public interface IStructuredbInstanceManager {

	/**
	 * Get all libraries in the given structure database
	 * 
	 * @param database database to look in
	 * @return all libraries in the database
	 */
	public List<Library> getAllLibraries( StructuredbDataSource database );
}

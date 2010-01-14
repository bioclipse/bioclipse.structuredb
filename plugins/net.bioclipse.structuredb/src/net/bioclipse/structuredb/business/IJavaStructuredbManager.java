/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.TestClasses;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.structuredb.domain.TextAnnotation;

/**
 * @author jonalv
 */
@PublishedClass ("Handles structure databases")
@TestClasses("net.bioclipse.structuredb.business.StructuredbManagerTest," + 
             "net.bioclipse.structuredb.business.CoverageTest," + 
             "net.bioclipse.structuredb.business.BioclipseManagerTests")
public interface IJavaStructuredbManager extends IStructuredbManager,
                                                 IBioclipseManager {

}

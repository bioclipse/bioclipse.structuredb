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

import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.TextAnnotation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

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

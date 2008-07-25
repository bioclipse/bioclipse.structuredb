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
package net.bioclipse.structuredb;

import net.bioclipse.structuredb.business.BusinessTestsSuite;
import net.bioclipse.structuredb.domain.DomainTestsSuite;
import net.bioclipse.structuredb.internalbusiness.InternalBusinessTestsSuite;
import net.bioclipse.structuredb.persistency.PersistencyTestsSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { BusinessTestsSuite.class,
                         DomainTestsSuite.class,
                         InternalBusinessTestsSuite.class, 
                         PersistencyTestsSuite.class } )
public class AllStructuredbTestsSuite {

}

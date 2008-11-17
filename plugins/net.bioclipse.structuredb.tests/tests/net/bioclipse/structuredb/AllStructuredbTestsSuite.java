/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
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
import net.bioclipse.structuredb.domain.AllDomainTestsSuite;
import net.bioclipse.structuredb.internalbusiness.InternalBusinessTestsSuite;
import net.bioclipse.structuredb.persistence.AllPersistencyTestsSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author jonalv
 *
 */
@RunWith(value=Suite.class)
@SuiteClasses( value = { BusinessTestsSuite.class,
                         AllDomainTestsSuite.class,
                         InternalBusinessTestsSuite.class, 
                         AllPersistencyTestsSuite.class, } )
public class AllStructuredbTestsSuite {

}

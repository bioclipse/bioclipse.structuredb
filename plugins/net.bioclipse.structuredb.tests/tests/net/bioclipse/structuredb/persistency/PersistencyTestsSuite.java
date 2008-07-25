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
package net.bioclipse.structuredb.persistency;

import net.bioclipse.structuredb.persistency.dao.GenericDaoTest;
import net.bioclipse.structuredb.persistency.dao.LabelDaoTest;
import net.bioclipse.structuredb.persistency.dao.StructureDaoTest;
import net.bioclipse.structuredb.persistency.dao.UserDaoTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { LabelDaoTest.class,
                         StructureDaoTest.class,
                         UserDaoTest.class, } )
public class PersistencyTestsSuite {

}

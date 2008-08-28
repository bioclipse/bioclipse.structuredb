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
package net.bioclipse.structuredb.persistence;

import net.bioclipse.structuredb.persistence.dao.AnnotationDaoTest;
import net.bioclipse.structuredb.persistence.dao.DBMoleculeDaoTest;
import net.bioclipse.structuredb.persistence.dao.UserDaoTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { AnnotationDaoTest.class,
                         DBMoleculeDaoTest.class,
                         UserDaoTest.class, } )
public class PersistencyTestsSuite {

}

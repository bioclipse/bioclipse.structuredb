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
import net.bioclipse.structuredb.domain.DomainTestsSuite;
import net.bioclipse.structuredb.internalbusiness.InternalBusinessTestsSuite;
import net.bioclipse.structuredb.persistence.PersistencyTestsSuite;
import net.bioclipse.structuredb.persistence.dao.ChoiceAnnotationDaoTest;
import net.bioclipse.structuredb.persistence.dao.ChoicePropertyDaoTest;
import net.bioclipse.structuredb.persistence.dao.RealNumberAnnotationDaoTest;
import net.bioclipse.structuredb.persistence.dao.RealNumberPropertyDaoTest;
import net.bioclipse.structuredb.persistence.dao.TextAnnotationDaoTest;
import net.bioclipse.structuredb.persistence.dao.TextPropertyDaoTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { BusinessTestsSuite.class,
                         DomainTestsSuite.class,
                         InternalBusinessTestsSuite.class, 
                         PersistencyTestsSuite.class,
                         ChoiceAnnotationDaoTest.class,
                         ChoicePropertyDaoTest.class,
                         RealNumberAnnotationDaoTest.class,
                         RealNumberPropertyDaoTest.class,
                         TextAnnotationDaoTest.class,
                         TextPropertyDaoTest.class } )
public class AllStructuredbTestsSuite {

}

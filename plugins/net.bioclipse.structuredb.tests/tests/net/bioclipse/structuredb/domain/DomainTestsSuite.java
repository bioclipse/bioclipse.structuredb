/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { BaseObjectTest.class,
                         LabelTest.class,
                         StructureTest.class,
                         UserTest.class,
                         ChoiceAnnotationTest.class,
                         RealNumberAnnotationTest.class,
                         TextAnnotationTest.class,
                         ChoicePropertyTest.class,
                         RealNumberPropertyTest.class,
                         TextPropertyTest.class } )
public class DomainTestsSuite {

}

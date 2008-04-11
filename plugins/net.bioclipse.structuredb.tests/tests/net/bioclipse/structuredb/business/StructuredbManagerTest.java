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
package net.bioclipse.structuredb.business;

import java.io.File;

import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Folder;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class StructuredbManagerTest 
       extends AbstractDependencyInjectionSpringContextTests {

	@Override
	protected String[] getConfigLocations() {
		String loc = Structuredb.class
                                .getClassLoader()
                                .getResource(".")
                                .toString();
		loc = loc.substring(0, loc.lastIndexOf(".tests"));
		loc += File.separator 
		    + "META-INF" 
		    + File.separator 
		    + "spring" 
		    + File.separator 
		    + "context.xml";
		
		return new String[] {loc};
	}
	
	public void testCreatingTwoFolderInTwoDatabases() {
		IStructuredbManager manager 
			= (IStructuredbManager) applicationContext
			                        .getBean("structuredbManagerTarget");
		assertNotNull(manager);
		String database1 = "database1";
		String database2 = "database2";
		manager.createLocalInstance(database1);
		manager.createLocalInstance(database2);
		Folder f = manager.createFolder(database1, "testFolder1");
		assertNotNull(f);
		Folder g = manager.createFolder(database2, "testFolder2");
		assertNotNull(g);
	}
	
}

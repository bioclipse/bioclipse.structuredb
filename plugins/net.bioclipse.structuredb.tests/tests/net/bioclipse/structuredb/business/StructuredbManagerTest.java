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
import net.bioclipse.structuredb.internalbusiness.ILoggedInUserKeeper;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class StructuredbManagerTest 
       extends AbstractDependencyInjectionSpringContextTests {

	private String location;
	
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
		
		for( ApplicationContext context : 
			 ((StructuredbManager)manager).applicationContexts.values() ) {
			
			LoggedInUserKeeper keeper = (LoggedInUserKeeper) 
			                            context.getBean("loggedInUserKeeper");
			IStructuredbInstanceManager internalManager 
				= (IStructuredbInstanceManager) 
				  context.getBean("structuredbInstanceManager"); 
			keeper.setLoggedInUser( internalManager
					                .retrieveUserByUsername("admin") );
		}
		
		Folder f = manager.createFolder(database1, "testFolder1");
		assertNotNull(f);
		Folder g = manager.createFolder(database2, "testFolder2");
		assertNotNull(g);
	}
}

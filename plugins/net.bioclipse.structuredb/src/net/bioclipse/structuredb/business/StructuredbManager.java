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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.core.runtime.Platform;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sun.corba.se.impl.resolver.FileResolverImpl;
import com.sun.org.apache.bcel.internal.generic.LNEG;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.persistency.dao.IUserDao;
import net.bioclipse.structuredb.persistency.tables.TableCreator;

public class StructuredbManager implements IStructuredbManager {

	//Package protected for testing purposes
	Map<String, IStructuredbInstanceManager> instances 
		= new HashMap<String, IStructuredbInstanceManager>();

	//Package protected for testing purposes
	Map<String, ApplicationContext> applicationContexts 
		= new HashMap<String, ApplicationContext>();
	
	public void createLocalInstance(String databaseName)
		throws IllegalArgumentException {

		if( instances.containsKey(databaseName) ) {
			throw new IllegalArgumentException( "Database name already used: " 
					                            + databaseName );
		}
		HsqldbUtil.getInstance().addDatabase(databaseName);
		TableCreator.INSTANCE.createTables( "jdbc:hsqldb:hsql://127.0.0.1/"
				                             + databaseName );
		
		Map<String, IStructuredbInstanceManager> newInstances 
			= new HashMap<String, IStructuredbInstanceManager>();
	
		Map<String, ApplicationContext> newApplicationContexts
			= new HashMap<String, ApplicationContext>();
		
		for( String nameKey : instances.keySet() ) {
			//TODO: The day we not only handle local databases the row here 
			//      below will need to change
			newApplicationContexts.put( nameKey, 
					                    getApplicationcontext(nameKey, true) );
			newInstances.put( 
					nameKey, 
					(IStructuredbInstanceManager) 
						newApplicationContexts
						.get( nameKey )
					    .getBean("structuredbInstanceManager") );
		}
		
		instances           = newInstances;
		applicationContexts = newApplicationContexts;
		
		applicationContexts.put( databaseName,  
				                 getApplicationcontext(databaseName, true) );
		instances.put( 
				databaseName, 
				(IStructuredbInstanceManager) 
					applicationContexts.get( databaseName)
				                       .getBean("structuredbInstanceManager") );
		createAdmin( applicationContexts.get(databaseName) );
	}
	
	private void createAdmin(ApplicationContext context) {
		IUserDao userDao = (IUserDao) context.getBean("userDao");
		User admin = new User("admin", "", true );
		admin.setCreator(admin);
		Timestamp now = new Timestamp( System.currentTimeMillis() );
		admin.setCreated(now);
		admin.setEdited(now);
		userDao.insert(admin);
	}

	private ApplicationContext getApplicationcontext( String databaseName, 
			                                            boolean local ) {
		FileSystemXmlApplicationContext context 
			= new FileSystemXmlApplicationContext( 
				Structuredb.class
				           .getClassLoader()
                           .getResource("applicationContext.xml")
                           .toString() );

		BasicDataSource dataSource 
			= (BasicDataSource) context.getBean("dataSource");
		
		if(local) {
			dataSource.setUrl( "jdbc:hsqldb:hsql://127.0.0.1/" + databaseName );
		}
		else {
			throw new RuntimeException( "non-local databases not " +
					                    "supported in this version" );
		}
		return context;
	}

	public Folder createFolder(String databaseName, String folderName)
			throws IllegalArgumentException {
		
		Folder folder = new Folder(folderName);
		instances.get(databaseName).insertFolder(folder);
		return folder;
	}

	public Structure createStructure(String databaseName, String moleculeName,
			ICDKMolecule cdkMolecule) throws BioclipseException {
		// TODO Auto-generated method stub
		return null;
	}

	public User createUser(String databaseName, String username,
			String password, boolean sudoer) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeLocalInstance(String databaseName) {
		// TODO Auto-generated method stub

	}

	public List<Folder> retrieveAllFolders(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Structure> retrieveAllStructures(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> retrieveAllUser(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Folder retrieveFolderByName(String databaseName, String folderName) {

		return instances.get(databaseName).retrieveFolderByName(folderName);
	}

	public List<Structure> retrieveStructureByName(String databaseName,
			String structureName) {
		// TODO Auto-generated method stub
		return null;
	}

	public User retrieveUserByName(String databaseName, String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

}

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.persistency.dao.IUserDao;
import net.bioclipse.structuredb.persistency.tables.TableCreator;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.openscience.cdk.CDKConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author jonalv
 */
public class StructuredbManager implements IStructuredbManager {

	private Logger logger = Logger.getLogger(StructuredbManager.class);
	
	/* 
	 * This isn't super if cdk starts using AOP for fancy stuff in the future 
	 * but we don't want the recorded variant and this is a solution that is 
	 * reasonably easy to test. Should cdk start using fancy stuff real 
	 * integration testing running the OSGI layer is needed and this instance 
	 * would need to come from the OSGI service container
	 */
	private ICDKManager cdk = new CDKManager();
	
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
		TableCreator.INSTANCE.createTables( 
				HsqldbUtil.getInstance().getConnectionUrl(databaseName) );
		
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
		logger.info( "A new local instance of Structuredb named" 
				      + databaseName + " has been created" );
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
			dataSource.setUrl( 
					HsqldbUtil.getInstance().getConnectionUrl(databaseName) );
		}
		else {
			throw new UnsupportedOperationException( 
					"non-local databases not " +
					"supported in this version" );
		}
		return context;
	}

	public Folder createFolder(String databaseName, String folderName)
			throws IllegalArgumentException {
		
		Folder folder = new Folder(folderName);
		instances.get(databaseName).insertFolder(folder);
		logger.debug("Folder " + folderName + " inserted in " + databaseName);
		return folder;
	}

	public Structure createStructure( String databaseName, 
			                          String moleculeName,
			                          ICDKMolecule cdkMolecule) 
	                                  throws BioclipseException {
		
		Structure s = new Structure( moleculeName, cdkMolecule );
		instances.get(databaseName).insertStructure(s);
		logger.debug( "Structure " + moleculeName 
				      + " inserted in " + databaseName );
		return s;
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
		return instances.get(databaseName).retrieveAllStructures();
	}

	public List<User> retrieveAllUser(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Folder retrieveFolderByName(String databaseName, String folderName) {

		return instances.get(databaseName).retrieveFolderByName(folderName);
	}

	public List<Structure> retrieveStructuresByName( String databaseName,
			                                        String structureName ) {
		return instances.get(databaseName)
		                .retrieveStructureByName(structureName);
	}

	public User retrieveUserByName(String databaseName, String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addStructuresFromSDF(String databaseName, String filePath) throws BioclipseException {
		Iterator<ICDKMolecule> iterator;
		try {
			iterator 
				= cdk.creatMoleculeIterator( new FileInputStream(filePath) );
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException( 
					"Could not open file:" + filePath );
		}
		File file = new File(filePath);
		Folder f = 	createFolder( databaseName, 
				                  file.getName().replaceAll("\\..*?$", "") );
		
		while ( iterator.hasNext() ) {
			ICDKMolecule molecule = iterator.next();
			
			Object title = molecule.getAtomContainer()
			                       .getProperty(CDKConstants.TITLE);
			
			Structure s 
				= new Structure( title == null ? ""
						                       : title.toString(),
						         molecule);
			
			instances.get(databaseName).insertStructure(s);
			f.addStructure(s);
			instances.get(databaseName).update(f);
		}
	}
}

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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import net.bioclipse.database.DatabaseModelEvent;
import net.bioclipse.database.IDataSource;
import net.bioclipse.database.IDataSourceType;
import net.bioclipse.database.IDatabaseModelListener;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.dialogs.CreateStructureDatabaseDialog;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.UserManager;
import net.bioclipse.usermanager.UserManagerEvent;

/**
 * This class is responsible for the different structuredb datasources in the
 * system. Their actual info are stored in the UserManager 
 * 
 * @author jonalv
 *
 */
public class Structuredb implements IDataSourceType, IUserManagerListener {

	private final String name = "Structure Database";

	private List<StructuredbDataSource>  dataSources;
	private List<IDatabaseModelListener> modelListeners;
	
	private IAction createDatabaseAction;
	
	public Structuredb() {
		dataSources    = new ArrayList<StructuredbDataSource>();
		modelListeners = new ArrayList<IDatabaseModelListener>();
		createActions();
	}
	
	private void createActions() {
		createDatabaseAction = new Action("Create new Structure Database") {
			@Override
			public void run() {
				CreateStructureDatabaseDialog dialog = 
					new CreateStructureDatabaseDialog( PlatformUI.
							                           getWorkbench().
							                           getActiveWorkbenchWindow().
							                           getShell(), 
							                           SWT.NONE );
				dialog.open();
				//TODO: create the new database
			}
		};
	}

	public StructuredbDataSource[] getDataSources() {
		return dataSources.toArray(new StructuredbDataSource[0]);
	}

	public boolean hasDataSources() {
		return dataSources.size() > 0;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

	public void receiveKeyringEvent(UserManagerEvent event) {

		switch (event) {
		
		case LOGIN:
			for( String id : UserManager.getInstance().getAccountIdsByAccountTypeName("net.bioclipse.structuredb.AccountType") ) {
				UserManager um = UserManager.getInstance(); 
				dataSources.add( createDataSource( um.getProperty(id, "url"), 
						                           um.getUserName(id), 
						                           um.getPassword(id) ) );
			}
			break;
			
		case LOGOUT:
			dataSources.clear();
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * Creates datasource and the corresponding database.
	 * 
	 * @param url 
	 * @param username
	 * @param password
	 * @return
	 */
	public StructuredbDataSource createDataSource(String url, String username, String password) {
		ApplicationContext context = new FileSystemXmlApplicationContext( getPluginURL() + 
		                                                                  "src" +
		                                                                  File.separator + 
		                                                                  "applicationContext.xml" );

		BasicDataSource basicDataSource = (BasicDataSource) context.getBean("dataSource");
		basicDataSource.setUrl( url );
		basicDataSource.setUsername( username );
		basicDataSource.setPassword( password );
		
		StructuredbDataSource dataSource = new StructuredbDataSource(context);
		dataSources.add(dataSource);
		fireUpdate();
		return dataSource;
	}
	
	public static URL getPluginURL() {
		try {
	        return FileLocator.toFileURL(Platform.getBundle("net.bioclipse.structuredb").getEntry("/"));
        }
        catch (IOException e) {
	        throw new RuntimeException("could not locate config file for Spring", e);
        }
	}

	public void fillContextMenu(IMenuManager manager) {
		manager.add( createDatabaseAction );
	}

	public void addListener(IDatabaseModelListener listener) {
		modelListeners.add(listener);
	}

	public void removeListener(IDatabaseModelListener listener) {
		modelListeners.remove(listener);
	}
	
	public void fireUpdate() {
		for( IDatabaseModelListener l : modelListeners) {
			l.modelUpdated( new DatabaseModelEvent(this) );
		}
	}
}

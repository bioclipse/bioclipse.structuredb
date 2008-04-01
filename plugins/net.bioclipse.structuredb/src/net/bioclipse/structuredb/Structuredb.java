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

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.services.views.model.AbstractServiceContainer;
import net.bioclipse.services.views.model.IDatabaseType;
import net.bioclipse.structuredb.dialogs.CreateStructureDatabaseDialog;
import net.bioclipse.usermanager.Activator;
import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.UserManagerEvent;
import net.bioclipse.usermanager.business.IUserManager;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

/**
 * This class is responsible for the different structuredb datasources in the
 * system. Their actual info are stored in the UserManager 
 * 
 * @author jonalv
 *
 */
public class Structuredb extends AbstractServiceContainer 
                         implements IUserManagerListener, IDatabaseType {

	private final Logger logger = Logger.getLogger( this.getClass() );
	
	private final String name = "Structure Database";

	private List<StructuredbDataSource>  dataSources;
	
	private IAction createDatabaseAction;
	
	public Structuredb() {
		dataSources    = new ArrayList<StructuredbDataSource>();
		createActions();
	}
	
	private void createActions() {
		createDatabaseAction = new Action("Create new Structure Database") {
			@Override
			public void run() {
				CreateStructureDatabaseDialog dialog = 
					new CreateStructureDatabaseDialog( PlatformUI
							                           .getWorkbench()
							                           .getActiveWorkbenchWindow()
							                           .getShell(), 
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

	public void fillContextMenu(IMenuManager manager) {
		manager.add( createDatabaseAction );
	}

	public void fireUpdate() {
	}

	@Override
	public void receiveUserManagerEvent(UserManagerEvent event) {
		
		switch (event) {

		case LOGIN:
			IUserManager um = Activator.getDefault().getUserManager();
			for ( String id : um.getAccountIdsByAccountTypeName(
			                  "net.bioclipse.structuredb.AccountType" ) ) {
				
			}
			break;
			
		case LOGOUT:
			dataSources.clear();
			break;
			
		default:
			break;
		}		
	}

	@Override
	public void createChildren() {
//		BasicDataSource basicDataSource = (BasicDataSource) context.getBean("dataSource");
//		basicDataSource.setUrl( url );
//		basicDataSource.setUsername( username );
//		basicDataSource.setPassword( password );
		
//		StructuredbDataSource dataSource = new StructuredbDataSource(context);
//		dataSources.add(dataSource);
		fireUpdate();		
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}

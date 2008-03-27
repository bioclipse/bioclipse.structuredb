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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.springframework.context.ApplicationContext;

import net.bioclipse.database.IDataSource;
import net.bioclipse.database.IDataSourceType;
import net.bioclipse.database.IDatabaseModelListener;
import net.bioclipse.database.ISearch;

/**
 * @author jonalv
 *
 */
public class StructuredbDataSource implements IDataSource {

	private ApplicationContext context;
	private List<IDatabaseModelListener> modelListeners;

	public StructuredbDataSource(ApplicationContext context) {
		this.context = context;
		modelListeners = new ArrayList<IDatabaseModelListener>();
		createActions();
	}
	
	private void createActions() {
	}

	public Object[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSourceType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	public ISearch search(Object query) {
		// TODO Auto-generated method stub
		return null;
	}

	public void fillContextMenu(IMenuManager manager) {
	}

	public void addListener(IDatabaseModelListener listener) {
		modelListeners.add(listener);
	}

	public void removeListener(IDatabaseModelListener listener) {
		modelListeners.remove(listener);
	}
}

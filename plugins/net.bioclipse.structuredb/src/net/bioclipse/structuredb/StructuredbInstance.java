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

import net.bioclipse.services.views.model.IServiceContainer;
import net.bioclipse.services.views.model.IServiceObject;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.springframework.context.ApplicationContext;

/**
 * @author jonalv
 *
 */
public class StructuredbInstance implements IStructuredbInstance {

	private ApplicationContext context;
	private String name;
	private Structuredb structuredb;
	
	public StructuredbInstance( Structuredb structuredb) {
		this.structuredb = structuredb;
	}
	
	public List<IServiceObject> getChildren() {
		return new ArrayList<IServiceObject>();
	}

	public String getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		return "net.bioclipse.structuredb - " + name;
	}

	public String getName() {
		return name;
	}

	public IServiceContainer getParent() {
		return structuredb;
	}

	public String getParentID() {
		return "net.bioclipse.structuredb";
	}

	public void setIcon(String icon) {
		// TODO Auto-generated method stub
		
	}

	public void setId(String id) {
		// TODO Auto-generated method stub
		
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	public void setParent(IServiceContainer parent) {
		// TODO Auto-generated method stub
		
	}

	public void setParentID(String parentID) {
		// TODO Auto-generated method stub
		
	}

	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUID() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}

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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.services.views.model.AbstractServiceContainer;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IDatabaseType;
import net.bioclipse.services.views.model.IServiceObject;
import net.bioclipse.structuredb.dialogs.CreateStructureDatabaseDialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author jonalv
 *
 */
public class Structuredb extends AbstractServiceContainer
                         implements IDatabaseType {

    private final Logger logger = Logger.getLogger( this.getClass() );

    private final String name = "Structure Database";

    public Structuredb() {
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Override
    public void createChildren() {
        
        List<IDatabase> children 
            = new ArrayList<IDatabase>();
        
        for ( String s : Activator.getDefault()
                                  .getStructuredbManager()
                                  .listDatabaseNames() ) {
            children.add( new DataBase(s) );
        }
        setChildren( children );
    }

    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }
}

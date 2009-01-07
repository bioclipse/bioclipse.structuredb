/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *******************************************************************************/
package net.bioclipse.structuredb.actions;

import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.dialogs.CreateNewStructureDBDialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * @author jonalv
 *
 */
public class CreateStructuredbAction extends ActionDelegate {

    @Override
    public void run(IAction action) {
        CreateNewStructureDBDialog dialog = 
            new CreateNewStructureDBDialog(PlatformUI.getWorkbench()
                                                     .getActiveWorkbenchWindow()
                                                     .getShell());
        dialog.open();
        String name = dialog.getName();
        if ( name != null ) {
        	IStructuredbManager manager = Activator
        	                              .getDefault()
        	                              .getStructuredbManager();
        	manager.createDatabase( name );
        }
    }
}

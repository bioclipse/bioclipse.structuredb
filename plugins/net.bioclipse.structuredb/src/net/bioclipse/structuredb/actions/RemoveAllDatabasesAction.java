/*******************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.actions;

import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.dialogs.CreateNewStructureDBDialog;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;


/**
 * @author jonalv
 *
 */
public class RemoveAllDatabasesAction extends ActionDelegate {

    @Override
    public void run(IAction action) {
        
        MessageDialog dialog 
            = new MessageDialog( 
                      null, 
                      "Confirm removal of all databases", 
                      null, 
                      "Really remove all databases?",
                      MessageDialog.QUESTION,
                      new String[] {"Yes", "Cancel"},
                      0 ); // yes is the default
 
        int result = dialog.open();

        if ( result == 0 ) {
            Activator.getDefault().getStructuredbManager()
                                  .deleteAllDatabases();
        }
    }
}
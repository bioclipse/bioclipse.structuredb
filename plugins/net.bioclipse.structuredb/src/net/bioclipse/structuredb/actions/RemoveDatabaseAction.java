/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.actions;

import java.util.Iterator;

import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.business.IStructuredbManager;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionDelegate;


/**
 * @author jonalv
 *
 */
public class RemoveDatabaseAction extends ActionDelegate {

    private ISelection selection;

    @SuppressWarnings("unchecked")
    @Override
    public void run(IAction action) {
        
        IStructuredbManager manager = Activator
                                      .getDefault()
                                      .getStructuredbManager();
        
        if ( selection instanceof IStructuredSelection ) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            System.out.println("Selected: " + ss.getFirstElement() );
            Iterator i = ss.iterator();
            while ( i.hasNext() ) {
                Object o = i.next();
                if (o instanceof Database) {
                    manager.deleteDatabase( ((Database)o).getName() );
                }
            }
        }
    }
    
    @Override
    public void selectionChanged( IAction action, 
                                  ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}

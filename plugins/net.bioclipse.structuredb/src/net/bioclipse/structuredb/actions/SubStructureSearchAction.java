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

import net.bioclipse.chemoinformatics.PickMoleculeDialog;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.StructureDBInstance;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;


public class SubStructureSearchAction extends ActionDelegate {
    Logger logger = Logger.getLogger( this.getClass() );
    private ISelection selection;

    @Override
    public void run( IAction action ) {   

        if ( selection instanceof IStructuredSelection ) {

            IWorkbenchPage wPage = PlatformUI.getWorkbench()
                                             .getActiveWorkbenchWindow()
                                             .getActivePage();
            
            for ( Object element : 
                  ( (IStructuredSelection) selection ).toArray() ) {
                
                if (element instanceof StructureDBInstance) {
                    PickMoleculeDialog dialog 
                        = new PickMoleculeDialog(
                                  PlatformUI.getWorkbench()
                                            .getActiveWorkbenchWindow()
                                            .getShell() );
                    dialog.open();
                    LogUtils.handleException( 
                        new BioclipseException("This feature is not yet " +
                        		        "completely implemented"), logger );
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

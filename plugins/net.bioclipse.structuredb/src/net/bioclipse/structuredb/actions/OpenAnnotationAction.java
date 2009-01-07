/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Arvid Berg
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.actions;

import net.bioclipse.structuredb.Label;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

public class OpenAnnotationAction extends ActionDelegate {
    Logger logger = Logger.getLogger(OpenAnnotationAction.class );
    private ISelection selection;

    @Override
    public void run( IAction action ) {   

        if ( selection instanceof IStructuredSelection ) {

            IWorkbenchPage wPage = PlatformUI.getWorkbench()
                                             .getActiveWorkbenchWindow()
                                             .getActivePage();
            
            for ( Object element : 
                  ( (IStructuredSelection) selection ).toArray() ) {
                
                if (element instanceof Label) {
                    try {
                        wPage.openEditor( 
                            (Label) element,
                            "net.bioclipse.cdk.ui.sdfeditor");
                    } 
                    catch ( PartInitException e ) {
                        logger.debug(
                            "Faild to open editor for Annotation" );                
                    }
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

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

import java.io.IOException;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.dialogs.SMARTSQueryPromptDialog;
import net.bioclipse.ui.business.IUIManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;


/**
 * @author jonalv
 *
 */
public class SMARTSSearchAction extends ActionDelegate {
    Logger logger = Logger.getLogger( this.getClass() );
    private ISelection selection;

    @Override
    public void run( IAction action ) {   

        if ( selection instanceof IStructuredSelection ) {

            for ( Object element : 
                  ( (IStructuredSelection) selection ).toArray() ) {
                
                if (element instanceof StructureDBInstance) {
                    SMARTSQueryPromptDialog dialog 
                        = new SMARTSQueryPromptDialog(
                                  PlatformUI.getWorkbench()
                                            .getActiveWorkbenchWindow()
                                            .getShell() );
                    if ( dialog.open() == dialog.OK ) {
                        performSMARTSQuery(
                            ( (StructureDBInstance)element ).getName(),
                            dialog.getSMARTS() );
                    }
                }
            }
        }
    }
    
    /**
     * @param name
     * @param selectedFiles
     */
    private void performSMARTSQuery( String dbName,
                                     final String SMARTS ) {

        final IUIManager ui 
            = net.bioclipse.ui.business.Activator.getDefault().getUIManager();
        final IStructuredbManager structuredb 
            = net.bioclipse.structuredb.Activator.getDefault()
                                                 .getStructuredbManager();
        try {
            
            BioclipseUIJob<List<?>> uiJob = new BioclipseUIJob<List<?>>() {
                @Override
                public void runInUI() {
                    if ( getReturnValue().isEmpty() ) {
                        MessageDialog.openInformation( 
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                                     .getShell(), 
                            "No hits", 
                            "SMARTS Query: \"" + SMARTS 
                                + "\" did not result in any hits." );
                    } else {
                        try {
                            ui.open( (IBioObject)getReturnValue() );
                        }
                        catch ( Exception e ) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
            
            structuredb.smartsQuery( dbName, SMARTS, uiJob);
        }
        catch ( Exception e ) {
            LogUtils.handleException( e, 
                                      logger, 
                                      "net.bioclipse.strucutredb" );
        }
    }

    @Override
    public void selectionChanged( IAction action, 
                                  ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}

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

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.chemoinformatics.dialogs.PickMoleculeDialog;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.ui.business.IUIManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
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
                    if ( dialog.open() == dialog.OK ) {
                        performSubstructureSearch(
                            ( (StructureDBInstance)element ).getName(),
                            dialog.getSelectedFile() );
                    }
                }
            }
        }
    }
    
    /**
     * @param name
     * @param selectedFiles
     */
    private void performSubstructureSearch( String dbName,
                                            IFile selectedFile ) {

        final IUIManager ui 
            = net.bioclipse.ui.business.Activator.getDefault().getUIManager();
        IStructuredbManager structuredb 
            = net.bioclipse.structuredb.Activator.getDefault()
                                                 .getStructuredbManager();
        ICDKManager cdk 
            = net.bioclipse.cdk.business.Activator.getDefault()
                                                  .getJavaCDKManager();
        try {
            IMolecule molecule = cdk.loadMolecule( selectedFile );
            BioclipseUIJob<List<?>> uijob = new BioclipseUIJob<List<?>>() {

                @Override
                public void runInUI() {
                    try {
                        ui.open( (IBioObject)getReturnValue() );
                    }
                    catch ( Exception e ) {
                        LogUtils.handleException( e, logger );
                    }
                }
            };
            structuredb.subStructureSearch( dbName, 
                                            molecule,
                                            uijob );
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

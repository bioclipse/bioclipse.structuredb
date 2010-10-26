/* *****************************************************************************
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

import java.util.List;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.chemoinformatics.dialogs.PickMoleculeDialog;
import net.bioclipse.chemoinformatics.dialogs.PickMoleculesDialog;
import net.bioclipse.core.api.domain.IBioObject;
import net.bioclipse.core.api.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.core.util.TimeCalculator;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.business.IStructuredbManager.ImportStatistics;
import net.bioclipse.structuredb.dialogs.ImportCompleteDialog;
import net.bioclipse.ui.business.IUIManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;


/**
 * @author jonalv
 *
 */
public class ImportMoleculesAction extends ActionDelegate {
    Logger logger = Logger.getLogger( this.getClass() );
    private ISelection selection;

    @Override
    public void run( IAction action ) {   

        if ( selection instanceof IStructuredSelection ) {

            for ( Object element : 
                  ( (IStructuredSelection) selection ).toArray() ) {
                
                if (element instanceof StructureDBInstance) {
                    PickMoleculesDialog dialog 
                        = new PickMoleculesDialog(
                                  PlatformUI.getWorkbench()
                                            .getActiveWorkbenchWindow()
                                            .getShell() );
                    if ( dialog.open() == dialog.OK ) {
                        performImport(
                            ( (StructureDBInstance)element ).getName(),
                            dialog.getSelectedFiles() );
                    }
                }
            }
        }
    }
    
    public static class ImportMoleculesUIJob 
                  extends BioclipseUIJob<ImportStatistics> {

        @Override
        public void runInUI() {
            ImportStatistics s = getReturnValue();
            if (s.failures.size() > 0) {
                ImportCompleteDialog dialog 
                    = new ImportCompleteDialog( 
                              PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getShell(),
                              getReturnValue() );
            
                dialog.open();
            }
            else {
                MessageDialog.openInformation( 
                    PlatformUI.getWorkbench()
                              .getActiveWorkbenchWindow()
                              .getShell(),
                    "Import complete",
                    "Imported " + s.importedMolecules + " molecules " +
                        " in " + TimeCalculator.millisecsToString( 
                                                    s.importTime )  
                    ); 
            }
        }
    }
    
    /**
     * @param name
     * @param selectedFiles
     */
    private void performImport( String dbName,
                                List<IFile> selectedFiles ) {

        IJavaStructuredbManager structuredb 
            = net.bioclipse.structuredb.Activator.getDefault()
                                                 .getStructuredbManager();
        structuredb.addMoleculesFromFiles( 
            dbName, 
            selectedFiles, 
            new ImportMoleculesUIJob() );
    }

    @Override
    public void selectionChanged( IAction action, 
                                  ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}
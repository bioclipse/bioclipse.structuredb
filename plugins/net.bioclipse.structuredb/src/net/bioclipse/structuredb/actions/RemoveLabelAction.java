/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.actions;

import java.util.Iterator;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.business.IStructuredbManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;


/**
 * @author jonalv
 *
 */
public class RemoveLabelAction extends ActionDelegate {

    private ISelection selection;

    @SuppressWarnings("unchecked")
    @Override
    public void run(IAction action) {
        
        IStructuredbManager manager = Activator
                                      .getDefault()
                                      .getStructuredbManager();
        
        if ( selection instanceof IStructuredSelection ) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Iterator i = ss.iterator();
            MessageBox messageBox 
                = new MessageBox( PlatformUI.getWorkbench()
                                            .getActiveWorkbenchWindow()
                                            .getShell(), 
                                  SWT.ICON_QUESTION | 
                                  SWT.YES           | 
                                  SWT.NO            |
                                  SWT.CANCEL );
            messageBox.setMessage( "Also remove structures " +
            		                   "with this labels?" );
            messageBox.setText("Deleting label");
            int response = messageBox.open();
            if (response == SWT.CANCEL)
              return;
            while ( i.hasNext() ) {
                Object o = i.next();
                if (o instanceof Label) {
                    Label l = (Label)o;
                    if ( l.getParent() instanceof Database ) {
                        net.bioclipse.structuredb.domain.Annotation annotation =
                            manager.retrieveLabelByName(
                                l.getParent().getName(), l.getName() );
                        if (response == SWT.YES) {
                            deleteWithStructures(l.getParent().getName(), annotation);
                        }
                        else {
                            manager.deleteLabel( l.getParent().getName(), 
                                            annotation );
                        }
                    }
                }
            }
        }
    }
    
    private void deleteWithStructures(
        final String name, 
        final net.bioclipse.structuredb.domain.Annotation annotation ) {

        Job job = new Job("Delete Annotation and Structures") {
            @Override
            protected IStatus run( IProgressMonitor monitor ) {
                Activator.getDefault()
                         .getStructuredbManager()
                         .deleteWithStructures( name, 
                                                annotation, 
                                                monitor );
                return Status.OK_STATUS;
            }
        };
        job.setUser( true );
        job.schedule();
    }
        
    @Override
    public void selectionChanged( IAction action, ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}

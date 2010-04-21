/* *****************************************************************************
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
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.dialogs.ConfirmDeleteLabelDialog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
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
public class RemoveAnnotationAction extends ActionDelegate {

    private ISelection selection;

    @SuppressWarnings("unchecked")
    @Override
    public void run(IAction action) {
        
        IJavaStructuredbManager manager = Activator
                                      .getDefault()
                                      .getStructuredbManager();
        
        if ( selection instanceof IStructuredSelection ) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            
            if ( ss.size() == 1 ) {
                Object o = ss.getFirstElement();
                if (o instanceof Label) {
                    Label m = (Label)o;
                    
                    net.bioclipse.structuredb.domain.Annotation annotation 
                        = m.getAnnotation();
                        
                    MessageBox messageBox 
                        = new MessageBox( 
                                  PlatformUI.getWorkbench()
                                            .getActiveWorkbenchWindow()
                                            .getShell(), 
                                  SWT.ICON_QUESTION | 
                                  SWT.YES           | 
                                  SWT.NO            |
                                  SWT.CANCEL );
                    messageBox.setMessage( "Should all structures with "
                                           + "label \"" + annotation.getValue() 
                                           + "\" also be removed?" );
                    messageBox.setText( "Deleting label \""
                                        + annotation.getValue() + "\"" );
                    int response = messageBox.open();
                    if (response == SWT.CANCEL)
                      return;
                    
                    if (response == SWT.YES) {
                        deleteWithStructures(m.getParent().getName(), 
                                             annotation);
                    }
                    else {
                        manager.deleteAnnotation( 
                            m.getParent().getName(), 
                            annotation );
                    }
                }
            }
            else {
                Iterator i = ss.iterator();
                boolean keepAsking = true;
                int response = -1;
                while ( i.hasNext() ) {
                    Object o = i.next();
                    if (o instanceof Label) {
                        Label m = (Label)o;
                        
                        net.bioclipse.structuredb.domain.Annotation annotation 
                            = m.getAnnotation();
                        
                        if (keepAsking) {
                            ConfirmDeleteLabelDialog dialog 
                                = new ConfirmDeleteLabelDialog( 
                                          PlatformUI.getWorkbench()
                                                    .getActiveWorkbenchWindow()
                                                    .getShell(), 
                                          annotation.getValue() + "" );
                            response = dialog.open();
                            keepAsking = !dialog.getApplyToAll();
                        }
                        
                        switch ( response ) {
                            case ConfirmDeleteLabelDialog.CANCEL:
                                return;
                            case ConfirmDeleteLabelDialog.REMOVE:
                                deleteWithStructures(m.getParent().getName(), 
                                                     annotation);
                                break;
                            case ConfirmDeleteLabelDialog.KEEP:
                                manager.deleteAnnotation( 
                                    m.getParent().getName(), 
                                    annotation );
                                break;
                            default:
                                throw new IllegalStateException(
                                    "Don't know how to handle return value: "
                                    + response + " returned by " 
                                    + "net.bioclipse.structuredb.dialogs." 
                                    + "ConfirmDeleteLabelDialog.open()");
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
                         .deleteWithMolecules( name, 
                                                annotation, 
                                                monitor );
                return Status.OK_STATUS;
            }
        };
        job.setUser( true );
        job.schedule();
    }
        
    @Override
    public void selectionChanged( IAction action, 
                                  ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}

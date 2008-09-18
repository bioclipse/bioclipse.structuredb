/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.actions;

import java.util.Iterator;

import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.AnnotationUIModel;
import net.bioclipse.structuredb.business.IStructuredbManager;

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
        
        IStructuredbManager manager = Activator
                                      .getDefault()
                                      .getStructuredbManager();
        
        if ( selection instanceof IStructuredSelection ) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Iterator i = ss.iterator();
            while ( i.hasNext() ) {
                Object o = i.next();
                if (o instanceof AnnotationUIModel) {
                    AnnotationUIModel l = (AnnotationUIModel)o;
                    if ( l.getParent() instanceof Database ) {
                        net.bioclipse.structuredb.domain.Annotation 
                            annotation = manager
                                         .annotationByName(
                                l.getParent().getName(), l.getName() );
                        
                        MessageBox messageBox 
                            = new MessageBox( 
                                      PlatformUI.getWorkbench()
                                                .getActiveWorkbenchWindow()
                                                .getShell(), 
                                      SWT.ICON_QUESTION | 
                                      SWT.YES           | 
                                      SWT.NO            |
                                      SWT.CANCEL );
                        messageBox.setMessage( "Should all structures " +
                        		               "annotated with with " +
                        		               annotation.getName() + 
                        		               "also be removed?" );
                        messageBox.setText( "Deleting annotation "
                                            + annotation.getName() );
                        int response = messageBox.open();
                        if (response == SWT.CANCEL)
                          return;
                        
                        if (response == SWT.YES) {
                            deleteWithStructures(l.getParent().getName(), 
                                                 annotation);
                        }
                        else {
                            manager.deleteAnnotation( 
                                l.getParent().getName(), 
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
    public void selectionChanged( IAction action, 
                                  ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}

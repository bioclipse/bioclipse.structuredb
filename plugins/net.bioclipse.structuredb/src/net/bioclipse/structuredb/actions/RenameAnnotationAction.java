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

import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.dialogs.RenameLabelDialog;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.TextAnnotation;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;


/**
 * @author jonalv
 *
 */
public class RenameAnnotationAction extends ActionDelegate {
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
                        Label label = (Label)element;
                        RenameLabelDialog d 
                            = new RenameLabelDialog( 
                                PlatformUI.getWorkbench()
                                          .getActiveWorkbenchWindow()
                                          .getShell(), 
                                          label.getName() );
                        if ( d.open() == d.OK ) {
                            
                            IJavaStructuredbManager structuredb 
                                = Activator.getDefault()
                                           .getStructuredbManager();
                            Annotation annotation = label.getAnnotation();
                            TextAnnotation loaded 
                                = (TextAnnotation)
                                  structuredb.getAnnotationById(
                                      label.getParent().getName(),
                                      annotation.getId() );
                            loaded.setValue( d.getName() );
                            structuredb.save( label.getParent().getName(), 
                                              loaded );
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

/* *****************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 * Copyright (c) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.actions;

import net.bioclipse.structuredb.Label;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public class OpenLabelAction extends BaseSelectionListenerAction {
    /**
     * @param text
     */
    protected OpenLabelAction(String text) {
        super( text );
    }

    Logger logger = Logger.getLogger(OpenLabelAction.class );
    
    @Override
    public void run() {
        logger.debug( "Running OpenAnnotationAction" );
        
        IStructuredSelection selection = getStructuredSelection();

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

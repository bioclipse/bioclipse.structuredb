/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
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

import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.business.IStructuredbManager;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
            while ( i.hasNext() ) {
                Object o = i.next();
                if (o instanceof Label) {
                    Label l = (Label)o;
                    if ( l.getParent() instanceof Database ) {
                        net.bioclipse.structuredb.domain.Label label =
                            manager.retrieveLabelByName(
                                l.getParent().getName(), l.getName() );
                        manager.delete( l.getParent().getName(), label );
                    }
                }
            }
        }
    }
    
    @Override
    public void selectionChanged( IAction action, ISelection selection ) {
        this.selection = selection;
        super.selectionChanged( action, selection );
    }
}

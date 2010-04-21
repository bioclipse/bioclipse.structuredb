/* *****************************************************************************
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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;


/**
 * @author Arvid
 *
 */
public class OpenActionProvider extends CommonActionProvider {

    OpenLabelAction doubleClickAction;
    /* (non-Javadoc)
     * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
     */
    @Override
    public void init( ICommonActionExtensionSite aSite ) {
        super.init( aSite );
        
        doubleClickAction = new OpenLabelAction("Open");
        aSite.getStructuredViewer().addSelectionChangedListener(doubleClickAction);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
    public void fillActionBars( IActionBars actionBars ) {

        super.fillActionBars( actionBars );
        IStructuredSelection selection = (IStructuredSelection) getContext()
                        .getSelection();
        if ( selection.size() == 1 && selection.getFirstElement() instanceof Label ) {
            doubleClickAction.selectionChanged( selection );
            actionBars.setGlobalActionHandler( ICommonActionConstants.OPEN,
                                               doubleClickAction );

        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu( IMenuManager aMenu ) {
        super.fillContextMenu( aMenu );
        IStructuredSelection selection = (IStructuredSelection) getContext()
        .getSelection();

        doubleClickAction.selectionChanged(selection);
        if (doubleClickAction.isEnabled()) {
            aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, doubleClickAction);
        }
    }
}

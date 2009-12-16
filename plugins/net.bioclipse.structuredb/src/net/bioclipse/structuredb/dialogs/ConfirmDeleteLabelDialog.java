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
package net.bioclipse.structuredb.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * @author jonalv
 *
 */
public class ConfirmDeleteLabelDialog extends TitleAreaDialog {

    public static final int KEEP   = IDialogConstants.CLIENT_ID + 0;
    public static final int REMOVE = IDialogConstants.CLIENT_ID + 1;
    public static final int CANCEL = IDialogConstants.CANCEL_ID;
    private String labelName = "$labelName$";
    
    private boolean applyToAll = false;
    private Button applyToAllButton;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ConfirmDeleteLabelDialog(Shell parentShell, String labelName) {

        super( parentShell );
        this.labelName = labelName;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite area = (Composite) super.createDialogArea( parent );
        Composite container = new Composite( area, SWT.NONE );
        container.setLayout(new FormLayout());
        container.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        applyToAllButton = new Button(container, SWT.CHECK);
        final FormData fd_applyToAllButton = new FormData();
        fd_applyToAllButton.top = new FormAttachment(0, 10);
        fd_applyToAllButton.left = new FormAttachment(0, 10);
        applyToAllButton.setLayoutData(fd_applyToAllButton);
        applyToAllButton.setText("Apply to all selected labels");
        setMessage("What should be done to the molecules with the label " 
                   + labelName + "?");
        setTitle("What about the labeled molecules?");
        //
        return area;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        createButton( parent, CANCEL, "Cancel", false );
        createButton( parent, KEEP,   "Keep",   true );
        createButton( parent, REMOVE, "Remove", false );
        
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {

        return new Point( 319, 191 );
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Confirm Delete ");
    }

    @Override
    protected void buttonPressed( int buttonId ) {
        applyToAll = applyToAllButton.getSelection();
        if ( buttonId == CANCEL ) {
            super.buttonPressed( buttonId );
        }
        else {
            setReturnCode(buttonId);
            close(); 
        }
    }
    
    public boolean getApplyToAll() {
        return applyToAll;
    }
}

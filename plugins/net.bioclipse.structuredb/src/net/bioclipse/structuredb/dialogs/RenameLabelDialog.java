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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


/**
 * @author jonalv
 *
 */
public class RenameLabelDialog extends Dialog {
    private Text text;
    private Label lblAnnotationName;
    private String name;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public RenameLabelDialog(Shell parentShell, String oldName) {
        super( parentShell );
        name = oldName;
    }

    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea( Composite parent ) {

        Composite container = (Composite) super.createDialogArea( parent );
        container.setLayout(new FormLayout());
        {
            lblAnnotationName = new Label(container, SWT.NONE);
            {
                FormData formData = new FormData();
                formData.top = new FormAttachment(0, 10);
                formData.left = new FormAttachment(0, 10);
                lblAnnotationName.setLayoutData(formData);
            }
            lblAnnotationName.setText("Label name:");
        }
        {
            text = new Text(container, SWT.BORDER);
            {
                FormData formData = new FormData();
                formData.top = new FormAttachment(lblAnnotationName, 0, SWT.TOP);
                formData.left = new FormAttachment(lblAnnotationName, 6);
                formData.right = new FormAttachment(100, -10);
                text.setLayoutData(formData);
            }
            text.setText( name );
        }

        return container;
    }

    /**
     * Create contents of the button bar.
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        createButton( parent, IDialogConstants.OK_ID,
                      IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID,
                      IDialogConstants.CANCEL_LABEL, false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        name = text.getText();
        super.okPressed();
    }
    
    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {

        return new Point( 450, 127 );
    }
    
    public String getName() {
        return name;
    }
}

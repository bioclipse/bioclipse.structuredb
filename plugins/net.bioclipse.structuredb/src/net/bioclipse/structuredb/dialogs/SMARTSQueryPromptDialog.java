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
package net.bioclipse.structuredb.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;


/**
 * @author jonalv
 *
 */
public class SMARTSQueryPromptDialog extends TitleAreaDialog {
    private Text text;
    private FormData formData_1;
    private Label lblSmarts;
    private String SMARTSString;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public SMARTSQueryPromptDialog(Shell parentShell) {

        super( parentShell );
    }

    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        setMessage("Enter a SMARTS query to be used in the search");
        setTitle("Give SMARTS Query");

        Composite area = (Composite) super.createDialogArea( parent );
        Composite container = new Composite( area, SWT.NONE );
        container.setLayout(new FormLayout());
        container.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        {
            lblSmarts = new Label(container, SWT.NONE);
            {
                formData_1 = new FormData();
                formData_1.top = new FormAttachment(0, 64);
                formData_1.left = new FormAttachment(0, 10);
                lblSmarts.setLayoutData(formData_1);
            }
            lblSmarts.setText("SMARTS:");
        }
        {
            text = new Text(container, SWT.BORDER);
            {
                FormData formData = new FormData();
                formData.right = new FormAttachment(100, -10);
                formData.top = new FormAttachment(lblSmarts, -9, SWT.TOP);
                formData.left = new FormAttachment(lblSmarts, 6);
                text.setLayoutData(formData);
            }
        }

        return area;
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

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point( 450, 300 );
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    public String getSMARTS() {
        return SMARTSString;
    }
    
    @Override
    protected void okPressed() {
        SMARTSString = text.getText();
        super.okPressed();
    }
}

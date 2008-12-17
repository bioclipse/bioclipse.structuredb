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
package net.bioclipse.structuredb.dialogs;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.grouplayout.GroupLayout;
import org.eclipse.swt.layout.grouplayout.LayoutStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
public class CreateNewStructureDBDialog extends TitleAreaDialog {
        private Text text;
        private String name;
        /**
         * Create the dialog
         * @param parentShell
         */
        public CreateNewStructureDBDialog(Shell parentShell) {
                super(parentShell);
        }
        /**
         * Create contents of the dialog
         * @param parent
         */
        @Override
        protected Control createDialogArea(Composite parent) {
                Composite area = (Composite) super.createDialogArea(parent);
                Composite container = new Composite(area, SWT.NONE);
                container.setLayoutData(new GridData(GridData.FILL_BOTH));
                Label databaseNameLabel;
                databaseNameLabel = new Label(container, SWT.NONE);
                databaseNameLabel.setText("Database name:");
                text = new Text(container, SWT.BORDER);
                final GroupLayout groupLayout = new GroupLayout(container);
                groupLayout.setHorizontalGroup(
                        groupLayout.createParallelGroup(GroupLayout.LEADING)
                                .add(groupLayout.createSequentialGroup()
                                        .add(5, 5, 5)
                                        .add(databaseNameLabel)
                                        .addPreferredGap(LayoutStyle.RELATED)
                                        .add(text, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
                );
                groupLayout.setVerticalGroup(
                        groupLayout.createParallelGroup(GroupLayout.LEADING)
                                .add(groupLayout.createSequentialGroup()
                                        .add(12, 12, 12)
                                        .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
                                                .add(databaseNameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .add(text, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .add(9, 9, 9))
                );
                container.setLayout(groupLayout);
                setTitle("Create new StructureDB instance");
                setMessage("Enter a name for the new database");
                //
                return area;
        }
        /**
         * Create contents of the button bar
         * @param parent
         */
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
                createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                                true);
                createButton(parent, IDialogConstants.CANCEL_ID,
                                IDialogConstants.CANCEL_LABEL, false);
        }
        /**
         * Return the initial size of the dialog
         */
        @Override
        protected Point getInitialSize() {
                return new Point(500, 213);
        }
        protected void configureShell(Shell newShell) {
                super.configureShell(newShell);
                newShell.setText("Create new StructureDB");
        }
        protected void buttonPressed(int buttonId) {
                if (buttonId == IDialogConstants.OK_ID) {
                        name = text.getText();
                }
                super.buttonPressed(buttonId);
        }
        public String getName() {
                return name;
        }
}

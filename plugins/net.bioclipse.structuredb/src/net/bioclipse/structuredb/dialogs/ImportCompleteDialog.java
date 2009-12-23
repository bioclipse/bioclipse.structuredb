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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.core.util.TimeCalculator;
import net.bioclipse.structuredb.business.IStructuredbManager.ImportStatistics;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * @author jonalv
 *
 */
public class ImportCompleteDialog extends TitleAreaDialog {

    private Text text;
    private List list;
    private String fileName = "$fileName$";
    private String numberOfImportedMolecules = "$numberOfImportedMolecules$";
    private long importTime = 41515151;
    private Map<Integer, Exception> failures 
        = new HashMap<Integer, Exception>() { {
            put(10, new Exception("some strange error message"));
            put(54, new Exception("peculiar error"));
            put(70, new Exception("Another peculiar error"));
        }
    };
    
    private java.util.List<Integer> keysInList = new ArrayList<Integer>();
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ImportCompleteDialog(Shell parentShell, ImportStatistics s) {
        super( parentShell );
        this.failures = s.failures;
        this.numberOfImportedMolecules = s.importedMolecules + "";
        this.importTime = s.importTime;
        this.fileName = "?";
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

        final Label importerdLabel = new Label(container, SWT.NONE);
        final FormData fd_importerdLabel = new FormData();
        fd_importerdLabel.top = new FormAttachment(0, 5);
        fd_importerdLabel.left = new FormAttachment(0, 5);
        importerdLabel.setLayoutData(fd_importerdLabel);
        importerdLabel.setText( "Imported " + numberOfImportedMolecules 
                                + "molecules in " 
                                + TimeCalculator.millisecsToString(importTime ) 
                                + "." );

        final Label label = new Label(container, SWT.NONE);
        final FormData fd_label = new FormData();
        fd_label.top = new FormAttachment(importerdLabel, 5, SWT.BOTTOM);
        fd_label.left = new FormAttachment(importerdLabel, 0, SWT.LEFT);
        label.setLayoutData(fd_label);
        label.setText( failures.size() + " molecules was skipped." );

        list = new List(container, SWT.BORDER);
        final FormData fd_list = new FormData();
        fd_list.top = new FormAttachment(importerdLabel, 23, SWT.DEFAULT);
        fd_list.right = new FormAttachment(100, -5);
        fd_list.left = new FormAttachment(0, 5);
        list.setLayoutData(fd_list);
        
        java.util.List<String> items = new LinkedList<String>();
        for ( Integer i : failures.keySet() ) {
            Exception e = failures.get( i );
            items.add( i + " | " + e.getClass().getSimpleName() + ": " 
                                 + e.getMessage() );
            keysInList.add( i );
        }
        list.setItems( items.toArray( new String[0] ) );
        list.addSelectionListener( new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {}
            public void widgetSelected( SelectionEvent e ) {
                text.setText( 
                    LogUtils.traceStringOf( 
                        failures.get( 
                            keysInList.get( list.getSelectionIndex() ) 
                ) ) );
            }
        });

        text = new Text(container, SWT.BORDER);
        final FormData fd_text = new FormData();
        fd_text.left = new FormAttachment(0, 5);
        fd_text.bottom = new FormAttachment(100, -5);
        fd_text.right = new FormAttachment(list, 0, SWT.RIGHT);
        fd_text.top = new FormAttachment(list, 39, SWT.DEFAULT);
        text.setLayoutData(fd_text);
        text.setEditable( false );

        final Label label_1 = new Label(container, SWT.NONE);
        final FormData fd_label_1 = new FormData();
        fd_label_1.top = new FormAttachment(list, 5, SWT.BOTTOM);
        fd_label_1.left = new FormAttachment(text, 0, SWT.LEFT);
        label_1.setLayoutData(fd_label_1);
        label_1.setText(
            "Full error text. If you feel that your file should be " +
            "importable send us a bug report \n at " +
            "bioclipse-devel@lists.sf.net with this error text and the file " +
            "you were tryign to import." );
        setMessage("Import of molecules from " + fileName + " complete.");
        setTitle("Import of molecules complete");
        //
        return area;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        createButton( parent, IDialogConstants.OK_ID,
                      IDialogConstants.OK_LABEL, true );
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {

        return new Point( 500, 375 );
    }
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Import Complete");
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }

}

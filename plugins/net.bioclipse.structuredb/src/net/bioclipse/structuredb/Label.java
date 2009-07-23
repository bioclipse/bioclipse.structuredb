/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.structuredb.domain.TextAnnotation;

/**
 * @author jonalv
 */
public class Label implements IEditorInput {

    private StructureDBInstance parent;
    private Logger logger;
    private TextAnnotation annotation;
    private String name;

    public Label(TextAnnotation textAnnotation, StructureDBInstance parent) {
        this.name       = textAnnotation.getValue();
        this.parent     = parent;
        this.annotation = textAnnotation;
    }

    public StructureDBInstance getParent() {
        return parent;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return getName();
    }
    
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        
        if ( adapter.isAssignableFrom( IMoleculesEditorModel.class ) ) {
            return new DBMoleculesEditorModel( parent.getName(), 
                                               getAnnotation() );
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public void doubleClick() {
        IWorkbenchPage wPage = PlatformUI.getWorkbench()
                                         .getActiveWorkbenchWindow()
                                         .getActivePage();
        try {
            wPage.openEditor( this, "net.bioclipse.cdk.ui.sdfeditor" );
        } 
        catch ( PartInitException e ) {
            logger.debug("Failed to open editor for Annotaion" );                
        }
    }
    
    public TextAnnotation getAnnotation() {

        return annotation;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
}

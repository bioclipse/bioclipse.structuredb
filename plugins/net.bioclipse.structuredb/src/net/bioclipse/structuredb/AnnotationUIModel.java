/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.cdk.ui.views.IMoleculesEditorModel;
import net.bioclipse.services.views.model.AbstractServiceObject;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IServiceContainer;
import net.bioclipse.structuredb.actions.OpenAnnotationAction;
import net.bioclipse.structuredb.domain.TextAnnotation;


/**
 * @author jonalv
 */
public class AnnotationUIModel extends AbstractServiceObject 
                               implements IDatabase, IEditorInput {

    private Database parent;
    private Logger logger;
    private TextAnnotation annotation;

    public AnnotationUIModel(TextAnnotation textAnnotation, Database parent) {
        setName( textAnnotation.getValue() );
        this.parent     = parent;
        this.annotation = textAnnotation;
    }

    public boolean drop( Object data ) {
        return false;
    }
    
    public IServiceContainer getParent() {
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
    @Override
    public Object getAdapter( Class adapter ) {
        
        if ( adapter.isAssignableFrom( IMoleculesEditorModel.class ) ) {
            return new DBMoleculesEditorModel( parent.getName(), 
                                               annotation );
        }
        return super.getAdapter( adapter );
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
    
    @Override
    public String getIcon() {
        return "icons/many_molecules.png";
    }
}

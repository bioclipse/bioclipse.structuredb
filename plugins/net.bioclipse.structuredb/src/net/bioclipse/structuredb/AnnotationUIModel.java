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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import net.bioclipse.services.views.model.AbstractServiceObject;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IServiceContainer;


/**
 * @author jonalv
 */
public class AnnotationUIModel extends AbstractServiceObject 
                               implements IDatabase, IEditorInput {

    private Database parent;

    public AnnotationUIModel(String name, Database parent) {
        setName( name );
        this.parent = parent;
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
}

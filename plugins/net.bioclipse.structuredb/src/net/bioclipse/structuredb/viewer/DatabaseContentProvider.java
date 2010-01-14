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
package net.bioclipse.structuredb.viewer;

import net.bioclipse.databases.DatabasesRoot;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.StructuredbFactory;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * @author jonalv
 *
 */
public class DatabaseContentProvider implements ITreeContentProvider {

    private DeferredTreeContentManager contentManager;
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        if (v instanceof AbstractTreeViewer) {
            contentManager = new DeferredTreeContentManager(
                                     (AbstractTreeViewer) v );
        }
    }

    public void dispose() {
    }

    public Object[] getElements(Object parentElement) {
        return getChildren( parentElement );
    }

    public Object[] getChildren(Object parentElement) {
        
        if (parentElement instanceof DatabasesRoot ) {
            return new Object[] { StructuredbFactory.getStructuredb() };
        }
        
        if (parentElement instanceof Structuredb) {
            Structuredb structuredb = (Structuredb)parentElement;
            return structuredb.getChildren().toArray();
        }
        
        if (parentElement instanceof StructureDBInstance) {
            return contentManager.getChildren( parentElement );
        }
        
        return new Object[0];
    }
    
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean hasChildren(Object element) {
        if (element instanceof Structuredb) {
            return true;
        }
        if ( element instanceof StructureDBInstance ) {
            return contentManager.mayHaveChildren( element );
        }
        if ( element instanceof PendingUpdateAdapter ) {
            return false;
        }
        if ( element instanceof Label ) {
            return false;
        }
        return true;
    }
}

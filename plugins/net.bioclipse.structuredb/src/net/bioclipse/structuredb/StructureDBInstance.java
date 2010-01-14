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

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.structuredb.business.IStructureDBChangeListener;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

/**
 * @author jonalv
 *
 */
public class StructureDBInstance implements IStructureDBChangeListener,
                                            IDatabasesElement,
                                            IDeferredWorkbenchAdapter {

    private Logger logger = Logger.getLogger( this.getClass() );
    private String name;
    private List<Label> cachedChildren;
    private Structuredb parent;
    
    public StructureDBInstance( String name, Structuredb structuredb ) {
        this.name = name;
        Activator.getDefault().getStructuredbManager().addListener(this);
        this.parent = structuredb;
    }
    
    public List<Label> getChildren() {
        IJavaStructuredbManager structureDB = Activator.getDefault()
                                                   .getStructuredbManager();
        if ( cachedChildren != null ) {
            return cachedChildren;
        }
        List<Label> children = new ArrayList<Label>();
        for ( net.bioclipse.structuredb.domain.TextAnnotation a : 
              structureDB.allLabels( name ) ) {
            children.add( new Label( a, this ) );
        }
        cachedChildren = children;
        return cachedChildren;
    }
    
    private void clearCachedChildren() {
        cachedChildren = null;
    }

    public void onDataBaseUpdate( DatabaseUpdateType updateType ) {
        if ( updateType == DatabaseUpdateType.LABELS_CHANGED ) {
            clearCachedChildren();
        }
    }
    
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfMolecules() {

        return Activator.getDefault().getStructuredbManager()
                                     .numberOfMoleculesInDatabaseInstance(name);
    }

    public void fetchDeferredChildren( Object object,
                                       IElementCollector collector,
                                       IProgressMonitor monitor ) {
        collector.add( getChildren().toArray(), monitor );
    }

    public ISchedulingRule getRule( Object object ) {
        return null;
    }

    public boolean isContainer() {
        return true;
    }

    public Object[] getChildren( Object o ) {
        return getChildren().toArray();
    }

    public ImageDescriptor getImageDescriptor( Object object ) {
        return null;
    }

    public String getLabel( Object o ) {
        return name;
    }

    public Object getParent( Object o ) {
        return parent;
    }
}

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

/**
 * @author jonalv
 *
 */
public class StructureDBInstance implements IStructureDBChangeListener,
                                            IDatabasesElement {

    private Logger logger = Logger.getLogger( this.getClass() );
    private String name;
    private List<Label> cachedChildren;
    
    public StructureDBInstance( String name ) {
        this.name = name;
        Activator.getDefault().getStructuredbManager().addListener(this);
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
}

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

import net.bioclipse.structuredb.business.IDatabaseListener;
import net.bioclipse.structuredb.internalbusiness.StructuredbInstanceManager;

import org.apache.log4j.Logger;

/**
 * 
 * @author jonalv
 *
 */
public class Structuredb implements IDatabaseListener {

    private final Logger logger = Logger.getLogger( this.getClass() );

    private final String name = "StructureDB";
    
    private List<StructureDBInstance> cachedChildren;

    public Structuredb() {
        Activator.getDefault().getStructuredbManager().addListener(this);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
    
    public List<StructureDBInstance> getChildren() {
        if ( cachedChildren != null ) {
            return cachedChildren;
        }
        List<StructureDBInstance> children 
            = new ArrayList<StructureDBInstance>();
        for ( String s : Activator.getDefault()
                        .getStructuredbManager()
                        .allDatabaseNames() ) {
            StructureDBInstance instance = new StructureDBInstance(s);
            children.add( instance );
        }
        cachedChildren = children;
        return cachedChildren;
    }
    
    private void clearChildren() {
        if ( cachedChildren != null ) {
            for ( StructureDBInstance instance : cachedChildren ) {
                Activator.getDefault()
                         .getStructuredbManager()
                         .removeListener( (IDatabaseListener)instance );
            }
        }
        cachedChildren = null;
    }
    

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onDataBaseUpdate( DatabaseUpdateType updateType ) {
        if ( updateType == DatabaseUpdateType.DATABASES_CHANGED ) {
            clearChildren();
        }
    }
}

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
package net.bioclipse.structuredb;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.services.views.model.AbstractServiceContainer;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IDatabaseType;
import net.bioclipse.services.views.model.IServiceObject;
import net.bioclipse.structuredb.business.IDatabaseListener;

import org.apache.log4j.Logger;

import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.ChildrenIterator;

/**
 * 
 * @author jonalv
 *
 */
public class Structuredb extends AbstractServiceContainer
                         implements IDatabaseType, IDatabaseListener {

    private final Logger logger = Logger.getLogger( this.getClass() );

    private final String name = "Structure Database";

    public Structuredb() {
        Activator.getDefault().getStructuredbManager().addListener(this);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Override
    public void createChildren() {
        
        // remove all children from listening 
        // (otherwise the garbage collector won't have a chance...)
        if ( children != null ) {
            for ( IServiceObject db : children ) {
                if( db instanceof IDatabaseListener)
                Activator.getDefault()
                         .getStructuredbManager()
                         .removeListener( (IDatabaseListener)db );
            }
        }
        
        List<IDatabase> children 
            = new ArrayList<IDatabase>();
        
        for ( String s : Activator.getDefault()
                                  .getStructuredbManager()
                                  .listDatabaseNames() ) {
            children.add( new DataBase(s) );
        }
        setChildren( children );
    }

    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onDataBaseUpdate( DatabaseUpdateType updateType ) {
        if ( updateType == DatabaseUpdateType.DATABASES_CHANGED ) {
            createChildren();
            fireChanged( this );
        }
    }
}

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
import net.bioclipse.services.views.model.AbstractServiceObject;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IServiceContainer;
import net.bioclipse.services.views.model.IServiceObject;
import net.bioclipse.structuredb.business.IDatabaseListener;
import net.bioclipse.structuredb.business.IStructuredbManager;
import net.bioclipse.structuredb.business.IDatabaseListener.DatabaseUpdateType;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.springframework.context.ApplicationContext;

/**
 * @author jonalv
 *
 */
public class DataBase extends AbstractServiceContainer 
                      implements IDatabase, IDatabaseListener {

    public DataBase( String name ) {
        setName( name );
        Activator.getDefault().getStructuredbManager().addListener(this);
    }
    
    public void drop( Object data ) {

        
    }

    @Override
    public void createChildren() {

        // remove all children from listening 
        // (otherwise the garbage collector won't have a chance...)
        if ( children != null ) {
            for ( IServiceObject db : children ) {
                if ( db instanceof IDatabaseListener )
                Activator.getDefault()
                         .getStructuredbManager()
                         .removeListener( (IDatabaseListener)db );
            }
        }
        
        List<IDatabase> children 
            = new ArrayList<IDatabase>();
        
        for ( net.bioclipse.structuredb.domain.Label l : 
                         Activator.getDefault()
                                  .getStructuredbManager()
                                  .allLabels( getName() ) ) {
            children.add( new Label( l.getName() ) );
        }
        setChildren( children );
    }

    public void onDataBaseUpdate( DatabaseUpdateType updateType ) {

        if ( updateType == DatabaseUpdateType.LABELS_CHANGED ) {
            createChildren();
            fireChanged( this );
        }
    }
}

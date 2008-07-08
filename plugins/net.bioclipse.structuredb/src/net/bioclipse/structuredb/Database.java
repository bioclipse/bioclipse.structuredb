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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.services.views.model.AbstractServiceContainer;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IServiceObject;
import net.bioclipse.structuredb.business.IDatabaseListener;

/**
 * @author jonalv
 *
 */
public class Database extends AbstractServiceContainer 
                      implements IDatabase, IDatabaseListener {

    public Database( String name ) {
        setName( name );
        Activator.getDefault().getStructuredbManager().addListener(this);
    }
    
    public boolean drop( Object data ) {
        
        if ( data instanceof ITreeSelection ) {
            ITreeSelection selections = (ITreeSelection)data;
            for ( Object selection : selections.toArray() ) {
                if (selection instanceof IFile) {
                    IFile file = (IFile)selection;
                    try {
                        
                        Activator.getDefault()
                                 .getStructuredbManager()
                                 .addStructuresFromSDF( getName(), 
                                                        file.getLocationURI().toString() );
                    } catch ( BioclipseException e ) {
                        return false;
                    }
                }
                selection.toString();
            }
        }
        return false;
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
            children.add( new Label( l.getName(), this ) );
            
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

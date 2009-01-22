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

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.services.views.model.AbstractServiceContainer;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IServiceObject;
import net.bioclipse.structuredb.business.IDatabaseListener;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.PlatformUI;

/**
 * @author jonalv
 *
 */
public class Database extends AbstractServiceContainer 
                      implements IDatabase, IDatabaseListener {

    private Logger logger = Logger.getLogger( Database.class );
    
    public Database( String name ) {
        setName( name );
        Activator.getDefault().getStructuredbManager().addListener(this);
    }
    
    public boolean drop( Object data ) {
        
        if ( data instanceof ITreeSelection ) {
            ITreeSelection selections = (ITreeSelection)data;
            for ( Object selection : selections.toArray() ) {
                if (selection instanceof IFile) {
                    final IFile file = (IFile)selection;
                    final String dbName = getName();
                    try {
                        Activator.getDefault().getStructuredbManager()
                                              .addMoleculesFromSDF( dbName, 
                                                                    file );
                    } catch ( BioclipseException e ) {
                        LogUtils.debugTrace( logger, e );
                        MessageDialog.openError( PlatformUI
                            .getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getShell(),
                            "Could not import moleculs",
                            "More information can be found in the log file" ); 
                    }
                }
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
        
        for ( net.bioclipse.structuredb.domain.TextAnnotation a : 
                         Activator.getDefault()
                                  .getStructuredbManager()
                                  .allLabels( getName() ) ) {
            children.add( new Label( a, 
                                                 this ) );
            
        }
        setChildren( children );
    }

    public void onDataBaseUpdate( DatabaseUpdateType updateType ) {

        if ( updateType == DatabaseUpdateType.LABELS_CHANGED ) {
            createChildren();
            fireChanged( this );
        }
    }

    public void doubleClick() {
        // TODO Auto-generated method stub
    }
    
    @Override
    public String getIcon() {
        return "icons/db_with_mol_16.png";
    }
    
    public String toString() {
        return getName();
    }
}

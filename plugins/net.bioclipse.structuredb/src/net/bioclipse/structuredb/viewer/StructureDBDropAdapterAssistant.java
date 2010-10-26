/* *****************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.viewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.chemoinformatics.util.ChemoinformaticUtils;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.actions.ImportMoleculesAction;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;


/**
 * @author jonalv
 *
 */
public class StructureDBDropAdapterAssistant 
       extends CommonDropAdapterAssistant {

    private Logger logger = Logger.getLogger( this.getClass() );
    
    @Override
    public IStatus handleDrop( CommonDropAdapter dropAdapter,
                               DropTargetEvent dropTargetEvent,
                               Object target ) {

        if ( target instanceof StructureDBInstance) {

            StructureDBInstance structureDBInstance = (StructureDBInstance) 
            target;

            if ( dropTargetEvent.data instanceof ITreeSelection ) {
                ITreeSelection selections = (ITreeSelection)dropTargetEvent.data;
                List<IFile> files = new ArrayList<IFile>();
                for ( Object selection : selections.toArray() ) {
                    if ( selection instanceof IFile ) {
                        files.add( (IFile) selection );
                    }
                }
                    
                final String dbName = structureDBInstance.getName();
                Activator.getDefault()
                         .getStructuredbManager()
                         .addMoleculesFromFiles( 
                             dbName, 
                             files,
                             new ImportMoleculesAction.ImportMoleculesUIJob() );
                return Status.OK_STATUS;
            }
        }
        return Status.CANCEL_STATUS;
    }

    @Override
    public IStatus validateDrop( Object target,
                                 int operation,
                                 TransferData transferType ) {

        if ( target instanceof StructureDBInstance ) {
            return Status.OK_STATUS;
        }
        return Status.CANCEL_STATUS;
    }
}

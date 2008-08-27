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
package net.bioclipse.structuredb.business;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.structuredb.domain.Structure;


/**
 * @author jonalv
 *
 */
public class SmartsQueryIterator implements Iterator<Structure> {

    private Structure next = null;
    private Iterator<Structure> parent;
    private ICDKManager cdk;
    private String smarts;
    private IStructuredbManager structuredb;
    private IProgressMonitor monitor;
    
    public SmartsQueryIterator( Iterator<Structure> 
                                    allStructuresIterator,
                                ICDKManager cdk, 
                                String smarts,
                                StructuredbManager structuredbManager, 
                                IProgressMonitor monitor) {

        parent = allStructuresIterator;
        this.cdk = cdk;
        this.smarts = smarts;
        this.structuredb = structuredbManager;
        this.monitor = monitor;
    }

    public boolean hasNext() {

        if( next != null ) {
            return true;
        }
        try {
            next = findNext();
        } 
        catch ( BioclipseException e ) {
            throw new RuntimeException(e);
        }
        return next != null;
    }

    private Structure findNext() throws BioclipseException {

        while( parent.hasNext() ) {
            Structure next = parent.next();
            if(monitor != null) {
                monitor.worked( 1 );
            }
            ICDKMolecule molecule;
            molecule = structuredb.toCDKMolecule( next );
            if( cdk.smartsMatches( molecule, smarts ) ) {
                return next;
            }
        }
        if( monitor != null ) {
            monitor.done();
        }
        return null;
    }

    public Structure next() {

        if( !hasNext() ) {
            throw new IllegalStateException( "there are no more " +
                                             "such structures" );
        }
        Structure next = this.next;
        this.next = null;
        return next;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

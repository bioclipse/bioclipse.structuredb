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

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.structuredb.domain.DBMolecule;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author jonalv
 *
 */
public class SubStructureIterator implements Iterator<DBMolecule> {

    private DBMolecule next = null;
    private Iterator<DBMolecule> parent;
    private ICDKManager cdk;
    private ICDKMolecule subStructure;
    private IStructuredbManager structuredb;
    private IProgressMonitor monitor;

    public SubStructureIterator( Iterator<DBMolecule> iterator, 
                                 ICDKManager cdk,
                                 ICDKMolecule subStructure,
                                 IStructuredbManager structuredb, 
                                 IProgressMonitor monitor ) {
        parent   = iterator;
        this.cdk = cdk;
        this.subStructure = subStructure;
        this.structuredb = structuredb;
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

    private DBMolecule findNext() throws BioclipseException {

        while( parent.hasNext() ) {
            DBMolecule next = parent.next();
            if(monitor != null) {
                monitor.worked( 1 );
            }
            if( cdk.subStructureMatches( next, subStructure ) ) {
                return next;
            }
        }
        if( monitor != null ) {
            monitor.done();
        }
        return null;
    }

    public DBMolecule next() {

        if( !hasNext() ) {
            throw new IllegalStateException( "there are no more " +
                                             "such structures" );
        }
        DBMolecule next = this.next;
        this.next = null;
        return next;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

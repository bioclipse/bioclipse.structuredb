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
package net.bioclipse.structuredb.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.exceptions.TimedOutException;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.util.TimeCalculater;
import net.bioclipse.structuredb.domain.DBMolecule;


/**
 * @author jonalv
 *
 */
public class SmartsQueryIterator implements Iterator<DBMolecule> {

    private DBMolecule next = null;
    private Iterator<DBMolecule> parent;
    private CDKManager cdk;
    private String smarts;
    private IStructuredbManager structuredb;
    private IProgressMonitor monitor;
    private int numOfMolecules;
    private int current = 1;
    private List<DBMolecule> failedMolecules = new ArrayList<DBMolecule>();
    private long startTime;
    
    public SmartsQueryIterator( Iterator<DBMolecule> allStructuresIterator,
                                CDKManager cdk, 
                                String smarts,
                                StructuredbManager structuredbManager, 
                                int numOfMolecules, 
                                IProgressMonitor monitor ) {

        parent = allStructuresIterator;
        this.cdk = cdk;
        this.smarts = smarts;
        this.structuredb = structuredbManager;
        this.monitor = monitor;
        this.numOfMolecules = numOfMolecules;
        this.startTime = System.currentTimeMillis();
    }

    public boolean hasNext() {

        if ( next != null ) {
            return true;
        }
        while (true) {
            try {
                next = findNext();
            }
            catch ( BioclipseException e ) {
                throw new RuntimeException(e);
            }
            break;
        }
        return next != null;
    }

    private DBMolecule findNext() throws BioclipseException {

        while ( parent.hasNext() ) {
            DBMolecule next = parent.next();
            if ( monitor != null ) {
                monitor.worked( 1 );
                monitor.subTask( 
                    current++ + "/" + numOfMolecules + " processed. (" 
                    + TimeCalculater.generateTimeRemainEst( startTime, 
                                                            current, 
                                                            numOfMolecules )
                    + ")" );
                if ( monitor.isCanceled() ) {
                    throw new OperationCanceledException();
                }
            }
            try {
                if ( cdk.smartsMatches( next, smarts ) ) {
                    return next;
                }
            }
            catch ( TimedOutException e ) {
                failedMolecules.add(next);
                continue;
            }
        }
        if ( monitor != null ) {
            monitor.done();
        }
        return null;
    }

    public DBMolecule next() {

        if ( !hasNext() ) {
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
    
    public boolean hasFailedMolecules() {
        return failedMolecules.size() != 0;
    }
    
    public List<DBMolecule> getFailedMolecules() {
        return new ArrayList<DBMolecule>(failedMolecules);
    }
}

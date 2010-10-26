/* *****************************************************************************
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

import java.util.Iterator;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.util.TimeCalculator;
import net.bioclipse.structuredb.domain.DBMolecule;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;


/**
 * @author jonalv
 *
 */
public class SubStructureIterator implements Iterator<DBMolecule> {

    private DBMolecule next = null;
    private Iterator<DBMolecule> parent;
    private ICDKManager cdk;
    private ICDKMolecule subStructure;
    private IProgressMonitor monitor;
    private int ticks;
    private int currentTick = 0;
    private long startTime;

    public SubStructureIterator( Iterator<DBMolecule> iterator, 
                                 ICDKManager cdk,
                                 ICDKMolecule subStructure,
                                 IProgressMonitor monitor,
                                 int ticks) {
        parent   = iterator;
        this.cdk = cdk;
        this.subStructure = subStructure;
        this.monitor = monitor;
        this.ticks = ticks;
        this.startTime = System.currentTimeMillis();
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
                
                monitor.worked(1);
                String s = "Finger print search hits processed: " 
                           + ++currentTick +"/" + ticks;
                if ( System.currentTimeMillis() - startTime > 5000 ) {
                    s += " (" 
                      + TimeCalculator.generateTimeRemainEst( startTime, 
                                                              currentTick, 
                                                              ticks )
                      + ")"; 
                }
                monitor.subTask(s);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            }
            if( cdk.subStructureMatches( next, subStructure ) ) {
                return next;
            }
        }
        if ( monitor != null ) {
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

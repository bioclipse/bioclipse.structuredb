/*******************************************************************************
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

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.business.IStructureDBChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;


/**
 * @author jonalv
 *
 */
public class StructureDBLightweightLabelDecorator 
       extends LabelProvider
       implements ILightweightLabelDecorator, 
                  IStructureDBLabelDecoratorChangeListener {

    private Logger logger = Logger.getLogger( this.getClass() );
    
    public StructureDBLightweightLabelDecorator() {
        Activator.getDefault()
                 .publishStructureDBDecoratorChangeListener( this );
    }
    
    public void decorate( Object element, IDecoration decoration ) {

        try {
            if ( element instanceof StructureDBInstance ) {
                int n = ( (StructureDBInstance) element ).getNumberOfMolecules();
                decoration.addSuffix( " [" + n + "]" );
            }
            if ( element instanceof Label ) {
                int n = ( (Label) element ).getNumberOfMolecules();
                decoration.addSuffix( " [" + n + "]" );
            }
        }
        catch (Exception e) {
            logger.debug( "Exception when trying to decorate StructureDB " +
            		       "part of databaseview" );
            LogUtils.debugTrace( logger, e );
        }
    }

    public void fireRefresh() {
        Display.getDefault().asyncExec( new Runnable() {

            public void run() {
                fireLabelProviderChanged( 
                    new LabelProviderChangedEvent(
                        StructureDBLightweightLabelDecorator.this ) );
            }
        });
    }
}

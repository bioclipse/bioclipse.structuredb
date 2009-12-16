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
package net.bioclipse.structuredb.viewer;

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.StructureDBInstance;
import net.bioclipse.structuredb.Structuredb;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author jonalv
 *
 */
public class DatabaseLabelProvider extends LabelProvider 
                                   implements ITableLabelProvider {

    Map<String, Image> images = new HashMap<String, Image>() {
        private static final long serialVersionUID = 1L;
        {
           put( "db_16", 
                new Image( Display.getDefault(), 
                           DatabaseLabelProvider.class.getResourceAsStream( 
                               "/icons/db_16.gif") ) );
           put( "db_mol", 
                new Image( Display.getDefault(), 
                           DatabaseLabelProvider.class.getResourceAsStream( 
                               "/icons/db_with_mol_16.png") ) );
           put( "label",
                new Image( Display.getDefault(),
                           DatabaseLabelProvider.class.getResourceAsStream( 
                               "/icons/label.png") ) );
        }
    };
    
    public String getColumnText(Object obj, int index) {
        return getText(obj);
    }
    
    public Image getColumnImage(Object obj, int index) {
        return getImage(obj);
    }
    
    public Image getImage(Object obj) {
        if ( obj instanceof Structuredb )
            return images.get( "db_16" );
        if ( obj instanceof StructureDBInstance ) {
            return images.get( "db_mol" );
        }
        if ( obj instanceof Label ) {
            return images.get( "label" );
        }
        return PlatformUI.getWorkbench()
                         .getSharedImages()
                         .getImage( ISharedImages.IMG_OBJ_ELEMENT );
    }
    
    @Override
    public void dispose() {
        super.dispose();
        for (Image i : images.values()) {
            i.dispose();
        }
    }
}

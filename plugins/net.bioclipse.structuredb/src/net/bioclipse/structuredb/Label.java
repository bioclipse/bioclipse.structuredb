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

import net.bioclipse.services.views.model.AbstractServiceObject;
import net.bioclipse.services.views.model.IDatabase;


/**
 * @author jonalv
 */
public class Label extends AbstractServiceObject 
                   implements IDatabase {

    public Label(String name) {
        setName( name );
    }

    public boolean drop( Object data ) {

        return false;
    }
}

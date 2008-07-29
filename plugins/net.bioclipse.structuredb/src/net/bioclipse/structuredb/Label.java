/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
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
import net.bioclipse.services.views.model.IServiceContainer;


/**
 * @author jonalv
 */
public class Label extends AbstractServiceObject 
                   implements IDatabase {

    private Database parent;

    public Label(String name, Database parent) {
        setName( name );
        this.parent = parent;
    }

    public boolean drop( Object data ) {

        return false;
    }
    
    public IServiceContainer getParent() {
        return parent;
    }
}

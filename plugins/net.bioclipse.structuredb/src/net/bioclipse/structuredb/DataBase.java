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

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.services.views.model.AbstractServiceObject;
import net.bioclipse.services.views.model.IDatabase;
import net.bioclipse.services.views.model.IServiceContainer;
import net.bioclipse.services.views.model.IServiceObject;
import net.bioclipse.structuredb.business.IStructuredbManager;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.springframework.context.ApplicationContext;

/**
 * @author jonalv
 *
 */
public class DataBase extends AbstractServiceObject 
                      implements IDatabase {

    public DataBase( String name ) {
        setName( name );
    }
    
    public void drop( Object data ) {

        
    }
}

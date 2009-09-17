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

import net.bioclipse.structuredb.Activator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * 
 * @author jonalv
 */
public class StructuredbManagerFactory 
             implements IExecutableExtension, 
                        IExecutableExtensionFactory {

    private Logger logger = Logger.getLogger(
                                StructuredbManagerFactory.class );
    private Object structuredbManager;
    
    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, 
                                       Object data ) 
                throws CoreException {
    
        structuredbManager = Activator.getDefault()
                                      .getJavaScriptStructuredbManager();
    }

    public Object create() throws CoreException {
        
        return structuredbManager;
    }
}

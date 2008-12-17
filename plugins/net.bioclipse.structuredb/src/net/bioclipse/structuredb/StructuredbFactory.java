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
package net.bioclipse.structuredb;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
/**
 * @author jonalv
 *
 */
public class StructuredbFactory implements IExecutableExtension,
        IExecutableExtensionFactory {
    private Structuredb structuredb;
    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, 
                                       Object data ) 
                throws CoreException {
        structuredb = new Structuredb();
    }
    public Object create() throws CoreException {
        return this.structuredb;
    }
}

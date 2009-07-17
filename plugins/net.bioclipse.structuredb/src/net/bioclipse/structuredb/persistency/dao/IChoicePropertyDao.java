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
package net.bioclipse.structuredb.persistency.dao;

import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.Property;


/**
 * @author jonalv
 *
 */
public interface IChoicePropertyDao 
       extends IGenericDao<ChoiceProperty> {

    public ChoiceProperty getByName( String propertyName );

}

/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.domain;
/**
 * @author jonalv
 *
 */
public abstract class Property extends BaseObject {
    private String name;
    public Property() {
        super();
        name = getId();
    }
    public Property(String name) {
        super();
        this.name = name;
    }
    public Property(Property property) {
        super(property);
        this.name = property.name;
    }
    public boolean hasValuesEqualTo( BaseObject obj ) {
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof Property) ) {
            return false;
        }
        return ((Property)obj).getName().equals(name);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

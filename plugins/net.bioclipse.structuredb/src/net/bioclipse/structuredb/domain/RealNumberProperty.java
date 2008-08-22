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
package net.bioclipse.structuredb.domain;

import java.util.List;


/**
 * @author jonalv
 *
 */
public class RealNumberProperty extends Property {

    public RealNumberProperty() {
        super();
    }

    public RealNumberProperty(String name) {
        super( name );
    }

    public RealNumberProperty(RealNumberProperty realNumberProperty1) {

        // TODO Auto-generated constructor stub
    }

    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if ( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if ( !(obj instanceof RealNumberProperty) ) {
            return false;
        }
        return true;
    }

    public List<RealNumberAnnotation> getAnnotations() {

        // TODO Auto-generated method stub
        return null;
    }

    public void addAnnotation( RealNumberAnnotation annotation ) {

        // TODO Auto-generated method stub
        
    }
}

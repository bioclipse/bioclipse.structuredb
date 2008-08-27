/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jonalv
 *
 */
public class RealNumberProperty extends Property {

    private List<RealNumberAnnotation> annotations;

    public RealNumberProperty() {
        super();
        annotations = new ArrayList<RealNumberAnnotation>(); 
    }

    public RealNumberProperty(String name) {
        super( name );
        annotations = new ArrayList<RealNumberAnnotation>();
    }

    public RealNumberProperty(RealNumberProperty realNumberProperty) {
        super( realNumberProperty );
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
        return annotations;
    }

    public void addAnnotation( RealNumberAnnotation annotation ) {
        annotations.add( annotation );
        if ( annotation.getProperty() != this ) {
            annotation.setProperty( this );
        }
    }
}

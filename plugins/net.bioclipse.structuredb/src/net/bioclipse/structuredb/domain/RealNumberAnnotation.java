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


/**
 * @author jonalv
 *
 */
public class RealNumberAnnotation extends Annotation {

    private double value;
    
    public RealNumberAnnotation() {
        super();
    }

    public RealNumberAnnotation(double value) {
        super( value + "" );
        this.value = value;
    }

    public RealNumberAnnotation( 
        RealNumberAnnotation realNumberAnnotation ) {

        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see net.bioclipse.structuredb.domain.BaseObject#hasValuesEqualTo(net.bioclipse.structuredb.domain.BaseObject)
     */
    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof RealNumberAnnotation) ) {
            return false;
        }
        RealNumberAnnotation annotation = (RealNumberAnnotation)obj;
        return Double.compare( getValue(), annotation.getValue() ) == 0;
    }


    public double getValue() {

        return value;
    }

    public void setProperty( RealNumberProperty property ) {

        // TODO Auto-generated method stub
        
    }

    public RealNumberProperty getProperty() {

        // TODO Auto-generated method stub
        return null;
    }
    
}

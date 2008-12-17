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
public class RealNumberAnnotation extends Annotation {
    private double value;
    private RealNumberProperty property;
    public RealNumberAnnotation() {
        super();
        this.setProperty( new RealNumberProperty() );
    }
    public RealNumberAnnotation(double value, RealNumberProperty property) {
        super();
        this.setValue( value );
        this.setProperty( property );
    }
    public RealNumberAnnotation( RealNumberAnnotation realNumberAnnotation ) {
        super( realNumberAnnotation );
        this.setValue( realNumberAnnotation.getValue() );
        this.setProperty( realNumberAnnotation.getProperty() );
    }
    public boolean hasValuesEqualTo( BaseObject obj ) {
        if ( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if ( !(obj instanceof RealNumberAnnotation) ) {
            return false;
        }
        RealNumberAnnotation annotation = (RealNumberAnnotation)obj;
        return Double.compare( getValue(), annotation.getValue() ) == 0 &&
               property.hasValuesEqualTo( annotation.getProperty() );
    }
    public Double getValue() {
        return value;
    }
    public void setProperty( RealNumberProperty property ) {
        this.property = property;
        if ( !property.getAnnotations().contains( this ) ) {
            property.addAnnotation( this );
        }
    }
    public RealNumberProperty getProperty() {
        return property;
    }
    public void setValue( double value ) {
        this.value = value;
    }
    @Override
    public String getSortOf() {
        return "RealNumber";
    }
}

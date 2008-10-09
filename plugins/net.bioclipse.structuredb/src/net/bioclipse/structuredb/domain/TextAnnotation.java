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
public class TextAnnotation extends Annotation {

    private String value;
    private TextProperty property;
    
    public TextAnnotation() {
        super();
        value = "";
    }

    public TextAnnotation(String value) {
        super();
        this.setValue( value );
    }

    public TextAnnotation(TextAnnotation textAnnotation) {
        super( textAnnotation );
        this.setValue( textAnnotation.getValue() );
    }

    public String getValue() {
        return value;
    }
        
    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof TextAnnotation) ) {
            return false;
        }
        TextAnnotation annotation = (TextAnnotation)obj;
        return getValue().equals( annotation.getValue() );
    }

    /**
     * @param property
     */
    public void setProperty( TextProperty property ) {
        this.property = property;
        if ( !property.getAnnotations().contains( this ) ) {
            property.addAnnotation( this );
        }
    }

    /**
     * @return
     */
    public TextProperty getProperty() {
        return property;
    }

    public void setValue( String value ) {

        this.value = value;
    }
}

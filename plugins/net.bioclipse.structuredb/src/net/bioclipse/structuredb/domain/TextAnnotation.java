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

import net.bioclipse.structuredb.persistency.dao.TextPropertyDao;


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
        this.property = new TextProperty();
    }

    public TextAnnotation(String value, TextProperty property) {
        super();
        this.setValue( value );
        this.property = property;
    }

    public TextAnnotation(TextAnnotation textAnnotation) {
        super( textAnnotation );
        this.setValue( textAnnotation.getValue() );
        this.property = new TextProperty( textAnnotation.getProperty() );
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
        return getValue().equals( annotation.getValue() ) &&
               property.hasValuesEqualTo( annotation.getProperty() );
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

    @Override
    public String getSortOf() {
        return "Text";
    }
    
    public String toString() {
        return "{TextAnnotation : value = " + value + ", " 
                                +"property = " + property.getName() + "}"; 
    }
}

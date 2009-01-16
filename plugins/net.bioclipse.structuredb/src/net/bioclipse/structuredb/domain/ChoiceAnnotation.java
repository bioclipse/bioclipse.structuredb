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
public class ChoiceAnnotation extends Annotation {

    private String value;
    private ChoiceProperty property;
    
    public ChoiceAnnotation() {
        super();
        value = "";
        property = new ChoiceProperty();
    }

    public ChoiceAnnotation(String value, ChoiceProperty choiceProperty) 
           throws IllegalArgumentException {
        
        super();
        boolean valueOk = false;
        for ( PropertyChoice p : choiceProperty.getPropertyChoices() ) {
            if ( p.getValue().equals( value ) ) {
                valueOk = true;
                break;
            }
        }
        if ( !valueOk ) {
            throw new IllegalArgumentException(
                "The ChoiceProperty: " + choiceProperty.getName() 
                + " does not support the value: " + value );
        }
        this.setValue( value );
        this.property = choiceProperty;
    }
    
    public ChoiceAnnotation(ChoiceAnnotation choiceAnnotation) {
        super(choiceAnnotation);
        this.setValue( choiceAnnotation.getValue() );
        this.property = new ChoiceProperty( choiceAnnotation.getProperty() );
    }

    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof ChoiceAnnotation) ) {
            return false;
        }
        ChoiceAnnotation annotation = (ChoiceAnnotation)obj;
        return getValue().equals( annotation.getValue() ) &&
               property.hasValuesEqualTo( annotation.getProperty() );
    }

    public String getValue() {
        return this.value;
    }

    public void setProperty( ChoiceProperty property ) {
        this.property = property;
        if ( !property.getAnnotations().contains( this ) ) {
            property.addAnnotation( this );
        }
    }

    public ChoiceProperty getProperty() {
        return property;
    }

    public void setValue( String value ) {

        this.value = value;
    }

    @Override
    public String getSortOf() {
        return "Choice";
    }
    
    public String toString() {
        return "{ChoiceAnnotation : value = " + value + ", " 
                                  +"property = " + property.getName() + "}"; 
    }
}

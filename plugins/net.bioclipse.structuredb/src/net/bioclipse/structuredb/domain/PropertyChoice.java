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


public class PropertyChoice extends BaseObject {

    private String value;
    private ChoiceProperty property;
    
    public PropertyChoice() {
        this("");
    }

    public PropertyChoice(String value) {
        super();
        this.value = value;
    }

    public PropertyChoice(PropertyChoice propertyChoice) {
        super(propertyChoice);
        this.value = propertyChoice.value;
    }

    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof PropertyChoice) ) {
            return false;
        }
        return ((PropertyChoice)obj).getValue().equals(value);
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public ChoiceProperty getProperty() {
        return this.property;
    }

    public void setProperty( ChoiceProperty choiceProperty ) {
        this.property = choiceProperty;
        if ( !choiceProperty.getPropertyChoices().contains( this ) ) {
           choiceProperty.addPropertyChoice( this ); 
        }
    }
}

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
    }

    public ChoiceAnnotation(String value) {
        super( value );
        this.value = value;
    }
    
    public ChoiceAnnotation(ChoiceAnnotation choiceAnnotation) {
        super(choiceAnnotation);
        this.value = choiceAnnotation.getValue();
    }

    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof ChoiceAnnotation) ) {
            return false;
        }
        ChoiceAnnotation annotation = (ChoiceAnnotation)obj;
        return value.equals( annotation.getValue() );
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
}

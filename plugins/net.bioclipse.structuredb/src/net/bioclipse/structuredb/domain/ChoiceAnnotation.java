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
public class ChoiceAnnotation extends Annotation {

    private String value;
    
    public ChoiceAnnotation() {
        super();
    }

    public ChoiceAnnotation(String value) {
        super( value );
        this.value = value;
    }
    
    public ChoiceAnnotation(ChoiceAnnotation choiceAnnotation1) {

        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see net.bioclipse.structuredb.domain.BaseObject#hasValuesEqualTo(net.bioclipse.structuredb.domain.BaseObject)
     */
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

    public Object getValue() {
        return this.value;
    }

    public void setProperty( ChoiceProperty property ) {

        // TODO Auto-generated method stub
        
    }

    public ChoiceProperty getProperty() {

        // TODO Auto-generated method stub
        return null;
    }
}

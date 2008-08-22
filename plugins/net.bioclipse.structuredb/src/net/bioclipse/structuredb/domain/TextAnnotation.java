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
public class TextAnnotation extends Annotation {

    private String value;
    
    public TextAnnotation() {
        super();
    }

    public TextAnnotation(String value) {
        super( value );
        this.value = value;
    }

    public TextAnnotation(TextAnnotation textAnnotation1) {

        // TODO Auto-generated constructor stub
    }

    public String getValue() {
        return value;
    }
        
    /* (non-Javadoc)
     * @see net.bioclipse.structuredb.domain.BaseObject#hasValuesEqualTo(net.bioclipse.structuredb.domain.BaseObject)
     */
    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof TextAnnotation) ) {
            return false;
        }
        TextAnnotation annotation = (TextAnnotation)obj;
        return value.equals( annotation.getValue() );
    }

    public void setProperty( TextProperty property ) {

        // TODO Auto-generated method stub
        
    }

    public TextProperty getProperty() {

        // TODO Auto-generated method stub
        return null;
    }
}

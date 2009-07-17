/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author jonalv
 *
 */
public class ChoiceProperty extends Property {

    private List<ChoiceAnnotation> annotations;
    private List<PropertyChoice> propertyChoices;
    
    public List<PropertyChoice> getPropertyChoices() {
    
        return propertyChoices;
    }
    
    public void setPropertyChoices( List<PropertyChoice> propertyChoices ) {
    
        this.propertyChoices = propertyChoices;
    }

    public ChoiceProperty() {
        super();
        annotations     = new ArrayList<ChoiceAnnotation>();
        propertyChoices = new ArrayList<PropertyChoice>();
     
        propertyChoices.add( new PropertyChoice("") );
    }

    public ChoiceProperty(String name) {
        super( name );
        annotations     = new ArrayList<ChoiceAnnotation>();
        propertyChoices = new ArrayList<PropertyChoice>();
    }

    public ChoiceProperty( ChoiceProperty choiceProperty ) {
        super( choiceProperty );
        annotations 
            = new ArrayList<ChoiceAnnotation>();
        propertyChoices
            = new ArrayList<PropertyChoice>();
        for ( PropertyChoice p : choiceProperty.propertyChoices ) {
            propertyChoices.add( new PropertyChoice(p) );
        }
    }

    public List<ChoiceAnnotation> getAnnotations() {
        return annotations;
    }

    public void addAnnotation( ChoiceAnnotation annotation ) {
        annotations.add( annotation );
        if ( annotation.getProperty() != this ) {
            annotation.setProperty( this );
        }
    }

    public void addPropertyChoice( PropertyChoice propertyChoice ) {
        this.propertyChoices.add( propertyChoice );
        if ( propertyChoice.getProperty() != this ) {
            propertyChoice.setProperty(this);
        }
    }
    
    @Override
    public boolean hasValuesEqualTo( BaseObject obj ) {
        return super.hasValuesEqualTo( obj ) && 
               (obj instanceof ChoiceProperty) &&
               objectsInHasSameValues( propertyChoices, 
                                       ( (ChoiceProperty)obj )
                                          .getPropertyChoices() );
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (PropertyChoice p : propertyChoices ) {
            s.append( p.getValue() );
            s.append( ',' );
            s.append( ' ' );
        }
        s.delete( s.length()-2, s.length()-1 );
        return "{ChoiceProperty : name = " + getName() + ", " 
                               + "choices = [" + s.toString() + "]}"; 
    }
}

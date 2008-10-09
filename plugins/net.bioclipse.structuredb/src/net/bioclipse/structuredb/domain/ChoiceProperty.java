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
    private Set<PropertyChoice> propertyChoices;
    
    public Set<PropertyChoice> getPropertyChoices() {
    
        return propertyChoices;
    }
    
    public void setPropertyChoices( Set<PropertyChoice> propertyChoices ) {
    
        this.propertyChoices = propertyChoices;
    }

    public ChoiceProperty() {
        this("");
        propertyChoices.add( new PropertyChoice("") );
    }

    public ChoiceProperty(String name) {
        super( name );
        annotations     = new ArrayList<ChoiceAnnotation>();
        propertyChoices = new HashSet<PropertyChoice>();
    }

    public ChoiceProperty( ChoiceProperty choiceProperty ) {
        super( choiceProperty );
        annotations 
            = new ArrayList<ChoiceAnnotation>();
        propertyChoices
            = new HashSet<PropertyChoice>();
        for ( PropertyChoice p : propertyChoices) {
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
}

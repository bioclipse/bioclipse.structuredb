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
import java.util.List;


/**
 * @author jonalv
 *
 */
public class ChoiceProperty extends Property {

    private List<ChoiceAnnotation> annotations;

    public ChoiceProperty() {
        super();
        annotations = new ArrayList<ChoiceAnnotation>();
    }

    public ChoiceProperty(String name) {
        super( name );
        annotations = new ArrayList<ChoiceAnnotation>();
    }

    public ChoiceProperty(ChoiceProperty choiceProperty) {
        super( choiceProperty );
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

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

import java.util.List;


public class ChoiceProperty extends Property {

    public ChoiceProperty() {
        super();
    }

    public ChoiceProperty(String name) {
        super( name );
    }

    public ChoiceProperty(ChoiceProperty choiceProperty1) {

        // TODO Auto-generated constructor stub
    }

    public List<ChoiceAnnotation> getAnnotations() {

        // TODO Auto-generated method stub
        return null;
    }

    public void addAnnotation( ChoiceAnnotation annotation ) {

        // TODO Auto-generated method stub
        
    }
}

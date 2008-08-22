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


/**
 * @author jonalv
 *
 */
public class TextProperty extends Property {

    public TextProperty() {
        super();
    }

    public TextProperty(String name) {
        super( name );
    }
    
    public boolean hasValuesEqualTo( BaseObject obj ) {
        
        if( !super.hasValuesEqualTo(obj) ) {
            return false;
        }
        if( !(obj instanceof Property) ) {
            return false;
        }
        return true;
    }

    public List<TextAnnotation> getAnnotations() {

        return null;
    }
}

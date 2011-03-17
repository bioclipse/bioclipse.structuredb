/* *****************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistence.dao;

import org.junit.Assert;

import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;

/**
 * @author jonalv
 *
 */
public class RealNumberAnnotationDaoTest 
             extends AnnotationDaoTest<RealNumberAnnotation> {

    public RealNumberAnnotationDaoTest() {
        super( RealNumberAnnotation.class );
    }

    public void testWithRealNumberProperty() {
        RealNumberProperty property = new RealNumberProperty();
        object1.setProperty( property );
        dao.update( object1 );
        RealNumberAnnotation loaded = dao.getById( object1.getId() );
        Assert.assertTrue( object1.hasValuesEqualTo(loaded) );
        
        RealNumberAnnotation newAnnotation = new RealNumberAnnotation();
        newAnnotation.setProperty( property );
        dao.insert( newAnnotation );
        loaded = dao.getById( newAnnotation.getId() );
        Assert.assertTrue( newAnnotation.hasValuesEqualTo( loaded ) );
    }
}

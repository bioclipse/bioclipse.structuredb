/*******************************************************************************
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
package net.bioclipse.structuredb.persistency.dao;

import net.bioclipse.structuredb.domain.RealNumberAnnotation;

/**
 * @author jonalv
 *
 */
public class RealNumberAnnotationDao 
       extends AnnotationDao<RealNumberAnnotation> 
       implements IRealNumberAnnotationDao {

    public RealNumberAnnotationDao() {
        super( RealNumberAnnotation.class );
    }
    
    @Override
    public void insert(RealNumberAnnotation annotation) {
        super.insert( annotation );
        getSqlMapClientTemplate().update( "RealNumberAnnotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(RealNumberAnnotation annotation) {
        super.update( annotation );
        getSqlMapClientTemplate().update( "RealNumberAnnotation.update", 
                                          annotation );
    }    
}

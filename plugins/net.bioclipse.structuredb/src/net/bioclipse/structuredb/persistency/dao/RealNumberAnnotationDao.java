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
package net.bioclipse.structuredb.persistency.dao;

import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;

/**
 * @author jonalv
 *
 */
public class RealNumberAnnotationDao 
       extends AnnotationDao<RealNumberAnnotation> 
       implements IRealNumberAnnotationDao {

    private IRealNumberPropertyDao realNumberPropertyDao;

    public RealNumberAnnotationDao() {
        super( RealNumberAnnotation.class );
    }
    
    @Override
    public void insert(RealNumberAnnotation annotation) {
        super.insert( annotation );
        insertOrUpdateProperty( annotation.getProperty() );
        getSqlMapClientTemplate().update( "RealNumberAnnotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(RealNumberAnnotation annotation) {
        super.update( annotation );
        insertOrUpdateProperty( annotation.getProperty() );
        getSqlMapClientTemplate().update( "RealNumberAnnotation.update", 
                                          annotation );
    }
    
    private void insertOrUpdateProperty( RealNumberProperty property ) {
        RealNumberProperty loaded 
            = realNumberPropertyDao.getById( property.getId() );
        if (loaded == null) {
            realNumberPropertyDao.insert( property );
        }
        else {
            if ( !property.hasValuesEqualTo( loaded ) ) {
                realNumberPropertyDao.update( property );
            }
        }
    }

    public void setRealNumberPropertyDao( 
                  IRealNumberPropertyDao realNumberPropertyDao ) {
        this.realNumberPropertyDao = realNumberPropertyDao;
    }

    public IRealNumberPropertyDao getRealNumberPropertyDao() {
        return realNumberPropertyDao;
    }
}

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
package net.bioclipse.structuredb.persistence.dao;

import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.persistency.dao.ITextAnnotationDao;


/**
 * @author jonalv
 *
 */
public class TextAnnotationDaoTest 
             extends AnnotationDaoTest<TextAnnotation> {

    public TextAnnotationDaoTest() {
        super(TextAnnotation.class);
    }

    public void testWithTextProperty() {
        TextProperty property = new TextProperty();
        object1.setProperty( property );
        dao.update( object1 );
        TextAnnotation loaded = dao.getById( object1.getId() );
        assertTrue( object1.hasValuesEqualTo(loaded) );
        
        TextAnnotation newAnnotation = new TextAnnotation();
        newAnnotation.setProperty( property );
        dao.insert( newAnnotation );
        loaded = dao.getById( newAnnotation.getId() );
        assertTrue( newAnnotation.hasValuesEqualTo( loaded ) );
    }
    
    public void testGetAllLabels() {
        TextAnnotation a = new TextAnnotation( "label", 
                                               new TextProperty("label") );
        TextAnnotation b = new TextAnnotation( "not label",
                                               new TextProperty("not label") );
        dao.insert( a );
        dao.insert( b );
        
        ITextAnnotationDao textAnnotationDao = (ITextAnnotationDao)dao;
        assertTrue( textAnnotationDao.getAllLabels().contains( a ) );
        assertFalse( textAnnotationDao.getAllLabels().contains( b ) );
    }
    
    public void testPersistingBobbyTables() {
        TextAnnotation bobbyTables 
            = new TextAnnotation( "Robert'); DROP TABLE Students;--", 
                                  new TextProperty("label") );
        dao.insert( bobbyTables );
        ITextAnnotationDao textAnnotationDao = (ITextAnnotationDao)dao;
        assertTrue( textAnnotationDao.getAllLabels().contains( bobbyTables ) );
    }
}

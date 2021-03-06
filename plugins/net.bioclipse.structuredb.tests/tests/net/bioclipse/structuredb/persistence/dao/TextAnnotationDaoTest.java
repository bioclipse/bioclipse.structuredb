/* *****************************************************************************
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

import org.junit.Assert;

import testData.TestData;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
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
        Assert.assertTrue( object1.hasValuesEqualTo(loaded) );
        
        TextAnnotation newAnnotation = new TextAnnotation();
        newAnnotation.setProperty( property );
        dao.insert( newAnnotation );
        loaded = dao.getById( newAnnotation.getId() );
        Assert.assertTrue( newAnnotation.hasValuesEqualTo( loaded ) );
    }
    
    public void testGetAllLabels() {
        TextAnnotation a = new TextAnnotation( "label", 
                                               new TextProperty("label") );
        TextAnnotation b = new TextAnnotation( "not label",
                                               new TextProperty("not label") );
        dao.insert( a );
        dao.insert( b );
        
        ITextAnnotationDao textAnnotationDao = (ITextAnnotationDao)dao;
        Assert.assertTrue( textAnnotationDao.getAllLabels().contains( a ) );
        Assert.assertFalse( textAnnotationDao.getAllLabels().contains( b ) );
    }
    
    public void testPersistingBobbyTables() {
        TextAnnotation bobbyTables 
            = new TextAnnotation( "Robert'); DROP TABLE Students;--", 
                                  new TextProperty("label") );
        dao.insert( bobbyTables );
        ITextAnnotationDao textAnnotationDao = (ITextAnnotationDao)dao;
        Assert.assertTrue( textAnnotationDao.getAllLabels().contains( bobbyTables ) );
    }
    
    public void testForBug1798() throws Exception {
        IDBMoleculeDao dBMoleculeDao 
            = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");
    
        DBMolecule dBMolecule = new DBMolecule( "CycloOctan",
                                                TestData.getCycloOctan() );
        
        dBMoleculeDao.insert(dBMolecule);
        dBMoleculeDao.annotate( dBMolecule, object1 );
        object1.setValue( "edited" );
        dao.update(object1);
        
        TextAnnotation loaded = dao.getById( object1.getId() );
        Assert.assertEquals( 1, loaded.getDBMolecules().size() );
    }
}

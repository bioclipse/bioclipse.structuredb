/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistence.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
import net.bioclipse.structuredb.persistency.dao.ITextAnnotationDao;

import org.openscience.cdk.exception.CDKException;

import testData.TestData;

/**
 * @author jonalv
 *
 */
public class DBMoleculeDaoTest extends GenericDaoTest<DBMolecule> {

    private DBMolecule molecule1;
    private DBMolecule molecule2;
    private List<DBMolecule> dBMolecules;

    public DBMoleculeDaoTest() {
        super(DBMolecule.class);
    }
    
    @Override
    public void onSetUpInTransaction() throws Exception {
    
        super.onSetUpInTransaction();
        molecule1 = new DBMolecule( "CycloOctan",
                                    TestData
                                    .getCycloOctan() );
        molecule2 = new DBMolecule( "CycloPropan", 
                                    TestData
                                    .getCycloPropane() );
        addCreatorAndEditor(molecule1);
        addCreatorAndEditor(molecule2);
        dao.insert(molecule1);
        dao.insert(molecule2);
        
        dBMolecules = new ArrayList<DBMolecule>() {{
            add(molecule1);
            add(molecule2);
        }};
    }
    
    public void testPersistDBMoleculeWithAnnotation() {
        
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) applicationContext.getBean("textAnnotationDao");
        addCreatorAndEditor(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.addAnnotation(annotation);
        addCreatorAndEditor(dBMolecule);
        dao.insert(dBMolecule);
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        assertNotNull("The lodaded object should not be null", loaded);
        assertNotSame(dBMolecule, loaded);
        assertTrue( dBMolecule.hasValuesEqualTo(loaded) );
        assertTrue( "Should contain the annotation",
                    loaded.getAnnotations().contains(annotation) );
    }

    public void testPersistDBMoleculeWithAnnotationId() {
        
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        addCreatorAndEditor(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();

        addCreatorAndEditor(dBMolecule);
        ((IDBMoleculeDao)dao).insertWithAnnotation( dBMolecule, 
                                                    annotation.getId() );
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        assertNotNull("The lodaded object should not be null", loaded);
        assertNotSame(dBMolecule, loaded);
        assertTrue( dBMolecule.hasValuesEqualTo(loaded) );
        assertTrue( "Should contain the annotation", 
                    loaded.getAnnotations().contains(annotation) );
    }
    
    @Override
    public void testUpdate() {
        super.testUpdate();
        
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        addCreatorAndEditor(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.addAnnotation(annotation);
        addCreatorAndEditor(dBMolecule);
        dao.insert(dBMolecule);
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        dBMolecule.setName("edited");
        dao.update(dBMolecule);
        DBMolecule updated = dao.getById( dBMolecule.getId() );
        assertTrue( dBMolecule.hasValuesEqualTo(updated) );
    }
    
    public void testGetByName() throws CDKException {

        
        assertTrue( dao.getAll().containsAll(dBMolecules) );
        
        List<DBMolecule> saved = ( (IDBMoleculeDao)dao ).getByName(
                                  molecule1.getName() );
        assertTrue(  saved.contains(molecule1) );
        assertFalse( saved.contains(object1)       );
        assertFalse( saved.contains(object2)       );
        assertTrue( saved.size() == 1);
        assertTrue( saved.get(0).getFingerPrint()
                                .equals( molecule1.getFingerPrint() ) );
    }
    
    public void testAllStructureIterator() {
        List<DBMolecule> dBMolecules = new ArrayList<DBMolecule>() {
            {
                add( object1 );
                add( object2 );
                add( molecule1 );
                add( molecule2 );
            }
        };
        Iterator<DBMolecule> iterator 
            = ( (IDBMoleculeDao)dao ).allStructuresIterator();
        assertTrue( iterator.hasNext() );
        int numberof = 0;
        while( iterator.hasNext() ) {
            assertTrue( dBMolecules.contains( iterator.next() ) );
            numberof++;
        }
        assertEquals( 4, numberof );
    }
    
    public void testNumberOfStructures() {
        assertEquals( 4, ((IDBMoleculeDao)dao).numberOfStructures() );
    }
    
    public void testFingerPrintSearch() {
        
        Iterator<DBMolecule> iterator
            = ( (IDBMoleculeDao)dao ).fingerPrintSubsetSearch( 
              ((DBMolecule)molecule1).getPersistedFingerprint() );
        boolean foundObject1 = false;
        boolean foundObject2 = false;
        while( iterator.hasNext() ) {
            if( iterator.next().equals( molecule1 )) {
                foundObject1 = true;
            }
            if( iterator.next().equals( molecule2 )) {
                foundObject2 = true;
            }
        }
        assertTrue(  foundObject1 );
        assertFalse( foundObject2 );
    }
    
    public void testGetAnnotations() {
        TextAnnotation annotation       = new TextAnnotation();
        TextAnnotation unusedAnnotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        textAnnotationDao.insert( annotation );
        textAnnotationDao.insert( unusedAnnotation );
        
        molecule1.addAnnotation( annotation );
        dao.update( molecule1 );
        DBMolecule loaded = dao.getById( molecule1.getId() );
        assertEquals( 1, loaded.getAnnotations().size() );
        assertEquals( annotation, molecule1.getAnnotations().get( 0 ) );
    }
    
    public void testGetMoleculeAtIndexInParamater() throws BioclipseException {
        TextAnnotation annotation 
            = new TextAnnotation( "test", new TextProperty("label") );
        ITextAnnotationDao textAnnotationDao
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        textAnnotationDao.insert( annotation );
        IDBMoleculeDao dbMoleculeDao = (IDBMoleculeDao)dao;
        CDKManager cdk = new CDKManager();
        List<String> SMILES = Arrays.asList( new String[] { "CC", 
                                                            "CCC", 
                                                            "CCCC" } );
        for ( String s : SMILES ) {
            dbMoleculeDao
                .insertWithAnnotation( new DBMolecule( s, 
                                                       cdk.fromSMILES( s ) ), 
                                       annotation.getId() );
        }

        for ( int i = 0 ; i < SMILES.size() ; i++ ) {
            assertTrue( 
                SMILES.contains( 
                    dbMoleculeDao.getMoleculeAtIndexInLabel( annotation, i )
                                 .toSMILES(
                                 ) ) );
        }
    }

    public void testGetNumberOfMoleculesWithLabel() {
        TextAnnotation annotation 
            = new TextAnnotation( "test", new TextProperty("label") );
        ITextAnnotationDao textAnnotationDao
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        textAnnotationDao.insert( annotation );
        IDBMoleculeDao dbMoleculeDao = (IDBMoleculeDao)dao;
        assertEquals( 0, 
                      dbMoleculeDao.getNumberOfMoleculesWithAnnotation( 
                          annotation ) );
        object1.addAnnotation( annotation );
        object2.addAnnotation( annotation );
        
        IStructuredbInstanceManager manager 
            = (IStructuredbInstanceManager) 
              applicationContext.getBean("structuredbInstanceManager");
        
        manager.update( annotation );
        
        assertEquals( 2, 
                      textAnnotationDao.getById( annotation.getId() )
                                       .getDBMolecules().size() );
        
        assertEquals( 2, 
                      dbMoleculeDao.getNumberOfMoleculesWithAnnotation( 
                          annotation ) );
    }
}

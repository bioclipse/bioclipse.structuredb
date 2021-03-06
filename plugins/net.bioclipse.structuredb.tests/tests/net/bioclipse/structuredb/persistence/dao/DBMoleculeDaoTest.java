/* *****************************************************************************
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

import org.junit.Assert;
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
        
        try {
            super.onSetUpInTransaction();
        } catch (Exception e) {
            // Okey we expected this exception but we needed the things done 
            // before it was thrown to be done. A bit ugly... FIXME
        }
        molecule1 = new DBMolecule( "CycloOctan",
                                    TestData
                                    .getCycloOctan() );
        molecule2 = new DBMolecule( "CycloPropan", 
                                    TestData
                                    .getCycloPropane() );
        addAuditInformation(molecule1);
        addAuditInformation(molecule2);
        dao.insert(molecule1);
        dao.insert(molecule2);
        
        dBMolecules = new ArrayList<DBMolecule>() {{
            add(molecule1);
            add(molecule2);
        }};
        this.object1 = molecule1;
        this.object2 = molecule2;
    }
    
    public void testPersistDBMoleculeWithAnnotation() {
        
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) applicationContext.getBean("textAnnotationDao");
        addAuditInformation(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.setAtomContainer( object1.getAtomContainer() );
        dBMolecule.addAnnotation(annotation);
        addAuditInformation(dBMolecule);
        dao.insert(dBMolecule);
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        Assert.assertNotNull("The lodaded object should not be null", loaded);
        Assert.assertNotSame(dBMolecule, loaded);
        Assert.assertTrue( dBMolecule.hasValuesEqualTo(loaded) );
        Assert.assertTrue( "Should contain the annotation",
                           loaded.getAnnotations().contains(annotation) );
    }

    public void testPersistDBMoleculeWithAnnotationId() {
        
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        addAuditInformation(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.setAtomContainer( object1.getAtomContainer() );

        addAuditInformation(dBMolecule);
        ((IDBMoleculeDao)dao).insertWithAnnotation( dBMolecule, 
                                                    annotation.getId() );
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        Assert.assertNotNull("The lodaded object should not be null", loaded);
        Assert.assertNotSame(dBMolecule, loaded);
        Assert.assertTrue( dBMolecule.hasValuesEqualTo(loaded) );
        Assert.assertTrue( "Should contain the annotation", 
                           loaded.getAnnotations().contains(annotation) );
    }
    
    public void testAnnotate() {
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        addAuditInformation(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.setAtomContainer( object1.getAtomContainer() );

        addAuditInformation(dBMolecule);
        dao.insert( dBMolecule );
        
        int moleculesBefore = annotation.getDBMolecules().size();
        
        ((IDBMoleculeDao)dao).annotate( dBMolecule, annotation );
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        Assert.assertNotNull("The lodaded object should not be null", loaded);
        Assert.assertNotSame(dBMolecule, loaded);
        Assert.assertTrue( "Should contain the annotation", 
                           loaded.getAnnotations().contains(annotation) );
        TextAnnotation LoadedAnnotation 
            = textAnnotationDao.getById( annotation.getId() );
        Assert.assertEquals( LoadedAnnotation.getDBMolecules().size(), 
                             moleculesBefore + 1 );
    }
    
    @Override
    public void testUpdate() {
        super.testUpdate();
        
        TextAnnotation annotation = new TextAnnotation();
        ITextAnnotationDao textAnnotationDao 
            = (ITextAnnotationDao) 
              applicationContext.getBean("textAnnotationDao");
        addAuditInformation(annotation);
        textAnnotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.setAtomContainer( object1.getAtomContainer() );
        dBMolecule.addAnnotation(annotation);
        addAuditInformation(dBMolecule);
        dao.insert(dBMolecule);
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        dBMolecule.setName("edited");
        dao.update(dBMolecule);
        DBMolecule updated = dao.getById( dBMolecule.getId() );
        Assert.assertTrue( dBMolecule.hasValuesEqualTo(updated) );
    }
    
    public void testGetByName() throws CDKException {

    	Assert.assertTrue( dao.getAll().containsAll(dBMolecules) );
        
        DBMolecule other = new DBMolecule( "CycloPropan", 
                                           TestData.getCycloPropane() );
        addAuditInformation( other );
        dao.insert( other );
        
        List<DBMolecule> saved = ( (IDBMoleculeDao)dao ).getByName(
                                  molecule1.getName() );
        Assert.assertTrue(  saved.contains(molecule1) );
        Assert.assertFalse( saved.contains(other)     );
        Assert.assertTrue( saved.size() == 1);
        Assert.assertTrue( saved.get(0).getFingerPrint()
                                .equals( molecule1.getFingerPrint() ) );
    }
    
    public void testAllStructureIterator() {
        List<DBMolecule> dBMolecules = new ArrayList<DBMolecule>() {
            {
                add( molecule1 );
                add( molecule2 );
            }
        };
        Iterator<DBMolecule> iterator 
            = ( (IDBMoleculeDao)dao ).allStructuresIterator();
        Assert.assertTrue( iterator.hasNext() );
        int numberof = 0;
        while( iterator.hasNext() ) {
        	Assert.assertTrue( dBMolecules.contains( iterator.next() ) );
            numberof++;
        }
        Assert.assertEquals( 2, numberof );
    }
    
    public void testNumberOfStructures() {
    	Assert.assertEquals( 2, ((IDBMoleculeDao)dao).numberOfStructures() );
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
        Assert.assertTrue(  foundObject1 );
        Assert.assertFalse( foundObject2 );
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
        Assert.assertEquals( 1, loaded.getAnnotations().size() );
        Assert.assertEquals( annotation, molecule1.getAnnotations().get( 0 ) );
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
        	Assert.assertTrue( 
                SMILES.contains( 
                    dbMoleculeDao.getMoleculeAtIndexInLabel( annotation, i )
                                 .toSMILES() ) );
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
        Assert.assertEquals( 0, 
                             dbMoleculeDao.getNumberOfMoleculesWithAnnotation( 
                                 annotation ) );
        object1.addAnnotation( annotation );
        object2.addAnnotation( annotation );
        
        IStructuredbInstanceManager manager 
            = (IStructuredbInstanceManager) 
              applicationContext.getBean("structuredbInstanceManager");
        
        manager.update( annotation );
        
        Assert.assertEquals( 2, 
                             textAnnotationDao.getById( annotation.getId() )
                                              .getDBMolecules().size() );
        
        Assert.assertEquals( 2, 
                             dbMoleculeDao.getNumberOfMoleculesWithAnnotation( 
                                  annotation ) );
    }
}

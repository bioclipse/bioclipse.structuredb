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
import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;

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
    
    public void testPersistStructureWithAnnotation() {
        
        Annotation annotation = new TextAnnotation();
        IAnnotationDao annotationDao 
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        addCreatorAndEditor(annotation);
        annotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();
        dBMolecule.addAnnotation(annotation);
        addCreatorAndEditor(dBMolecule);
        dao.insert(dBMolecule);
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        assertNotNull("The lodaded object shuold not be null", loaded);
        assertNotSame(dBMolecule, loaded);
        assertTrue( dBMolecule.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getAnnotations().contains(annotation) );
    }

    public void testPersistStructureWithLabelId() {
        
        Annotation annotation = new TextAnnotation();
        IAnnotationDao annotationDao 
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        addCreatorAndEditor(annotation);
        annotationDao.insert(annotation);
        
        DBMolecule dBMolecule = new DBMolecule();

        addCreatorAndEditor(dBMolecule);
        ((IDBMoleculeDao)dao).insertWithAnnotation( dBMolecule, annotation.getId() );
        
        DBMolecule loaded = dao.getById( dBMolecule.getId() );
        assertNotNull("The lodaded object should not be null", loaded);
        assertNotSame(dBMolecule, loaded);
        assertTrue( dBMolecule.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getAnnotations().contains(annotation) );
    }
    
    @Override
    public void testUpdate() {
        super.testUpdate();
        
        Annotation annotation = new TextAnnotation();
        IAnnotationDao annotationDao 
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        addCreatorAndEditor(annotation);
        annotationDao.insert(annotation);
        
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
        Annotation annotation  = new TextAnnotation();
        Annotation unusedLabel = new TextAnnotation();
        IAnnotationDao annotationDao
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        annotationDao.insert( annotation );
        annotationDao.insert( unusedLabel );
        
        molecule1.addAnnotation( annotation );
        dao.update( molecule1 );
        DBMolecule loaded = dao.getById( molecule1.getId() );
        assertEquals( 1, loaded.getAnnotations().size() );
        assertEquals( annotation, molecule1.getAnnotations().get( 0 ) );
    }
}

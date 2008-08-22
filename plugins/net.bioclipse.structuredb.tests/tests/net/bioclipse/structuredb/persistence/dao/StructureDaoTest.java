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

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

import testData.TestData;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IStructureDao;

public class StructureDaoTest extends GenericDaoTest<Structure> {

    private Structure structure1;
    private Structure structure2;
    private List<Structure> structures;

    public StructureDaoTest() {
        super(Structure.class);
    }
    
    @Override
    public void onSetUpInTransaction() throws Exception {
    
        super.onSetUpInTransaction();
        structure1 = new Structure( "CycloOctan",
                                    TestData
                                    .getCycloOctan() );
        structure2 = new Structure( "CycloPropan", 
                                    TestData
                                    .getCycloPropane() );
        addCreatorAndEditor(structure1);
        addCreatorAndEditor(structure2);
        dao.insert(structure1);
        dao.insert(structure2);
        
        structures = new ArrayList<Structure>() {{
            add(structure1);
            add(structure2);
        }};
    }
    
    public void testPersistStructureWithLabel() {
        
        Annotation annotation = new Annotation();
        IAnnotationDao annotationDao 
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        addCreatorAndEditor(annotation);
        annotationDao.insert(annotation);
        
        Structure structure = new Structure();
        structure.addAnnotation(annotation);
        addCreatorAndEditor(structure);
        dao.insert(structure);
        
        Structure loaded = dao.getById( structure.getId() );
        assertNotNull("The lodaded object shuold not be null", loaded);
        assertNotSame(structure, loaded);
        assertTrue( structure.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getAnnotations().contains(annotation) );
    }

    public void testPersistStructureWithLabelId() {
        
        Annotation annotation = new Annotation();
        IAnnotationDao annotationDao 
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        addCreatorAndEditor(annotation);
        annotationDao.insert(annotation);
        
        Structure structure = new Structure();

        addCreatorAndEditor(structure);
        ((IStructureDao)dao).insertWithAnnotation( structure, annotation.getId() );
        
        Structure loaded = dao.getById( structure.getId() );
        assertNotNull("The lodaded object should not be null", loaded);
        assertNotSame(structure, loaded);
        assertTrue( structure.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getAnnotations().contains(annotation) );
    }
    
    @Override
    public void testUpdate() {
        super.testUpdate();
        
        Annotation annotation = new Annotation();
        IAnnotationDao annotationDao 
            = (IAnnotationDao) applicationContext.getBean("annotationDao");
        addCreatorAndEditor(annotation);
        annotationDao.insert(annotation);
        
        Structure structure = new Structure();
        structure.addAnnotation(annotation);
        addCreatorAndEditor(structure);
        dao.insert(structure);
        
        Structure loaded = dao.getById( structure.getId() );
        structure.setName("edited");
        dao.update(structure);
        Structure updated = dao.getById( structure.getId() );
        assertTrue( structure.hasValuesEqualTo(updated) );
    }
    
    @SuppressWarnings("serial")
    public void testGetByName() throws CDKException {

        
        assertTrue( dao.getAll().containsAll(structures) );
        
        List<Structure> saved = ( (IStructureDao)dao ).getByName(
                                  structure1.getName() );
        assertTrue(  saved.contains(structure1) );
        assertFalse( saved.contains(object1)       );
        assertFalse( saved.contains(object2)       );
        assertTrue( saved.size() == 1);
        assertTrue( saved.get(0).getFingerPrint()
                                .equals( structure1.getFingerPrint() ) );
    }
    
    public void testAllStructureIterator() {
        List<Structure> structures = new ArrayList<Structure>() {
            {
                add( object1 );
                add( object2 );
                add( structure1 );
                add( structure2 );
            }
        };
        Iterator<Structure> iterator 
            = ( (IStructureDao)dao ).allStructuresIterator();
        assertTrue( iterator.hasNext() );
        int numberof = 0;
        while( iterator.hasNext() ) {
            assertTrue( structures.contains( iterator.next() ) );
            numberof++;
        }
        assertEquals( 4, numberof );
    }
    
    public void testNumberOfStructures() {
        assertEquals( 4, ((IStructureDao)dao).numberOfStructures() );
    }
    
    public void testFingerPrintSearch() {
        
        Iterator<Structure> iterator
            = ( (IStructureDao)dao ).fingerPrintSubsetSearch( 
              ((Structure)structure1).getPersistedFingerprint() );
        boolean foundObject1 = false;
        boolean foundObject2 = false;
        while( iterator.hasNext() ) {
            if( iterator.next().equals( structure1 )) {
                foundObject1 = true;
            }
            if( iterator.next().equals( structure2 )) {
                foundObject2 = true;
            }
        }
        assertTrue(  foundObject1 );
        assertFalse( foundObject2 );
    }
    
    public void testGetLabels() {
        Annotation annotation = new Annotation( "my label" );
        Annotation unusedLabel = new Annotation( "I should not turn up" );
        IAnnotationDao annotationDao = (IAnnotationDao) applicationContext.getBean("annotationDao");
        annotationDao.insert( annotation );
        annotationDao.insert( unusedLabel );
        
        structure1.addAnnotation( annotation );
        dao.update( structure1 );
        Structure loaded = dao.getById( structure1.getId() );
        assertEquals( 1, loaded.getAnnotations().size() );
        assertEquals( annotation, structure1.getAnnotations().get( 0 ) );
    }
}

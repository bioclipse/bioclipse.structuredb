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
package net.bioclipse.structuredb.persistency.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

import testData.TestData;
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;

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
        
        Label label = new Label();
        ILabelDao labelDao 
            = (ILabelDao) applicationContext.getBean("labelDao");
        addCreatorAndEditor(label);
        labelDao.insert(label);
        
        Structure structure = new Structure();
        structure.addLabel(label);
        addCreatorAndEditor(structure);
        dao.insert(structure);
        
        Structure loaded = dao.getById( structure.getId() );
        assertNotNull("The lodaded object shuold not be null", loaded);
        assertNotSame(structure, loaded);
        assertTrue( structure.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getLabels().contains(label) );
    }

    public void testPersistStructureWithLabelId() {
        
        Label label = new Label();
        ILabelDao labelDao 
            = (ILabelDao) applicationContext.getBean("labelDao");
        addCreatorAndEditor(label);
        labelDao.insert(label);
        
        Structure structure = new Structure();

        addCreatorAndEditor(structure);
        ((IStructureDao)dao).insertWithLabel( structure, label.getId() );
        
        Structure loaded = dao.getById( structure.getId() );
        assertNotNull("The lodaded object should not be null", loaded);
        assertNotSame(structure, loaded);
        assertTrue( structure.hasValuesEqualTo(loaded) );
        assertTrue( loaded.getLabels().contains(label) );
    }
    
    @Override
    public void testUpdate() {
        super.testUpdate();
        
        Label label = new Label();
        ILabelDao labelDao 
            = (ILabelDao) applicationContext.getBean("labelDao");
        addCreatorAndEditor(label);
        labelDao.insert(label);
        
        Structure structure = new Structure();
        structure.addLabel(label);
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
        Label label = new Label( "my label" );
        Label unusedLabel = new Label( "I should not turn up" );
        ILabelDao labelDao = (ILabelDao) applicationContext.getBean("labelDao");
        labelDao.insert( label );
        labelDao.insert( unusedLabel );
        
        structure1.addLabel( label );
        dao.update( structure1 );
        Structure loaded = dao.getById( structure1.getId() );
        assertEquals( 1, loaded.getLabels().size() );
        assertEquals( label, structure1.getLabels().get( 0 ) );
    }
}

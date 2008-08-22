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
package net.bioclipse.structuredb.persistency.dao;

import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.Structure;

/**
 * @author jonalv
 *
 */
public class LabelDaoTest extends GenericDaoTest<Annotation> {

    public LabelDaoTest() {
        super(Annotation.class);
    }
    
    @Override
    public void testDelete() {
        
        IStructureDao structureDao 
            = (IStructureDao) applicationContext.getBean("structureDao");
        
        Structure structure = new Structure();
        structure.setCreator(testUser);
        structure.setLastEditor(testUser);
        structureDao.insert(structure);
        object1.addStructure(structure);
        dao.update(object1);
        assertTrue( structure.getAnnotations().contains( object1 ) );
        super.testDelete();
        
        structure = structureDao.getById( structure.getId() );
        assertFalse( structure.getAnnotations().contains( object1 ) );
    }
    
    public void testGetByName() {

        IStructureDao structureDao 
            = (IStructureDao) applicationContext.getBean("structureDao");

        Structure s = new Structure();
        structureDao.insert( s );
        Annotation annotation = new Annotation("label");
        addCreatorAndEditor(annotation);
        dao.insert(annotation);
        assertEquals( annotation, 
                      ( (IAnnotationDao)dao ).getByName(annotation.getName()) );
        annotation.addStructure( s );
        dao.update( annotation );
        assertEquals( 1, 
                      ( (IAnnotationDao)dao ).getByName( annotation.getName() )
                                        .getStructures().size() );
    }
    
    public void testGetStructures() {
        Structure structure       = new Structure();
        Structure unusedStructure = new Structure();
        IStructureDao structureDao = (IStructureDao) applicationContext.getBean("structureDao");
        structureDao.insert( structure );
        structureDao.insert( unusedStructure );
        
        object1.addStructure( structure );
        dao.update( object1 );
        Annotation loaded = dao.getById( object1.getId() );
        assertEquals( 1, loaded.getStructures().size() );
        assertEquals( structure, object1.getStructures().get( 0 ) );
    }
}

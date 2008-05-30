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

import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;

/**
 * @author jonalv
 *
 */
public class LabelDaoTest extends GenericDaoTest<Label> {

    public LabelDaoTest() {
        super(Label.class);
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
        assertTrue( structure.getLabels().contains( object1 ) );
        super.testDelete();
        
        structure = structureDao.getById( structure.getId() );
        assertFalse( structure.getLabels().contains( object1 ) );
    }
    
    public void testGetByName() {

        IStructureDao structureDao 
            = (IStructureDao) applicationContext.getBean("structureDao");

        Structure s = new Structure();
        structureDao.insert( s );
        Label label = new Label("label");
        addCreatorAndEditor(label);
        dao.insert(label);
        assertEquals( label, 
                      ( (ILabelDao)dao ).getByName(label.getName()) );
        label.addStructure( s );
        dao.update( label );
        assertEquals( 1, 
                      ( (ILabelDao)dao ).getByName( label.getName() )
                                        .getStructures().size() );
    }
}

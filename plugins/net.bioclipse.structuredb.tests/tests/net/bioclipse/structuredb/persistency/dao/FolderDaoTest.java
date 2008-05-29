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
public class FolderDaoTest extends GenericDaoTest<Label> {

    public FolderDaoTest() {
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
        assertNotNull(structure.getLabel());
        super.testDelete();
        
        structure = structureDao.getById( structure.getId() );
        assertNull( structure.getLabel() );
    }
    
    public void testGetByName() {
        Label label = new Label("folder");
        addCreatorAndEditor(label);
        dao.insert(label);
        assertEquals( label, 
                      ( (ILabelDao)dao ).getByName(label.getName()) );
    }
}

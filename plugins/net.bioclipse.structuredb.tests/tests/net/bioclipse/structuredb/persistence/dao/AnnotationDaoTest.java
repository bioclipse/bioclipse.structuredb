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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.openscience.cdk.exception.CDKException;

import testData.TestData;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;

/**
 * @author jonalv
 *
 */
public abstract class AnnotationDaoTest<T extends Annotation> 
                extends GenericDaoTest<T> {

    @SuppressWarnings("unchecked")
    public AnnotationDaoTest(Class c) {
        super( c );
    }

    @Override
    public void testDelete() throws Exception {
        
        IDBMoleculeDao dBMoleculeDao 
            = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");
        
        DBMolecule dBMolecule = new DBMolecule( "CycloOctan",
                                                TestData.getCycloOctan() );
        
        dBMolecule.setCreator(testUser);
        dBMolecule.setLastEditor(testUser);
        dBMoleculeDao.insert(dBMolecule);
        object1.addDBMolecule(dBMolecule);
        dao.update(object1);
        assertTrue( dBMolecule.getAnnotations().contains( object1 ) );
        super.testDelete();
        
        dBMolecule = dBMoleculeDao.getById( dBMolecule.getId() );
        assertFalse( dBMolecule.getAnnotations().contains( object1 ) );
    }
    
    public void testDeleteWithStructures() throws Exception {
        
        IDBMoleculeDao dBMoleculeDao 
            = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");
        
        DBMolecule dBMolecule = new DBMolecule( "CycloOctan",
                                                TestData.getCycloOctan() );
        
        dBMolecule.setCreator(testUser);
        dBMolecule.setLastEditor(testUser);
        dBMoleculeDao.insert(dBMolecule);
        object1.addDBMolecule(dBMolecule);
        dao.update(object1);
        assertTrue( dBMolecule.getAnnotations().contains( object1 ) );
        ( (IAnnotationDao<Annotation>)dao )
            .deleteWithStructures( object1.getId(), new NullProgressMonitor() );
        
        assertNull( dBMoleculeDao.getById( dBMolecule.getId() ) );
        assertNull( dao.getById( object1.getId() ) );
    }
    
    public void testGetDBMolecules() throws Exception {
        DBMolecule dBMolecule      = new DBMolecule( "CycloOctan",
                                                     TestData.getCycloOctan() );
        DBMolecule unusedStructure = new DBMolecule( "CycloOctan",
                                                     TestData.getCycloOctan() );
        IDBMoleculeDao dBMoleculeDao 
            = (IDBMoleculeDao) applicationContext.getBean("dBMoleculeDao");
        dBMoleculeDao.insert( dBMolecule );
        dBMoleculeDao.insert( unusedStructure );
        
        object1.addDBMolecule( dBMolecule );
        dao.update( object1 );
        Annotation loaded = dao.getById( object1.getId() );
        assertEquals( 1, loaded.getDBMolecules().size() );
        assertEquals( dBMolecule, object1.getDBMolecules().get( 0 ) );
    }
}

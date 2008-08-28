/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

/**
 * @author jonalv
 *
 */
public abstract class AbstractStructuredbInstanceManager 
                      implements IStructuredbInstanceManager {

    protected IAnnotationDao annotationDao;
    protected IDBMoleculeDao dBMoleculeDao;
    protected IUserDao       userDao;
    
    public AbstractStructuredbInstanceManager() {
        
    }

    public IAnnotationDao getLabelDao() {
        return annotationDao;
    }

    public void setAnnotationDao(IAnnotationDao annotationDao) {
        this.annotationDao = annotationDao;
    }

    public IDBMoleculeDao getDBMoleculeDao() {
        return dBMoleculeDao;
    }

    public void setDBMoleculeDao(IDBMoleculeDao dBMoleculeDao) {
        this.dBMoleculeDao = dBMoleculeDao;
    }

    public IUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }
}

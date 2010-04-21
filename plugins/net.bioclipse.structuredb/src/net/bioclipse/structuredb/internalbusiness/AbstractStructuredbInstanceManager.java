/* *****************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
import net.bioclipse.structuredb.persistency.dao.IRealNumberAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IRealNumberPropertyDao;
import net.bioclipse.structuredb.persistency.dao.ITextAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.ITextPropertyDao;

/**
 * @author jonalv
 *
 */
public abstract class AbstractStructuredbInstanceManager 
                      implements IStructuredbInstanceManager {

    protected IDBMoleculeDao           dBMoleculeDao;
    protected IRealNumberAnnotationDao realNumberAnnotationDao;
    protected ITextAnnotationDao       textAnnotationDao;
    protected IRealNumberPropertyDao   realNumberPropertyDao;
    protected ITextPropertyDao         textPropertyDao;
    
    public IRealNumberAnnotationDao getRealNumberAnnotationDao() {
        return realNumberAnnotationDao;
    }
    
    public void setRealNumberAnnotationDao(
        IRealNumberAnnotationDao realNumberAnnotationDao ) {
    
        this.realNumberAnnotationDao = realNumberAnnotationDao;
    }
    
    public ITextAnnotationDao getTextAnnotationDao() {
        return textAnnotationDao;
    }
    
    public void setTextAnnotationDao( ITextAnnotationDao textAnnotationDao ) {
        this.textAnnotationDao = textAnnotationDao;
    }
    
    public IRealNumberPropertyDao getRealNumberPropertyDao() {
        return realNumberPropertyDao;
    }
    
    public void setRealNumberPropertyDao(
        IRealNumberPropertyDao realNumberPropertyDao ) {
    
        this.realNumberPropertyDao = realNumberPropertyDao;
    }
    
    public ITextPropertyDao getTextPropertyDao() {
        return textPropertyDao;
    }
    
    public void setTextPropertyDao( ITextPropertyDao textPropertyDao ) {
        this.textPropertyDao = textPropertyDao;
    }
    
    public AbstractStructuredbInstanceManager() {
        
    }

    public IDBMoleculeDao getDBMoleculeDao() {
        return dBMoleculeDao;
    }

    public void setDBMoleculeDao(IDBMoleculeDao dBMoleculeDao) {
        this.dBMoleculeDao = dBMoleculeDao;
    }
}

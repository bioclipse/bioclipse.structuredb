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

import java.sql.SQLException;

import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;

/**
 * The labelDao persists and loads libraries
 * 
 * @author jonalv
 *
 */
public class LabelDao extends GenericDao<Label> implements ILabelDao {

    public LabelDao() {
        super(Label.class);
    }

    @Override
    public void insert(Label label) {
        getSqlMapClientTemplate().update( "BaseObject.insert", label );
        getSqlMapClientTemplate().update( "Label.insert",    label );
    }
    
    @Override
    public void update(Label label) {
        getSqlMapClientTemplate().update( "BaseObject.update", label );
        getSqlMapClientTemplate().update( "Label.update",     label );
    }

    public Label getByName(String name) {
        return (Label)getSqlMapClientTemplate()
               .queryForObject( "Label.getByName", name);
    }
}

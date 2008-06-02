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
import java.util.HashMap;
import java.util.Map;

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
        getSqlMapClientTemplate().update( "Label.update",      label );
        fixStructureLabel( label );
    }
    
    private void fixStructureLabel( final Label label ) {

        for( final Structure s : label.getStructures() ) {
            Map<String, String> params = new HashMap<String, String>() {
                {
                    put( "labelId",     label.getId()         );
                    put( "structureId", s.getId() );
                }
            };
            if ( (Integer) getSqlMapClientTemplate()
                .queryForObject( "StructureLabel.hasConnection", 
                                 params ) == 0 ) {
                getSqlMapClientTemplate().update( "StructureLabel.connect", 
                                                  params );
            }
        }
    }

    public Label getByName(String name) {
        return (Label)getSqlMapClientTemplate()
               .queryForObject( "Label.getByName", name);
    }
}

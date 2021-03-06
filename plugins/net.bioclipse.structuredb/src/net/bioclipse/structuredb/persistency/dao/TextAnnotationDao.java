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
package net.bioclipse.structuredb.persistency.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import net.bioclipse.structuredb.FileStoreKeeper;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;

/**
 * @author jonalv
 *
 */
public class TextAnnotationDao extends AnnotationDao<TextAnnotation> 
                               implements ITextAnnotationDao {

    private ITextPropertyDao textPropertyDao;

    public TextAnnotationDao() {
        super( TextAnnotation.class );
    }
    
    @Override
    public void insert(TextAnnotation annotation) {
        super.insert( annotation );
        insertOrUpdateProperty( annotation.getProperty() );
        getSqlMapClientTemplate().update( "TextAnnotation.insert",
                                          annotation );
    }
    
    @Override
    public void update(TextAnnotation annotation) {
        super.update( annotation );
        insertOrUpdateProperty( annotation.getProperty() );
        getSqlMapClientTemplate().update( "TextAnnotation.update", 
                                          annotation );
    }
    
    private void insertOrUpdateProperty( TextProperty property ) {
        if (property == null) {
            return;
        }
        TextProperty loaded 
            = getTextPropertyDao().getById( property.getId() );
        if (loaded == null) {
            getTextPropertyDao().insert( property );
        }
        else {
            if ( !property.hasValuesEqualTo( loaded ) ) {
                getTextPropertyDao().update( property );
            }
        }
    }

    public void setTextPropertyDao( ITextPropertyDao textPropertyDao ) {
        this.textPropertyDao = textPropertyDao;
    }

    public ITextPropertyDao getTextPropertyDao() {
        return textPropertyDao;
    }

    @SuppressWarnings("unchecked")
    public List<TextAnnotation> getAllLabels() {
        return getSqlMapClientTemplate()
                   .queryForList( "TextAnnotation.getAllLabels" );
    }

    @SuppressWarnings("unchecked")
    public Collection<String> 
           getAvailableProperties( TextAnnotation annotation ) {
        Collection<String> result = new LinkedList<String>();
        
        result.addAll( 
            getSqlMapClientTemplate()
                .queryForList( "TextAnnotation.getAvailableTextProperties", 
                               annotation.getId() ) );
        result.addAll( 
            getSqlMapClientTemplate()
                .queryForList( 
                    "TextAnnotation.getAvailableRealNumberProperties", 
                    annotation.getId() ) );
        return result;
    }


}

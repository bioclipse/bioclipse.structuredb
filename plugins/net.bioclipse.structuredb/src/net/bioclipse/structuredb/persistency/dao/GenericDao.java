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

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * This class is the basis for the daos. It uses generics to be able to return
 * daos without need for casting and to reduce code duplication. 
 * 
 * @author jonalv
 *
 * @param <T> the domain class the dao works with
 */
public class GenericDao<T> extends SqlMapClientDaoSupport implements IGenericDao<T>, FetchExecutor<T> {

    protected Class<T> type;
    

    /**
     * @param type the class the dao work with
     */
    public GenericDao(Class<T> type){
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.IGenericDao#delete(java.lang.String)
     */
    public void delete(String id) {
        getSqlMapClientTemplate().delete("BaseObject.delete", id);
        //The rest should be deleted by cascades in the database
    }

    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.IGenericDao#getAll()
     */
    public List<T> getAll() {
        return getSqlMapClientTemplate().queryForList( type.getSimpleName() + ".getAll" );
    }

    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.IGenericDao#getById(java.lang.String)
     */
    public T getById(String id) {
        return (T)getSqlMapClientTemplate().queryForObject( type.getSimpleName() + ".getById", id);
    }

    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.IGenericDao#save(java.lang.Object)
     */
    public void insert(T instance) {
        getSqlMapClientTemplate().update(type.getSimpleName() + ".insert", instance );
    }

    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.IGenericDao#update(java.lang.Object)
     */
    public void update(T instance) {
        getSqlMapClientTemplate().update( type.getSimpleName() + ".update", instance );
    }

    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.FetchExecutor#executeListFetch(java.lang.reflect.Method, java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public List<T> executeListFetch(Method method, Object[] queryArgs) {
        String queryName = type.getSimpleName() + "." + method.getName();
        return getSqlMapClientTemplate().queryForList(queryName, queryArgs[0]);
    }

    /* (non-Javadoc)
     * @see net.bioclipse.pcm.dao.FetchExecutor#executeObjectFetch(java.lang.reflect.Method, java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public T executeObjectFetch(Method method, Object[] queryArgs) {
        String queryName = type.getSimpleName() + "." + method.getName();
        return (T)getSqlMapClientTemplate().queryForObject(queryName, queryArgs[0]);
    }
}

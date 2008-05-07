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

/**
 * @author jonalv
 *
 * @param <T>
 */
public interface FetchExecutor<T> {

    /**
     * Execute a fetch of multiple objects with the appropriate arguments
     */
    List<T> executeListFetch( Method method, Object[] queryArgs );
    
    /**
     * Execute a fetch of a single object with the appropriate arguments
     */
    T executeObjectFetch( Method method, Object[] queryArgs );
}

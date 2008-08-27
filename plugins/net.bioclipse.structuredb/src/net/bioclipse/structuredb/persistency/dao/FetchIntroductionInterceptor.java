/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/

package net.bioclipse.structuredb.persistency.dao;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;

/**
 * @author jonalv
 */
public class FetchIntroductionInterceptor implements IntroductionInterceptor {

    public Object invoke(MethodInvocation invocation) 
                  throws Throwable {

        FetchExecutor<?> genericDao 
            = (FetchExecutor<?>) invocation.getThis();

        String methodName = invocation.getMethod().getName();
        if ( methodName.startsWith("fetchObject") ) {
            return genericDao
                   .executeObjectFetch( invocation.getMethod(), 
                                        invocation.getArguments() );
        }
        else if ( methodName.startsWith("fetchList") ) {
            return genericDao
                   .executeListFetch( invocation.getMethod(), 
                                      invocation.getArguments() );
        }
        else {
            return invocation.proceed();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean implementsInterface(Class intf) {
        return intf.isInterface() 
               && FetchExecutor.class.isAssignableFrom(intf);
    }
}
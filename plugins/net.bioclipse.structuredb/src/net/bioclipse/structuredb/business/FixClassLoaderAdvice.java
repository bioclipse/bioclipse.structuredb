/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.business;
import java.util.Arrays;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openscience.cdk.Bond;
/**
 * An advice ensuring that the current threads context class loader is 
 * correct. This is a bit of a hack to fix problems... :(
 * 
 *   And the method list is probably not complete...
 * 
 * @author jonalv
 *
 */
public class FixClassLoaderAdvice implements MethodInterceptor {
    private static final List<String> interestingMethods =
        Arrays.asList( new String[] { 
            "addStructuresFromSDF",
            "createStructure",
            } );
    public Object invoke( MethodInvocation invocation ) 
                  throws Throwable {
        if ( interestingMethods.contains( 
                 invocation.getMethod().getName() ) ) {
            ClassLoader old = Thread.currentThread()
                                    .getContextClassLoader(); 
            Thread.currentThread().setContextClassLoader(
                   Bond.class.getClassLoader() );
            Object result = invocation.proceed();
            Thread.currentThread().setContextClassLoader( old );
            return result;
        }
        return invocation.proceed();
    }
}

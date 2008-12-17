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
package net.bioclipse.structuredb.internalbusiness;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import net.bioclipse.structuredb.domain.BaseObject;
import net.bioclipse.structuredb.persistency.dao.IUserDao;
/**
 * @author jonalv
 *
 */
public class AuditCreationAdvice implements IAuditAdvice {
    private ILoggedInUserKeeper loggedInUserKeeper;
    public void before(Method method, Object[] args, Object target)
            throws Throwable {
        BaseObject baseObject = (BaseObject)args[0];
        long now = System.currentTimeMillis();
        baseObject.setCreated( new Timestamp(now) );
        baseObject.setEdited(  new Timestamp(now) );
        baseObject.setCreator(    loggedInUserKeeper.getLoggedInUser() );
        baseObject.setLastEditor( loggedInUserKeeper.getLoggedInUser() );
    }
    public void setLoggedInUserKeeper( ILoggedInUserKeeper keeper) {
        this.loggedInUserKeeper = keeper;
    }
}

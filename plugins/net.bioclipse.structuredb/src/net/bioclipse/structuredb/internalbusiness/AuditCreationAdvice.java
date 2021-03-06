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

import java.lang.reflect.Method;
import java.sql.Timestamp;

import net.bioclipse.structuredb.domain.BaseObject;

/**
 * @author jonalv
 *
 */
public class AuditCreationAdvice implements IAuditAdvice {

    public void before(Method method, Object[] args, Object target)
            throws Throwable {

        BaseObject baseObject = (BaseObject)args[0];
        long now = System.currentTimeMillis();
        baseObject.setCreated( new Timestamp(now) );
        baseObject.setEdited(  new Timestamp(now) );
    }
}

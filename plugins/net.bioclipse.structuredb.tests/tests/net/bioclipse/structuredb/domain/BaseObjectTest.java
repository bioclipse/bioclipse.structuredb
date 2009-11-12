/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.domain;

import static org.junit.Assert.*;

import org.junit.Test;


public class BaseObjectTest {

    @Test
    public void testHasValuesEqualTo() {

        BaseObject baseObject1 = new BaseObject();
        BaseObject baseObject2 = new BaseObject(baseObject1);
        
        BaseObject baseObject3 = new BaseObject();
        
        assertTrue(  baseObject1.hasValuesEqualTo(baseObject2) );
        assertFalse( baseObject1.hasValuesEqualTo(baseObject3) );
    }
}

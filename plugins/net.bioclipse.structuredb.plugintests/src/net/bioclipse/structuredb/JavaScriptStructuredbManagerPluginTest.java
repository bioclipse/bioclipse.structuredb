/* *****************************************************************************
 * Copyright (c) 2009  jonalv <jonalv@users.sourceforge.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package net.bioclipse.structuredb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class JavaScriptStructuredbManagerPluginTest
    extends AbstractStructuredbManagerPluginTest {

    @BeforeClass public static void setup() {
        structuredb = net.bioclipse.structuredb.Activator.getDefault()
                         .getJavaScriptStructuredbManager();
        cdk = net.bioclipse.cdk.business.Activator.getDefault()
                 .getJavaScriptCDKManager();
        ui = net.bioclipse.ui.business.Activator.getDefault().getJSUIManager();
    }

    @After
    public void dropDatabases() {

        structuredb.deleteDatabase( database1 );
        structuredb.deleteDatabase( database2 );
    }

    @Test
    public void testRemovingDatabaseInstance() {
        
        assertTrue( structuredb.allDatabaseNames().contains(database1) );
        
        structuredb.deleteDatabase( database1 );
        
        assertFalse( structuredb.allDatabaseNames().contains(database1) );
        
        structuredb.createDatabase( database1 ); // restore order
        assertTrue( structuredb.allDatabaseNames().contains( database1 ) );
    }

}

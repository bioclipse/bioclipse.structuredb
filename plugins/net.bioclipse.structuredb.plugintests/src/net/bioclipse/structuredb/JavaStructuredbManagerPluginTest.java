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
import net.bioclipse.jobs.BioclipseJobUpdateHook;

import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavaStructuredbManagerPluginTest
    extends AbstractStructuredbManagerPluginTest {

    @BeforeClass public static void setup() {
        structuredb = net.bioclipse.structuredb.Activator.getDefault()
            .getStructuredbManager();
        cdk = net.bioclipse.cdk.business.Activator.getDefault()
                 .getJavaCDKManager();
        ui = net.bioclipse.ui.business.Activator.getDefault().getUIManager();
    }

    @After
    public void dropDatabases() throws InterruptedException {

        Job j1 = structuredb.deleteDatabase( 
                     database1, 
                     new BioclipseJobUpdateHook<Void>( "" ) );
        Job j2 = structuredb.deleteDatabase( 
                     database2, 
                     new BioclipseJobUpdateHook<Void>( "" ) );
        j1.join();
        j2.join();
    }
    
    @Test
    public void testRemovingDatabaseInstance() throws InterruptedException {
        
        assertTrue( structuredb.allDatabaseNames().contains(database1) );
        
        Job j1 = structuredb.deleteDatabase( 
                     database1,
                     new BioclipseJobUpdateHook<Void>( "" ) );
        
        j1.join();
        
        assertFalse( structuredb.allDatabaseNames().contains(database1) );
        
        structuredb.createDatabase( database1 ); // restore order
        assertTrue( structuredb.allDatabaseNames().contains( database1 ) );
    }
}

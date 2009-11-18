/*******************************************************************************
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

import org.junit.BeforeClass;

public class JavaStructuredbManagerPluginTest
    extends AbstractStructuredbManagerPluginTest {

    @BeforeClass public static void setup() {
        structuredb = net.bioclipse.structuredb.Activator.getDefault()
            .getStructuredbManager();
        cdk = net.bioclipse.cdk.business.Activator.getDefault()
                 .getJavaCDKManager();
        ui = net.bioclipse.ui.business.Activator.getDefault().getUIManager();
    }

}

/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.inchi.business.test;

import junit.framework.Assert;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.inchi.business.IInChIManager;

import org.junit.BeforeClass;
import org.junit.Test;

public class InChIManagerPluginTest {

    private static IInChIManager inchi;
    private static ICDKManager cdk;
    
    @BeforeClass public static void setup() {
        // the next line is needed to ensure the OSGI loader properly start
        // the org.springframework.bundle.osgi.extender, so that the manager
        // can be loaded too. Otherwise, it will fail with a time out.
        net.bioclipse.ui.Activator.getDefault();

        inchi = net.bioclipse.inchi.business.Activator.getDefault().getInChIManager();
        cdk = net.bioclipse.cdk.business.Activator.getDefault().getCDKManager();
    }

    @Test public void testGenerate() throws Exception {
        IMolecule mol = cdk.fromSMILES("C");
        Assert.assertNotNull("Input structure is unexpectedly null", mol);
        String inchiStr = inchi.generate(mol);
        Assert.assertNotNull(inchiStr);
        Assert.assertEquals("InChI=1/CH4/h1H4", inchiStr);
    }

    @Test public void testGenerateNoStereo() throws Exception {
        IMolecule mol = cdk.fromSMILES("ClC(Br)(F)(O)");
        Assert.assertNotNull("Input structure is unexpectedly null", mol);
        String inchiStr = inchi.generate(mol);
        Assert.assertNotNull(inchiStr);
        Assert.assertEquals("InChI=1/CHBrClFO/c2-1(3,4)5/h5H", inchiStr);
    }

    @Test public void testGenerateKey() throws Exception {
        IMolecule mol = cdk.fromSMILES("C");
        Assert.assertNotNull("Input structure is unexpectedly null", mol);
        String key = inchi.generateKey(mol);
        Assert.assertNotNull(key);
        Assert.assertEquals("VNWKTOKETHGBQD-UHFFFAOYAM", key);
    }
}

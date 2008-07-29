/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.cdk10.sdfeditor.editor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import net.bioclipse.cdk10.business.CDK10Molecule;


public class StructureSaveHelper {

    public static List<CDK10Molecule> extractCDK10Mols(
                                                        StructureTableEntry[] entries, ArrayList<String> propHeaders ) {

        List<CDK10Molecule> mols = new ArrayList<CDK10Molecule>();
        int cnt=0;
        for (StructureTableEntry entry : entries){
            IAtomContainer ac=(IAtomContainer)entry.getMoleculeImpl();

//            System.out.println("Mol: " + entry.getIndex());
            
            //Add the properties with key->value to the AC
            Hashtable props=new Hashtable();
            int pcnt=0;
            for (String pkey : propHeaders){
                String val=String.valueOf( entry.columns[pcnt] );
//                System.out.println("    Added property: " + pkey + " -> " + val);
                props.put( pkey, val );
                pcnt++;
            }
            
            ac.setProperties( props );

            CDK10Molecule mol=new CDK10Molecule(ac);
            mols.add( mol );
            
            cnt++;
        }
        
        return mols;
    }

    
    
}

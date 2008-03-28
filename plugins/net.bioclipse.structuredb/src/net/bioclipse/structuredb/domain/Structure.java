/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.structuredb.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.BitSet;

import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * @author jonalv
 *
 */
public class Structure extends BaseObject {

    private static final Logger logger = Logger.getLogger(Structure.class);
    
	private AtomContainer molecule;
	private BitSet        fingerPrint;
	private String        fingerPrintString;  //TODO: Do the persisting of the fingerprint in a nicer way
	private String        smiles;
	
	private Library library;
	
	public Structure() {
		super();
		fingerPrint = new BitSet();
		this.fingerPrintString = makeFingerPrintString(fingerPrint);
		molecule    = new AtomContainer();
		smiles      = "";
	}
	
	public Structure( String name, AtomContainer molecule ) {
		
		super(name);
		this.molecule = molecule;
		
		Fingerprinter fingerprinter = new Fingerprinter();
		try {
			fingerPrint = fingerprinter.getFingerprint(molecule);
			fingerPrintString = makeFingerPrintString(fingerPrint);
		} catch (Exception e) {
			//If this happens often maybe something else is needed
			throw new IllegalArgumentException("could not create fingerPrint for Atomcontainer:" + name);  
		}
		
		SmilesGenerator sg = new SmilesGenerator();
		//If molecule often isn't an instance of IMolecule maybee something else is needed
		smiles = sg.createSMILES( (IMolecule) molecule );  
	}
	
	/**
	 * Creates a new Structure that is an exact copy of the 
	 * given instance including the same id.
	 * 
	 * @param structure
	 */
	public Structure(Structure structure) {
		
		super(structure);
		
		this.molecule          = structure.getMolecule();
		this.fingerPrint       = (BitSet)structure.getFingerPrint().clone();
		this.fingerPrintString = makeFingerPrintString(fingerPrint);
		this.smiles            = structure.getSmiles();
		this.library           = structure.getLibrary();
	}

	/* (non-Javadoc)
	 * @see net.bioclipse.pcm.domain.PCMBaseObject#hasValuesEqualTo(net.bioclipse.pcm.domain.PCMBaseObject)
	 */
	public boolean hasValuesEqualTo( BaseObject object ) {
		
   		if( !super.hasValuesEqualTo(object) ) {
			return false;
		}
		if( !(object instanceof Structure) ) {
			return false;
		}
		
		Structure structure = (Structure)object;
		
		return fingerPrint.equals( structure.getFingerPrint() )
		       &&   smiles.equals( structure.getSmiles()      );
//		       && molecule.equals( structure.getMolecule()    ); //TODO: can give false positives without?
	}
	
	/**
	 * @return the CDK AtomContainer
	 */
	public AtomContainer getMolecule() {
		return molecule;
	}

	/**
	 * @param molecule the CDK molecule to set
	 */
	public void setMolecule(AtomContainer molecule) {
		this.molecule = molecule;
	}

	/**
	 * @return the structure's fingerprint
	 */
	public BitSet getFingerPrint() {
		return fingerPrint;
	}

	/**
	 * @param fingerPrint the fingerprint to set
	 */
	public void setFingerPrint(BitSet fingerPrint) {
		this.fingerPrint = fingerPrint;
		this.fingerPrintString = makeFingerPrintString(fingerPrint);
	}

	private String makeFingerPrintString(BitSet fingerPrint) {
		StringBuilder s = new StringBuilder();
		for( int i = 0 ; i < fingerPrint.size() ; i++) {
			s.append( fingerPrint.get(i) ? 1 : 0 );
		}
		return s.toString();
	}
	
	private BitSet makeFingerPrint(String fingerPrintString) {
		BitSet fingerPrint = new BitSet( fingerPrintString.length() );
		for(int i = 0 ; i < fingerPrintString.length() ; i++) {
			fingerPrint.set( i, fingerPrintString.charAt(i) == '1' ? true : false );
		}
		return fingerPrint;
	}

	/**
	 * @return the structure's smiles representation
	 */
	public String getSmiles() {
		return smiles;
	}

	/**
	 * @param smiles the smiles representation to set
	 */
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}

	/**
	 * @return the library containing this structure
	 */
	public Library getLibrary() {
		return library;
	}

	/**
	 * Places this structure in a library
	 * 
	 * @param library the library to place the structure in
	 */
	public void setLibrary(Library library) {
		
		Library oldLibrary = this.library;
		this.library = library;
		
		if( oldLibrary != null && oldLibrary != library ) {
			oldLibrary.removeStructure(this);
		}
		
		if( library != null && !library.getStructures().contains(this) ) {
			library.addStructure( this );
		}
	}

	public String getFingerPrintString() {
		return fingerPrintString;
	}

	public void setFingerPrintString(String fingerPrintString) {
		this.fingerPrintString = fingerPrintString;
		this.fingerPrint       = makeFingerPrint(fingerPrintString);
	}
	
	public String getMoleculeCML() {
		StringWriter stringWriter = new StringWriter();
		CMLWriter cmlWriter       = new CMLWriter(stringWriter);
		try {
			cmlWriter.write(molecule);
		} catch (CDKException e) {
			// TODO Auto-generated catch block
		    LogUtils.debugTrace(logger, e);
		}
		return stringWriter.toString();
	}
	
	public void setMoleculeCML(String cml) {
		InputStream inputStream = new ByteArrayInputStream(cml.getBytes());
		CMLReader cmlReader = new CMLReader(inputStream);
		try {
			IChemFile readFile = (IChemFile)cmlReader.read( new ChemFile() );
			molecule = (AtomContainer)ChemFileManipulator.getAllAtomContainers(readFile).get(0);
		} catch (CDKException e) {
			throw new RuntimeException("failed to read molecule", e);
		}
	}
}

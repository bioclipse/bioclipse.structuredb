/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/

package net.bioclipse.cdk.domain;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioObject;
import net.bioclipse.core.domain.IMolecule;

/**
 * The CKMolecule wraps an IAtomContainer and is able to cache SMILES
 * @author ola
 *
 */
public class CDKMolecule extends BioObject implements IMolecule{

	private String name;
	private IAtomContainer atomContainer;
	private String cachedSMILES;

	public CDKMolecule(IAtomContainer atomContainer) {
		super();
		this.atomContainer=atomContainer;
	}

	public Object getParsedResource() {
		return atomContainer;
	}

	public String getSMILES() throws BioclipseException {

		//TODO: wrap in job?

		if (cachedSMILES != null) {
			return cachedSMILES;
		}

		if (getAtomContainer() == null)
			throw new BioclipseException("Unable to calculate SMILES: Molecule is empty");

		if (!(getAtomContainer() instanceof org.openscience.cdk.interfaces.IMolecule))
			throw new BioclipseException("Unable to calculate SMILES: Not a molecule.");

		if (getAtomContainer().getAtomCount() > 100)
			throw new BioclipseException("Unable to calculate SMILES: Molecule has more than 100 atoms.");

		if (getAtomContainer().getBondCount() == 0)
			throw new BioclipseException("Unable to calculate SMILES: Molecule has no bonds.");

		org.openscience.cdk.interfaces.IMolecule molecule=(org.openscience.cdk.interfaces.IMolecule)getAtomContainer();

		// Create the SMILES
		SmilesGenerator generator = new SmilesGenerator();
		cachedSMILES = generator.createSMILES(molecule);

		return cachedSMILES;
	}

	public IAtomContainer getAtomContainer() {
		return atomContainer;
	}

	public void setAtomContainer(IAtomContainer atomContainer) {
		this.atomContainer = atomContainer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCachedSMILES() {
		return cachedSMILES;
	}

	public void setCachedSMILES(String cachedSMILES) {
		this.cachedSMILES = cachedSMILES;
	}

}

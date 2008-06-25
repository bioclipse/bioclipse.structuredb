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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.util.LogUtils;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
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
public class Structure extends BaseObject
                       implements net.bioclipse.core.domain.IMolecule {

    private static final Logger logger = Logger.getLogger(Structure.class);

    private IAtomContainer atomContainer;
    private BitSet         fingerPrint;
    private byte[]         persistedFingerPrint;
    private String         smiles;
    private List<Label>    labels;

    public Structure() {
        super();
        fingerPrint = new BitSet();
        this.persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        atomContainer = new AtomContainer();
        smiles = "";
        labels = new ArrayList<Label>();
    }

    public Structure( String name, AtomContainer molecule ) {

        super(name);
        this.atomContainer = molecule;

        Fingerprinter fingerprinter = new Fingerprinter();
        try {
            fingerPrint = fingerprinter.getFingerprint(molecule);
            persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        } catch (Exception e) {
            //If this happens often maybe something else is needed
            throw new IllegalArgumentException(
                    "could not create fingerPrint for Atomcontainer:" + name);
        }

        SmilesGenerator sg = new SmilesGenerator();
        //If atomContainer often isn't an instance of IMolecule
        //maybe something else is needed
        if(molecule.getAtomCount() > 100) {
            smiles = "";
            logger.debug( "Not generating SMILES. Structure " + name + " has too many atoms." );
        }
        else {
            smiles = sg.createSMILES( (IMolecule) molecule );
        }
        labels = new ArrayList<Label>();
    }

    public Structure( String name, ICDKMolecule cdkMolecule )
           throws BioclipseException {

        super(name);
        fingerPrint          = cdkMolecule.getFingerprint(false);
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        atomContainer        = cdkMolecule.getAtomContainer();
        smiles               = cdkMolecule.getSmiles();
        labels               = new ArrayList<Label>();
    }

    /**
     * Creates a new Structure that is an exact copy of the
     * given instance including the same id.
     *
     * @param structure
     */
    public Structure(Structure structure) {

        super(structure);

        this.atomContainer   = structure.getMolecule();
        this.fingerPrint     = (BitSet)structure.getFingerPrint().clone();
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        this.smiles          = structure.getSmiles();
        this.labels          = new ArrayList<Label>( structure.getLabels() );
    }

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
//             && atomContainer.equals( structure.getMolecule()    ); //TODO: can give false positives without?
    }

    /**
     * @return the CDK AtomContainer
     */
    public IAtomContainer getMolecule() {
        return atomContainer;
    }

    /**
     * @param atomContainer the CDK atomContainer to set
     */
    public void setMolecule(IAtomContainer molecule) {
        this.atomContainer = molecule;
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
        this.fingerPrint     = fingerPrint;
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
    }

    private byte[] makePersistedFingerPrint(BitSet fingerPrint) {
        byte[] persistedFingerPrint = new byte[fingerPrint.size()];
        for( int i = 0 ; i < fingerPrint.size() ; i++) {
            persistedFingerPrint[i] = (byte) (fingerPrint.get(i) ? 1 : 0) ;
        }
        return persistedFingerPrint;
    }

    private BitSet makeFingerPrint(byte[] persistedFingerPrint) {
        BitSet fingerPrint = new BitSet( persistedFingerPrint.length );
        for(int i = 0 ; i < persistedFingerPrint.length ; i++) {
            fingerPrint.set( i, persistedFingerPrint[i] == 1 ? true
                                                             : false );
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
     * @return the label containing this structure or null
     * if the structure isn't in any label
     */
    public List<Label> getLabels() {
        return labels;
    }

    /**
     * Adds a label to this structure
     *
     * @param label the label to place the structure in
     */
    public void addLabel(Label label) {
        labels.add( label );
        if( label != null && !label.getStructures().contains(this) ) {
            label.addStructure( this );
        }
    }
    
    void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public byte[] getPersistedFingerprint() {
        return persistedFingerPrint;
    }

    public void setPersistedFingerprint(byte[] persistedFingerPrint) {
        this.persistedFingerPrint = persistedFingerPrint;
        this.fingerPrint = makeFingerPrint(persistedFingerPrint);
    }

    public String getCML() {
        StringWriter stringWriter = new StringWriter();
        CMLWriter cmlWriter       = new CMLWriter(stringWriter);
        try {
            cmlWriter.write(atomContainer);
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            LogUtils.debugTrace(logger, e);
        }
        return stringWriter.toString();
    }

    /**
     * Sets the given cml. Does not update other fields like for example smiles
     * 
     * @param cml
     */
    public void setCML(String cml) {
        InputStream inputStream = new ByteArrayInputStream(cml.getBytes());
        CMLReader cmlReader = new CMLReader(inputStream);
        try {
            IChemFile readFile = (IChemFile)cmlReader.read( new ChemFile() );
            atomContainer = (AtomContainer)ChemFileManipulator
                                      .getAllAtomContainers(readFile).get(0);
        } 
        catch (CDKException e) {
            throw new RuntimeException("failed to read atomContainer", e);
        }
    }

    public IResource getResource() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUID() {
        return getId();
    }

    public Object getAdapter(Class adapter) {
        return null;
    }
    
    public IAtomContainer getAtomContainer() {
        return atomContainer;
    }

    public void removeLabel( Label label ) {
        if ( label.getStructures().contains(this) ) {
            label.removeStructure(this);
        }
        labels.remove( label );
    }

    public List<net.bioclipse.core.domain.IMolecule> getConformers() {
        List<net.bioclipse.core.domain.IMolecule> result 
            = new BioList<net.bioclipse.core.domain.IMolecule>();
        result.add( this );
        return result;
    }
}

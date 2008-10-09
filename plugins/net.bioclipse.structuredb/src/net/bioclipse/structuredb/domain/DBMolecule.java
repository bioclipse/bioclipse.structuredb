/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *
 *******************************************************************************/

package net.bioclipse.structuredb.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
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
public class DBMolecule extends BaseObject
                        implements ICDKMolecule {

    private static final Logger logger 
        = Logger.getLogger(DBMolecule.class);

    private IAtomContainer   atomContainer;
    private BitSet           fingerPrint;
    private byte[]           persistedFingerPrint;
    private String           smiles;
    private List<Annotation> annotations;
    private String           name;

    public DBMolecule() {
        super();
        fingerPrint = new BitSet();
        this.persistedFingerPrint 
            = makePersistedFingerPrint(fingerPrint);
        atomContainer = new AtomContainer();
        smiles = "";
        annotations = new ArrayList<Annotation>();
        name = "DBMolecule" + getId();
    }

    public DBMolecule( String name, AtomContainer molecule ) {
        super();
        this.atomContainer = molecule;
        this.name = name;

        Fingerprinter fingerprinter = new Fingerprinter();
        try {
            fingerPrint = fingerprinter.getFingerprint(molecule);
            persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        } catch (Exception e) {
            //If this happens often maybe something else is needed
            throw new IllegalArgumentException(
                    "could not create fingerPrint for Atomcontainer:" 
                    + name );
        }

        SmilesGenerator sg = new SmilesGenerator();
        //If atomContainer often isn't an instance of IMolecule
        //maybe something else is needed
        if(molecule.getAtomCount() > 100) {
            smiles = "";
            logger.debug( "Not generating SMILES. " +
            		      "DBMolecule " + name + " has too many atoms." );
        }
        else {
            smiles = sg.createSMILES( (IMolecule) molecule );
        }
        annotations = new ArrayList<Annotation>();
    }

    public DBMolecule( String name, ICDKMolecule cdkMolecule )
           throws BioclipseException {

        super();
        this.name            = name;
        fingerPrint          = cdkMolecule.getFingerprint(false);
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        atomContainer        = cdkMolecule.getAtomContainer();
        try {
            smiles = cdkMolecule.getSmiles();
        }
        catch (BioclipseException e) {
            smiles = "";
        }
        
        annotations = new ArrayList<Annotation>();
    }

    /**
     * Creates a new DBMolecule that is an exact copy of the
     * given instance including the same id.
     *
     * @param dBMolecule
     */
    public DBMolecule(DBMolecule dBMolecule) {

        super(dBMolecule);

        atomContainer        = dBMolecule.getMolecule();
        fingerPrint          = (BitSet)dBMolecule.getFingerPrint()
                                                .clone();
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        smiles               = dBMolecule.getSmiles();
        annotations          = new ArrayList<Annotation>( 
                                   dBMolecule.getAnnotations() );
    }

    public boolean hasValuesEqualTo( BaseObject object ) {

        if( !super.hasValuesEqualTo(object) ) {
            return false;
        }
        if( !(object instanceof DBMolecule) ) {
            return false;
        }

        DBMolecule dBMolecule = (DBMolecule)object;

        return fingerPrint.equals( dBMolecule.getFingerPrint() )
               &&   smiles.equals( dBMolecule.getSmiles()      );
// TODO: can give false positives without?
//             && atomContainer.equals( structure.getMolecule()    ); 
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
            persistedFingerPrint[i] = (byte) (fingerPrint.get(i) ? 1 
                                                                 : 0) ;
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
     * @return the annotation containing this structure or null
     * if the structure isn't in any annotation
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Adds an Annotation to this structure
     *
     * @param annotation the annotation to place the structure in
     */
    public void addAnnotation(Annotation annotation) {
        annotations.add( annotation );
        if( annotation != null && 
            !annotation.getDBMolecules().contains(this) ) {
            
            annotation.addDBMolecule( this );
        }
    }
    
    /*
     * used by Spring to inject the annotations
     */
    void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.debugTrace(logger, e);
        }
        return stringWriter.toString();
    }

    /**
     * Sets the given cml. Does not update other fields like for example 
     * smiles
     * 
     * @param cml
     */
    public void setCML(String cml) {
        InputStream inputStream 
            = new ByteArrayInputStream(cml.getBytes());
        CMLReader cmlReader = new CMLReader(inputStream);
        try {
            IChemFile readFile 
                = (IChemFile)cmlReader.read( new ChemFile() );
            atomContainer = (AtomContainer)ChemFileManipulator
                                .getAllAtomContainers(readFile).get(0);
        } 
        catch (CDKException e) {
            throw new RuntimeException( "failed to read atomContainer", 
                                        e);
        }
    }

    public IResource getResource() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getUID() {
        return getId();
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        return super.getAdapter(adapter );
    }
    
    public IAtomContainer getAtomContainer() {
        return atomContainer;
    }

    public void removeAnnotation( Annotation annotation ) {
        if ( annotation.getDBMolecules().contains(this) ) {
            annotation.removeDBMolecule(this);
        }
        annotations.remove( annotation );
    }

    public List<net.bioclipse.core.domain.IMolecule> getConformers() {
        List<net.bioclipse.core.domain.IMolecule> result 
            = new BioList<net.bioclipse.core.domain.IMolecule>();
        result.add( this );
        return result;
    }

    public BitSet getFingerprint( boolean force ) 
                  throws BioclipseException {

        if (force) {
            Fingerprinter fp = new Fingerprinter();
            try {
                fingerPrint = fp.getFingerprint( getAtomContainer() );
            } 
            catch (Exception e) {
                throw new BioclipseException(
                    "Could not create fingerprint: " + e.getMessage() );
            }
        }
        return fingerPrint;
    }
    
    public String getName() {
        
        return name;
    }
    
    public void setName( String name ) {
    
        this.name = name;
    }
}

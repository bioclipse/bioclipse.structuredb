/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/

package net.bioclipse.structuredb.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.RecordableList;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.inchi.InChI;
import net.bioclipse.inchi.business.IInChIManager;
import net.bioclipse.structuredb.FileStoreKeeper;
import net.sf.jniinchi.INCHI_RET;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.OperationCanceledException;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
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
    private static InChIGeneratorFactory factory;

    private IAtomContainer   atomContainer;
    private BitSet           fingerPrint;
    private byte[]           persistedFingerPrint;
    private String           smiles;
    private List<Annotation> annotations;
    private String           name;
    private UUID             fileStoreKey;
    private InChI            cachedInchi;

    public DBMolecule() {
        super();
        fingerPrint = new BitSet();
        this.persistedFingerPrint 
            = makePersistedFingerPrint(fingerPrint);
        smiles = "";
        annotations = new ArrayList<Annotation>();
        name = "DBMolecule" + getId();
    }

    public DBMolecule( String name, AtomContainer molecule ) {
        super();
        this.setAtomContainer( molecule );
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
        smiles = "";
        annotations = new ArrayList<Annotation>();
    }

    public DBMolecule( String name, ICDKMolecule cdkMolecule )
           throws BioclipseException {

        super();
        this.name = name;
        try {
            fingerPrint = cdkMolecule.getFingerprint(
                net.bioclipse.core.domain.IMolecule
                    .Property.USE_CACHED_OR_CALCULATED
            );
        }
        catch ( BioclipseException e ) {
            logger.error( "Could not create fingerprint for molecule", e );
        }
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        setAtomContainer( cdkMolecule.getAtomContainer() );
        smiles = "";
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

        setAtomContainer( dBMolecule.getMolecule() );
        fingerPrint          = (BitSet)dBMolecule.getFingerPrint()
                                                .clone();
        persistedFingerPrint = makePersistedFingerPrint(fingerPrint);
        smiles               = dBMolecule.toSMILES(
        );
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

        try {
            return fingerPrint.equals( dBMolecule.getFingerPrint() )
                   && getInChIKey( Property.USE_CALCULATED ).equals( 
                          dBMolecule.getInChIKey( Property.USE_CALCULATED ) );
        }
        catch ( BioclipseException e ) {
            throw new RuntimeException(e);
        } 
    }

    /**
     * @return the CDK AtomContainer
     */
    public IAtomContainer getMolecule() {
        return getAtomContainer();
    }

    /**
     * @param atomContainer the CDK atomContainer to set
     */
    public void setMolecule(IAtomContainer molecule) {
        this.setAtomContainer( molecule );
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
        if ( fingerPrint == null ) {
            return new byte[0];
        }
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

    public String toSMILES() {
        if ( "".equals( smiles ) && atomContainer instanceof IMolecule ) {
            if (atomContainer.getAtomCount() < 100) {
                SmilesGenerator sg = new SmilesGenerator();
                smiles = sg.createSMILES( (IMolecule)atomContainer );
            }
            else {
                logger.warn( "Not generating SMILES. " +
                             "DBMolecule " + name + " has too many atoms." );
            }
        }
        return smiles;
    }
    
    String getSMILES() {
        return toSMILES();
    }
    
    String getCML() {
        return toCML();
    }

    /**
     * @param smiles the smiles representation to set
     */
    public void setSMILES(String smiles) {
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

    public String toCML() {
        StringWriter stringWriter = new StringWriter();
        CMLWriter cmlWriter       = new CMLWriter(stringWriter);
        try {
            cmlWriter.write(getAtomContainer());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new IllegalStateException("Could not create CML", e);
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
            setAtomContainer( (AtomContainer)ChemFileManipulator
                                .getAllAtomContainers(readFile).get(0) );
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
        if ( atomContainer == null ) {
            InputStream in = FileStoreKeeper.FILE_STORE
                                            .retrieve( fileStoreKey );
            CMLReader cmlReader = new CMLReader(in);
            try {
                IChemFile readFile 
                    = (IChemFile)cmlReader.read( new ChemFile() );
                setAtomContainer( (AtomContainer)ChemFileManipulator
                                    .getAllAtomContainers(readFile).get(0) );
            } 
            catch (CDKException e) {
                throw new RuntimeException( "failed to read atomContainer", 
                                            e);
            }
        }
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
            = new RecordableList<net.bioclipse.core.domain.IMolecule>();
        result.add( this );
        return result;
    }

    public BitSet getFingerprint(net.bioclipse.core.domain.IMolecule.Property 
            urgency) 
                  throws BioclipseException {
        if (urgency == net.bioclipse.core.domain.IMolecule.Property.USE_CACHED)
            return fingerPrint;
        
        if (urgency == net.bioclipse.core.domain.IMolecule.Property.USE_CALCULATED) {
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

    public String getInChI( net.bioclipse.core.domain.IMolecule.Property 
                            urgency  ) throws BioclipseException {
        switch ( urgency ) {
            case USE_CACHED:
                return cachedInchi == null ? "" : cachedInchi.getValue();
            case USE_CACHED_OR_CALCULATED:
                if (cachedInchi != null) {
                    return cachedInchi.getValue();
                }
            case USE_CALCULATED:
                calculateInchi();
                return cachedInchi.getValue();
            default:
                throw new IllegalArgumentException(
                              "Unrecognized Property:" + urgency );
        }
    }

    /**
     * @throws BioclipseException 
     * 
     */
    private void calculateInchi() throws BioclipseException {
        try {
            IAtomContainer clone = (IAtomContainer)getAtomContainer().clone();
            // remove aromaticity flags
            for (IAtom atom : clone.atoms())
                atom.setFlag(CDKConstants.ISAROMATIC, false);
            for (IBond bond : clone.bonds())
                bond.setFlag(CDKConstants.ISAROMATIC, false);
            if ( factory == null ) {
                factory = new InChIGeneratorFactory();
            }
            InChIGenerator gen = factory.getInChIGenerator(clone);
            INCHI_RET status = gen.getReturnStatus();
            if ( status == INCHI_RET.OKAY ||
                 status == INCHI_RET.WARNING ) {
                InChI inchi = new InChI();
                inchi.setValue( gen.getInchi()  );
                inchi.setKey( gen.getInchiKey() );
                cachedInchi = inchi;
            } else {
                throw new InvalidParameterException(
                    "Error while generating InChI (" + status + "): " +
                    gen.getMessage()
                );
            }
        } catch (Exception e) {
            throw new BioclipseException("Could not create InChI: "
                                         + e.getMessage(), e);
        }
    }

    public String getInChIKey( net.bioclipse.core.domain.IMolecule.Property 
                               urgency ) throws BioclipseException {
        switch ( urgency ) {
            case USE_CACHED:
                return cachedInchi == null ? "" : cachedInchi.getKey();
            case USE_CACHED_OR_CALCULATED:
                if (cachedInchi != null) {
                    return cachedInchi.getKey();
                }
            case USE_CALCULATED:
                calculateInchi();
                return cachedInchi.getKey();
            default:
                throw new IllegalArgumentException(
                              "Unrecognized Property:" + urgency );
        }
    }

    public Object getProperty( String propertyKey, Property urgency ) {
        for ( Annotation a : this.annotations ) {
            if ( a.getProperty().getName().equals( propertyKey ) ) {
                return a.getValue();
            }
        }
        return null;
    }
    
    public String toString() {
        if ( getName() != null && !getName().equals( "\"\"" ) )
            return getClass().getSimpleName() + ":" + getName();
        if (this.getAtomContainer().getAtomCount() == 0) {
            return getClass().getSimpleName() + ": no atoms";
        }
        if (Activator.getDefault() == null)
            return getClass().getSimpleName() + ":" + hashCode();

        return getClass().getSimpleName() + ":" 
               + Activator.getDefault().getJavaCDKManager()
                                       .molecularFormula(this);
    }

    /* (non-Javadoc)
     * @see net.bioclipse.cdk.domain.ICDKMolecule#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty( String propertyKey, Object value ) {

        throw new UnsupportedOperationException();
    }

    public void setFileStoreKey( String fileStoreKey ) {

        this.fileStoreKey = UUID.fromString( fileStoreKey );
    }

    public String getFileStoreKey() {

        return fileStoreKey.toString();
    }
    
    public void setFileStoreKey( UUID fileStoreKey ) {
        this.fileStoreKey = fileStoreKey;
    }

    public void setAtomContainer( IAtomContainer atomContainer ) {
        this.atomContainer = atomContainer;
    }
}

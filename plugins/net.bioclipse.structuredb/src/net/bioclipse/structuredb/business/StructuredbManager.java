/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *
 *******************************************************************************/
package net.bioclipse.structuredb.business;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.business.IDatabaseListener.DatabaseUpdateType;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.Property;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;
import net.bioclipse.structuredb.persistency.dao.IUserDao;
import net.bioclipse.structuredb.persistency.tables.TableCreator;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author jonalv
 */
public class StructuredbManager implements IStructuredbManager {

    private Logger logger = Logger.getLogger(StructuredbManager.class);

    private static final Pattern databaseNamePattern 
        = Pattern.compile( "(.*?)\\.sdb.*" );
    
    /*
     * This isn't super if cdk starts using AOP for fancy stuff in the 
     * future but we don't want the recorded variant and this is a 
     * solution that is reasonably easy to test. Should cdk start using 
     * fancy stuff real integration testing running the OSGI layer is 
     * needed and this instance would need to come from the OSGI service 
     * container
     */
    private ICDKManager cdk = new CDKManager();

    //Package protected for testing purposes
    Map<String, IStructuredbInstanceManager> internalManagers
        = new HashMap<String, IStructuredbInstanceManager>();

    //Package protected for testing purposes
    Map<String, ApplicationContext> applicationContexts
        = new HashMap<String, ApplicationContext>();

    private Collection<IDatabaseListener> listeners 
        = new HashSet<IDatabaseListener>();;

    public StructuredbManager() {
        File[] files = HsqldbUtil.getInstance()
                                 .getDatabaseFilesDirectory()
                                 .listFiles();
        if ( files != null) {
            for ( File file : files ) {
                if ( file.getName().contains( ".sdb" ) ) {
                    loadDatabase(file);
                }
            }
        }
    }
    
    private void loadDatabase( File file ) {
        
        Matcher m = databaseNamePattern.matcher( file.getName() );
        if ( m.matches() ) {
            String name = m.group( 1 );
            if ( internalManagers.containsKey( name ) ) {
                return;
            }
            ApplicationContext context 
                = createApplicationcontext( name, true );
            
            applicationContexts.put( name, context );
            internalManagers
                .put( name, (IStructuredbInstanceManager) context
                            .getBean("structuredbInstanceManager") );
            LoggedInUserKeeper keeper 
                = (LoggedInUserKeeper)context.getBean( "loggedInUserKeeper" );
            IUserDao userDao = (IUserDao) context.getBean( "userDao" );
            keeper.setLoggedInUser( userDao.getByUserName( "local" ) );
        }
    }

    public void createDatabase(String databaseName)
                throws IllegalArgumentException {

        if ( internalManagers.containsKey(databaseName) ) {
            throw new IllegalArgumentException( "Database name " +
                                                "already used: "
                                                + databaseName );
        }
        TableCreator.INSTANCE.createTables(
            HsqldbUtil.getInstance()
                      .getConnectionUrl(databaseName + ".sdb") );

        Map<String, IStructuredbInstanceManager> newInstances
            = new HashMap<String, IStructuredbInstanceManager>();

        Map<String, ApplicationContext> newApplicationContexts
            = new HashMap<String, ApplicationContext>();

        for( String nameKey : internalManagers.keySet() ) {
            //TODO: The day we not only handle local databases the row 
            //      here below will need to change
            newApplicationContexts.put( nameKey,
                                        createApplicationcontext( nameKey, 
                                                                  true) );
            newInstances.put( nameKey,
                              (IStructuredbInstanceManager)
                              newApplicationContexts
                                  .get( nameKey )
                                  .getBean("structuredbInstanceManager") );
        }

        internalManagers    = newInstances;
        applicationContexts = newApplicationContexts;

        applicationContexts.put( databaseName,
                                 createApplicationcontext( databaseName, 
                                                           true) );
        internalManagers.put(
                databaseName,
                (IStructuredbInstanceManager)
                applicationContexts.get( databaseName)
                                   .getBean("structuredbInstanceManager") );
        createLocalUser( applicationContexts.get(databaseName) );
        logger.info( "A new local instance of Structuredb named"
                      + databaseName + " has been created" );
        fireDatabasesChanged();
    }

    /**
     * Creates the standard user for a local database.
     * 
     * @param context
     */
    private void createLocalUser(ApplicationContext context) {
        IUserDao userDao = (IUserDao) context.getBean("userDao");
        User localUser = new User("local", "", true );
        localUser.setCreator(localUser);
        Timestamp now = new Timestamp( System.currentTimeMillis() );
        localUser.setCreated(now);
        localUser.setEdited(now);
        userDao.insert(localUser);
        LoggedInUserKeeper keeper = (LoggedInUserKeeper)
                                     context
                                     .getBean( "loggedInUserKeeper" );
        keeper.setLoggedInUser( localUser );
    }

    private ApplicationContext createApplicationcontext( String databaseName, 
                                                         boolean local ) {
        
        FileSystemXmlApplicationContext context
            = new FileSystemXmlApplicationContext(
                Structuredb.class
                           .getClassLoader()
                           .getResource("applicationContext.xml")
                           .toString() );

        BasicDataSource dataSource
            = (BasicDataSource) context.getBean("dataSource");

        if (local) {
            dataSource.setUrl(
                    HsqldbUtil.getInstance()
                              .getConnectionUrl(databaseName + ".sdb") );
        }
        else {
            throw new UnsupportedOperationException(
                    "non-local databases not " +
                    "supported in this version" );
        }
        return context;
    }

    /**
     * Throw an exception if no such database.
     * 
     * @param databaseName
     * 
     * @throws IllegalArgumentException if no such database
     */
    private void checkDatabaseName(String databaseName) {
        if ( !internalManagers.containsKey(databaseName) ) {
            throw new IllegalArgumentException(
                "There is no database " + "named: '" + databaseName 
                + "'.\n" + "Use `" + getNamespace() 
                + ".allDatabaseNames` to show all available names." );
        }
    }

    public DBMolecule createMolecule( String databaseName,
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule)
                                      throws BioclipseException {

        checkDatabaseName(databaseName);
        DBMolecule m = new DBMolecule( moleculeName, cdkMolecule );
        internalManagers.get(databaseName).insertMolecule(m);
        logger.debug( "DBMolecule " + moleculeName
                      + " inserted in " + databaseName );
        return m;
    }

    public User createUser( String databaseName,
                            String username,
                            String password,
                            boolean sudoer) 
                throws IllegalArgumentException {

        checkDatabaseName(databaseName);
        User user = new User(username, password, sudoer);
        internalManagers.get(databaseName).insertUser(user);
        return user;
    }

    public void deleteDatabase(String databaseName) {
        checkDatabaseName(databaseName);
        internalManagers.remove( databaseName );
        applicationContexts.remove( databaseName );
        HsqldbUtil.getInstance().remove( databaseName + ".sdb" );
        fireDatabasesChanged();
    }

    public List<Annotation> allAnnotations(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllAnnotations();
    }

    public List<DBMolecule> allMolecules(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllMolecules();
    }

    public List<User> allUsers(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllUsers();
    }

    public List<DBMolecule> allMoleculesByName( String databaseName,
                                                String structureName ) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName)
                               .retrieveStructureByName(structureName);
    }

    public User userByName(String databaseName, String username) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName)
                               .retrieveUserByUsername(username);
    }

    public String getNamespace() {
        return "structuredb";
    }

    public void addMoleculesFromSDF( String databaseName, 
                                     IFile file,
                                     IProgressMonitor monitor )
                throws BioclipseException {
        
        checkDatabaseName(databaseName);
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        
        int ticks = 1000000;

        monitor.beginTask( "Importing molecules from SDF to database", 
                           ticks );
        monitor.subTask( "Calculating size of file to import" );
        // first, count the number of items to read. 
        // It's a bit of overhead, but adds to the user experience
        int firstTaskTicks = (int) (0.05 * ticks);
        int entries = cdk.numberOfEntriesInSDF( 
                          file, new SubProgressMonitor( monitor, 
                                                        firstTaskTicks ) );
        int maintTaskTick 
            = (ticks - firstTaskTicks) 
              / ( entries != 0 ? entries : 1 ); 
         
        monitor.worked( firstTaskTicks );
        // now really read the structures
        
        monitor.subTask( "importing molecules" );
        
        Iterator<ICDKMolecule> iterator;
        int moleculesRead = 0;
        try {
            iterator = cdk.createMoleculeIterator( file ); 
        } 
        catch ( CoreException e ) {
            throw new IllegalArgumentException( "Could not open file:" + 
                                                file );
        } 
        String annotationId 
            = createTextAnnotation( databaseName,
                                    "label",
                                    file.getName()
                                     //extracts a name for our 
                                     //new annotation
                                    .replaceAll("\\..*?$", "") ).getId();

        TextAnnotation noSMILESAnnotation 
            = createTextAnnotation( databaseName,
                                    "label",
                                    "no SMILES" );
        
        while ( iterator.hasNext() ) {
            ICDKMolecule molecule = iterator.next();
            moleculesRead++;
            
            Object title = molecule.getAtomContainer()
                                   .getProperty(CDKConstants.TITLE);

            DBMolecule s = new DBMolecule( title == null ? ""
                                                         : title.toString(),
                                           molecule );

            if ( "".equals( s.getName() ) ) {
                s.setName( "\"" + s.getSMILES() + "\"" );
            }

            internalManagers.get(databaseName)
                            .insertMoleculeInAnnotation( s, 
                                                         annotationId );
            if ( "".equals( s.getSMILES() ) ) {
                noSMILESAnnotation.addDBMolecule( s );
            }
            monitor.worked( maintTaskTick );
        }
        internalManagers.get(databaseName).update( noSMILESAnnotation );
        monitor.done();
        fireAnnotationsChanged();        
    }
    
    public void addMoleculesFromSDF( String databaseName,
                                     String filePath,
                                     IProgressMonitor monitor )
                throws BioclipseException {
        checkDatabaseName(databaseName);
        addMoleculesFromSDF( databaseName, 
                             ResourcePathTransformer.getInstance()
                                                    .transform( filePath ), 
                             monitor );
    }

    public List<String> allDatabaseNames() {
        return new ArrayList<String>( internalManagers.keySet() );
    }

    public List<DBMolecule> allStructureFingerprintSearch( 
        String databaseName, ICDKMolecule molecule ) 
                            throws BioclipseException {
        
        checkDatabaseName(databaseName);
        List<DBMolecule> dBMolecules = new BioList<DBMolecule>();
        Iterator<DBMolecule> iterator 
            = internalManagers.get( databaseName )
                              .allStructuresIterator();
        while ( iterator.hasNext() ) {
            DBMolecule current = iterator.next();
            if ( cdk.fingerPrintMatches( new CDKMolecule( 
                                             current.getAtomContainer() ), 
                                         molecule ) ) {
                dBMolecules.add( current );
            }
        }
        return dBMolecules;
    }

    public void addMoleculeFromSDF( String databaseName, 
                                    String filePath )
                throws BioclipseException {

        checkDatabaseName(databaseName);
        addMoleculesFromSDF( databaseName, filePath, null );
    }

    public Iterator<DBMolecule> subStructureSearchIterator( 
            String databaseName,
            IMolecule queryMolecule,
            IProgressMonitor monitor ) throws BioclipseException {

        checkDatabaseName(databaseName);
        if ( !(queryMolecule instanceof ICDKMolecule) ) {
            queryMolecule = cdk.fromSMILES( queryMolecule.getSMILES() );
        }
        DBMolecule queryStructure 
            = new DBMolecule( "", (ICDKMolecule)queryMolecule );
        if (monitor != null) {
            monitor.beginTask( "substructure search", 
                               internalManagers.get( databaseName )
                                   .numberOfFingerprintMatches(
                                            queryStructure) );
        }
         
        return new SubStructureIterator( 
            internalManagers.get( databaseName )
                            .fingerprintSubstructureSearchIterator(
                                queryStructure ),
            cdk,
            (ICDKMolecule)queryMolecule, 
            this, 
            monitor );
   }
    
    public Iterator<DBMolecule> subStructureSearchIterator(
        String databaseName, IMolecule molecule )
                                throws BioclipseException {

        checkDatabaseName(databaseName);
        return subStructureSearchIterator( databaseName, 
                                           molecule, 
                                           null );
    }

    public List<DBMolecule> subStructureSearch( String databaseName,
                                                IMolecule molecule ) 
                           throws BioclipseException {
        checkDatabaseName(databaseName);
        return subStructureSearch( databaseName, molecule, null );
    }

    public List<DBMolecule> subStructureSearch( String databaseName,
                                                IMolecule molecule,
                                                IProgressMonitor monitor )
                           throws BioclipseException {
        
        checkDatabaseName(databaseName);
        List<DBMolecule> dBMolecules = new BioList<DBMolecule>();
        Iterator<DBMolecule> iterator 
            = subStructureSearchIterator( databaseName, 
                                          molecule, 
                                          monitor );
        while ( iterator.hasNext() ) {
            dBMolecules.add( iterator.next() );
        }
        return dBMolecules;
    }

    public void deleteAnnotation( String databaseName, 
                                  Annotation annotation ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).delete( annotation );
        fireAnnotationsChanged();
    }

    public void deleteStructure( String databaseName, 
                                 DBMolecule dBMolecule ) {
     
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).delete( dBMolecule );
    }

    public void save( String databaseName, DBMolecule dBMolecule ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).update( dBMolecule );
    }

    public void save( String databaseName, Annotation annotation ) {

        checkDatabaseName(databaseName);
        if ( annotation instanceof TextAnnotation ) {
            internalManagers.get( databaseName )
                            .update( (TextAnnotation)annotation );
        }
        else if ( annotation instanceof RealNumberAnnotation ) {
            internalManagers.get( databaseName )
                            .update( (RealNumberAnnotation)annotation );
        }
        else if ( annotation instanceof ChoiceAnnotation ) {
            internalManagers.get( databaseName )
                            .update( (ChoiceAnnotation)annotation );
        }
    }

    public List<DBMolecule> smartsQuery( String databaseName, 
                                         String smarts ) {

        checkDatabaseName(databaseName);
        return smartsQuery( databaseName, smarts, null );
    }

    public Iterator<DBMolecule> smartsQueryIterator( String databaseName,
                                                     String smarts ) {
        checkDatabaseName(databaseName);
        return smartsQueryIterator( databaseName, smarts, null );
    }
    
    public List<DBMolecule> smartsQuery( String databaseName, 
                                         String smarts,
                                         IProgressMonitor monitor ) {
    
        checkDatabaseName(databaseName);
        List<DBMolecule> hits = new BioList<DBMolecule>();
        Iterator<DBMolecule> iterator = smartsQueryIterator( databaseName, 
                                                             smarts, 
                                                             monitor );
        while ( iterator.hasNext() ) {
            hits.add( iterator.next() );
        }
        return hits;
    }

    public Iterator<DBMolecule> smartsQueryIterator( 
        String databaseName, String smarts, IProgressMonitor monitor) {

        checkDatabaseName(databaseName);
        if (monitor != null) {
            monitor.beginTask( "substructure search", 
                               internalManagers.get( databaseName )
                                               .numberOfMolecules() );
        }
        
        return new SmartsQueryIterator( internalManagers
                                            .get( databaseName )
                                            .allStructuresIterator(),
                                        cdk,
                                        smarts, 
                                        this, 
                                        monitor );
    }

    public void addListener( IDatabaseListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( IDatabaseListener listener ) {
        listeners.remove( listener );
    }
    
    public void fireDatabasesChanged() {
        //the original listeners collection gets edited during the loop
        for ( IDatabaseListener l 
              : new ArrayList<IDatabaseListener>(listeners) ) {
            l.onDataBaseUpdate( DatabaseUpdateType.DATABASES_CHANGED );
        }
    }
    
    public void fireAnnotationsChanged() {
        //the original listeners collection gets edited during the loop
        for ( IDatabaseListener l 
              : new ArrayList<IDatabaseListener>(listeners) ) {
            l.onDataBaseUpdate( DatabaseUpdateType.LABELS_CHANGED );
        }
    }

    public void deleteWithMolecules( String databaseName, 
                                     Annotation annotation ) {
        checkDatabaseName(databaseName);
        deleteWithMolecules( databaseName, annotation, null );
    }

    public void deleteWithMolecules( String databaseName, 
                                      Annotation annotation,
                                      IProgressMonitor monitor ) {
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName )
                        .deleteWithMolecules( annotation, monitor );
        fireAnnotationsChanged();
    }

    public void addStructuresFromSDF( String databaseName, IFile file ) 
                throws BioclipseException {

        addMoleculesFromSDF( databaseName, 
                             file, 
                             new NullProgressMonitor() );
    }

    public ChoiceAnnotation createChoiceAnnotation( String databaseName,
                                                    String propertyName,
                                                    String value )
                            throws IllegalArgumentException {

        checkDatabaseName( databaseName );
        ChoiceProperty property = (ChoiceProperty) 
                                   internalManagers.get( databaseName )
                                                   .retrievePropertyByName(
                                                       propertyName );
        if ( property == null ) {
            property = new ChoiceProperty(propertyName);
            internalManagers.get( databaseName )
                            .insertChoiceProperty( property );
        }
        ChoiceAnnotation annotation = new ChoiceAnnotation( value,
                                                            property );
        internalManagers.get( databaseName )
                        .insertChoiceAnnotation( annotation );
        return annotation;
    }

    public RealNumberAnnotation createRealNumberAnnotation( 
                                                  String databaseName,
                                                  String propertyName,
                                                  double value )
                                throws IllegalArgumentException {

        checkDatabaseName( databaseName );
        RealNumberProperty property 
            = (RealNumberProperty) 
              internalManagers.get( databaseName )
                              .retrievePropertyByName( propertyName );
        if ( property == null ) {
            property = new RealNumberProperty(propertyName);
            internalManagers.get( databaseName )
                            .insertRealNumberProperty( property );
        }
        RealNumberAnnotation annotation = new RealNumberAnnotation( value,
                                                                    property );
        internalManagers.get( databaseName )
                        .insertRealNumberAnnotation( annotation );
        return annotation;
    }

    public TextAnnotation createTextAnnotation( String databaseName,
                                                String propertyName,
                                                String value )
                          throws IllegalArgumentException {

        checkDatabaseName( databaseName );
        TextProperty property 
            = (TextProperty) 
              internalManagers.get( databaseName )
                              .retrievePropertyByName( propertyName );
        if ( property == null ) {
            property = new TextProperty( propertyName );
            internalManagers.get( databaseName )
                            .insertTextProperty( property );
        }
        TextAnnotation annotation = new TextAnnotation( value,
                                                        property );
        internalManagers.get( databaseName )
                        .insertTextAnnotation( annotation );
        return annotation;
    }

    public List<TextAnnotation> allLabels( String databaseName ) {

        return internalManagers.get( databaseName )
                              .allLabels();
    }

    public DBMolecule moleculeAtIndexInLabel( String databaseName, int index,
                                              TextAnnotation annotation ) {

        return internalManagers.get( databaseName )
                               .moleculeAtIndexInLabel(index, annotation);
        
    }

    public int numberOfMoleculesInLabel( String databaseName,
                                         TextAnnotation annotation ) {

        return internalManagers.get( databaseName )
                               .numberOfMoleculesInLabel(annotation);
    }
}

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
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
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
        if( files != null) {
            for( File file : files ) {
                if( file.getName().contains( ".sdb" ) ) {
                    loadDatabase(file);
                }
            }
        }
    }
    
    private void loadDatabase( File file ) {
        
        Matcher m = databaseNamePattern.matcher( file.getName() );
        if( m.matches() ) {
            String name = m.group( 1 );
            if( internalManagers.containsKey( name ) ) {
                return;
            }
            ApplicationContext context 
                = createApplicationcontext( name, true );
            
            applicationContexts.put( name, context );
            internalManagers
                .put( name, (IStructuredbInstanceManager) context
                            .getBean("structuredbInstanceManager") );
            LoggedInUserKeeper keeper 
                = (LoggedInUserKeeper)context
                  .getBean( "loggedInUserKeeper" );
            IUserDao userDao = (IUserDao) context.getBean( "userDao" );
            keeper.setLoggedInUser( userDao.getByUserName( "local" ) );
        }
    }

    public void createDatabase(String databaseName)
        throws IllegalArgumentException {

        if( internalManagers.containsKey(databaseName) ) {
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
            //TODO: The day we not only handle local databases the row here
            //      below will need to change
            newApplicationContexts.put( nameKey,
                                        createApplicationcontext(
                                            nameKey, true) );
            newInstances.put(
                    nameKey,
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
                                       .getBean(
                                         "structuredbInstanceManager") );
        createLocalUser( applicationContexts.get(databaseName) );
        logger.info( "A new local instance of Structuredb named"
                      + databaseName + " has been created" );
        fireDatabasesChanged();
    }

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

    private ApplicationContext createApplicationcontext( 
        String databaseName, boolean local ) {
        
        FileSystemXmlApplicationContext context
            = new FileSystemXmlApplicationContext(
                Structuredb.class
                           .getClassLoader()
                           .getResource("applicationContext.xml")
                           .toString() );

        BasicDataSource dataSource
            = (BasicDataSource) context.getBean("dataSource");

        if(local) {
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

    public Label createLabel(String databaseName, String folderName)
            throws IllegalArgumentException {

        Label label = new Label(folderName);
        checkDatabaseName(databaseName);
        internalManagers.get(databaseName).insertLabel(label);
        logger.debug("Label " + folderName + " inserted in " + databaseName);
        fireLabelsChanged();
        return label;
    }

    private void checkDatabaseName(String databaseName) {

        if( !internalManagers.containsKey(databaseName) ) {
            throw new IllegalArgumentException( "There is no database " +
                                                "named: " 
                                                + databaseName );
        }
    }

    public Structure createStructure( String databaseName,
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule)
                                      throws BioclipseException {

        checkDatabaseName(databaseName);
        Structure s = new Structure( moleculeName, cdkMolecule );
        internalManagers.get(databaseName).insertStructure(s);
        logger.debug( "Structure " + moleculeName
                      + " inserted in " + databaseName );
        return s;
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

    public void removeDatabase(String databaseName) {
        checkDatabaseName(databaseName);
        internalManagers.remove( databaseName );
        applicationContexts.remove( databaseName );
        HsqldbUtil.getInstance().remove( databaseName + ".sdb" );
        fireDatabasesChanged();
    }

    public List<Label> allLabels(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllLabels();
    }

    public List<Structure> allStructures(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName)
                               .retrieveAllStructures();
    }

    public List<User> allUsers(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllUsers();
    }

    public Label labelByName( String databaseName,
                              String folderName ) {

        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName)
                               .retrieveLabelByName(folderName);
    }

    public List<Structure> allStructuresByName( String databaseName,
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

    public void addStructuresFromSDF( String databaseName, 
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
        int maintTaskTick 
            = (ticks - firstTaskTicks) 
              / cdk.numberOfEntriesInSDF( file, 
                                          new SubProgressMonitor( 
                                              monitor, 
                                              firstTaskTicks ) );
         
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
        String labelId 
            = createLabel( databaseName,
                           file.getName()
                              //extracts a name for our new label
                              .replaceAll("\\..*?$", "") )
                              .getId();

        while ( iterator.hasNext() ) {
            ICDKMolecule molecule = iterator.next();
            moleculesRead++;
            
            Object title = molecule.getAtomContainer()
            .getProperty(CDKConstants.TITLE);

            Structure s
            = new Structure( title == null ? ""
                    : title.toString(),
                    molecule);

            if ( "".equals( s.getName() ) ) {
                s.setName( "\"" + s.getSmiles() + "\"" );
            }

            internalManagers.get(databaseName)
                            .insertStructureInLabel(s, labelId);
            monitor.worked( maintTaskTick );
        }
        monitor.done();
        fireLabelsChanged();        
    }
    
    public void addStructuresFromSDF( String databaseName,
                                      String filePath,
                                      IProgressMonitor monitor)
                throws BioclipseException {
        checkDatabaseName(databaseName);
        addStructuresFromSDF( databaseName, 
                              ResourcePathTransformer
                                  .getInstance()
                                  .transform( filePath ), 
                              monitor );
    }

    public List<String> listDatabaseNames() {
        return new ArrayList<String>( internalManagers.keySet() );
    }

    public List<Structure> allStructureFingerprintSearch( 
        String databaseName, ICDKMolecule molecule ) 
                           throws BioclipseException {
        
        checkDatabaseName(databaseName);
        List<Structure> structures = new BioList<Structure>();
        Iterator<Structure> iterator 
            = internalManagers.get( databaseName )
                              .allStructuresIterator();
        while( iterator.hasNext() ) {
            Structure current = iterator.next();
            if( cdk.fingerPrintMatches( new CDKMolecule( 
                                            current.getAtomContainer() ), 
                                        molecule ) ) {
                structures.add( current );
            }
        }
        return structures;
    }

    public void addStructuresFromSDF( String databaseName, 
                                      String filePath )
                throws BioclipseException {

        checkDatabaseName(databaseName);
        addStructuresFromSDF( databaseName, filePath, null );
    }

    public Iterator<Structure> subStructureSearchIterator( 
            String databaseName,
            IMolecule queryMolecule,
            IProgressMonitor monitor ) throws BioclipseException {

        checkDatabaseName(databaseName);
        ICDKMolecule cdkQueryMolecule;
        if(queryMolecule instanceof Structure) {
            cdkQueryMolecule 
                = toCDKMolecule( (Structure) queryMolecule );
        }
        else {
            cdkQueryMolecule 
                = cdk.fromSmiles( queryMolecule.getSmiles() );
        }
        Structure queryStructure = new Structure("", cdkQueryMolecule);
        if(monitor != null) {
            monitor
            .beginTask( "substructure search", 
                        internalManagers.get( databaseName )
                                        .numberOfFingerprintMatches(
                                            queryStructure) );
        }

         
        return new SubStructureIterator( 
            internalManagers.get( databaseName )
                            .fingerprintSubstructureSearchIterator(
                                queryStructure),
            cdk,
            cdkQueryMolecule, 
            this, 
            monitor );
   }
    
    public Iterator<Structure> subStructureSearchIterator(
        String databaseName, IMolecule molecule )
                               throws BioclipseException {

        checkDatabaseName(databaseName);
        return subStructureSearchIterator( databaseName, 
                                           molecule, 
                                           null );
    }

    public ICDKMolecule toCDKMolecule( Structure structure ) {
        try {
           return new CDKMolecule( structure.getName(), 
                                   (IAtomContainer) structure
                                                    .getAtomContainer()
                                                    .clone(),
                                   structure.getSmiles(),
                                   structure.getFingerPrint() );
        } 
        catch ( CloneNotSupportedException e ) {
            throw new RuntimeException(e);
        }
    }

    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule ) 
                           throws BioclipseException {
        checkDatabaseName(databaseName);
        return subStructureSearch( databaseName, molecule, null );
    }

    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule,
                                               IProgressMonitor monitor )
                           throws BioclipseException {
        
        checkDatabaseName(databaseName);
        List<Structure> structures = new BioList<Structure>();
        Iterator<Structure> iterator 
            = subStructureSearchIterator( databaseName, 
                                          molecule, 
                                          monitor );
        while( iterator.hasNext() ) {
            structures.add( iterator.next() );
        }
        return structures;
    }

    public void delete( String databaseName, Label label ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).delete( label );
        fireLabelsChanged();
    }

    public void delete( String databaseName, Structure structure ) {
     
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).delete( structure );
    }

    public void save( String databaseName, Structure structure ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).update( structure );
    }

    public void save( String databaseName, Label label ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).update( label );
    }

    public List<Structure> smartsQuery( String databaseName, 
                                        String smarts ) {

        checkDatabaseName(databaseName);
        return smartsQuery( databaseName, smarts, null );
    }

    public Iterator<Structure> smartsQueryIterator( String databaseName,
                                                    String smarts ) {
        checkDatabaseName(databaseName);
        return smartsQueryIterator( databaseName, smarts, null );
    }
    
    public List<Structure> smartsQuery( String databaseName, 
                                        String smarts,
                                        IProgressMonitor monitor) {
    
        checkDatabaseName(databaseName);
        List<Structure> hits = new BioList<Structure>();
        Iterator<Structure> iterator = smartsQueryIterator( databaseName, 
                                                            smarts, 
                                                            monitor );
        while ( iterator.hasNext() ) {
            hits.add( iterator.next() );
        }
        return hits;
    }

    public Iterator<Structure> smartsQueryIterator( 
        String databaseName, String smarts, IProgressMonitor monitor) {

        checkDatabaseName(databaseName);
        if(monitor != null) {
            monitor.beginTask( "substructure search", 
                               internalManagers.get( databaseName )
                                               .numberOfStructures() );
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
    
    public void fireLabelsChanged() {
      //the original listeners collection gets edited during the loop
        for ( IDatabaseListener l 
              : new ArrayList<IDatabaseListener>(listeners) ) {
            l.onDataBaseUpdate( DatabaseUpdateType.LABELS_CHANGED );
        }
    }

    public Label retrieveLabelByName( String databaseName, 
                                      String labelName ) {
        checkDatabaseName(databaseName);
        return internalManagers.get( databaseName )
                               .retrieveLabelByName( labelName );
    }

    public void deleteWithStructures( String databaseName, Label label ) {
        checkDatabaseName(databaseName);
        deleteWithStructures( databaseName, label, null );
    }

    public void deleteWithStructures( String databaseName, Label label,
                                      IProgressMonitor monitor ) {
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName )
                        .deleteWithStructures( label, monitor );
        fireLabelsChanged();
    }

}

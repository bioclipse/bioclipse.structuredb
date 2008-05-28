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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.BioList;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.internalbusiness.LoggedInUserKeeper;
import net.bioclipse.structuredb.persistency.dao.IUserDao;
import net.bioclipse.structuredb.persistency.tables.TableCreator;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
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
     * This isn't super if cdk starts using AOP for fancy stuff in the future
     * but we don't want the recorded variant and this is a solution that is
     * reasonably easy to test. Should cdk start using fancy stuff real
     * integration testing running the OSGI layer is needed and this instance
     * would need to come from the OSGI service container
     */
    private ICDKManager cdk = new CDKManager();

    //Package protected for testing purposes
    Map<String, IStructuredbInstanceManager> internalManagers
        = new HashMap<String, IStructuredbInstanceManager>();

    //Package protected for testing purposes
    Map<String, ApplicationContext> applicationContexts
        = new HashMap<String, ApplicationContext>();

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
            ApplicationContext context = createApplicationcontext( name, true );
            
            applicationContexts.put( name, context );
            internalManagers.put( name, 
                                  (IStructuredbInstanceManager) context
                                      .getBean("structuredbInstanceManager") );
            LoggedInUserKeeper keeper 
                = (LoggedInUserKeeper)context.getBean( "loggedInUserKeeper" );
            IUserDao userDao = (IUserDao) context.getBean( "userDao" );
            keeper.setLoggedInUser( userDao.getByUserName( "local" ) );
        }
    }

    public void createLocalInstance(String databaseName)
        throws IllegalArgumentException {

        if( internalManagers.containsKey(databaseName) ) {
            throw new IllegalArgumentException( "Database name already used: "
                                                + databaseName );
        }
        TableCreator.INSTANCE.createTables(
            HsqldbUtil.getInstance().getConnectionUrl(databaseName + ".sdb") );

        Map<String, IStructuredbInstanceManager> newInstances
            = new HashMap<String, IStructuredbInstanceManager>();

        Map<String, ApplicationContext> newApplicationContexts
            = new HashMap<String, ApplicationContext>();

        for( String nameKey : internalManagers.keySet() ) {
            //TODO: The day we not only handle local databases the row here
            //      below will need to change
            newApplicationContexts.put( nameKey,
                                        createApplicationcontext(nameKey, true) );
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
                                 createApplicationcontext(databaseName, true) );
        internalManagers.put(
                databaseName,
                (IStructuredbInstanceManager)
                    applicationContexts.get( databaseName)
                                       .getBean("structuredbInstanceManager") );
        createLocalUser( applicationContexts.get(databaseName) );
        logger.info( "A new local instance of Structuredb named"
                      + databaseName + " has been created" );
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
                                     context.getBean( "loggedInUserKeeper" );
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

    public Folder createFolder(String databaseName, String folderName)
            throws IllegalArgumentException {

        Folder folder = new Folder(folderName);
        if( !internalManagers.containsKey(databaseName) ) {
            throw new IllegalArgumentException( "There is no database named: " 
                                                + databaseName );
        }
        internalManagers.get(databaseName).insertFolder(folder);
        logger.debug("Folder " + folderName + " inserted in " + databaseName);
        return folder;
    }

    public Structure createStructure( String databaseName,
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule)
                                      throws BioclipseException {

        Structure s = new Structure( moleculeName, cdkMolecule );
        internalManagers.get(databaseName).insertStructure(s);
        logger.debug( "Structure " + moleculeName
                      + " inserted in " + databaseName );
        return s;
    }

    public User createUser( String databaseName,
                            String username,
                            String password,
                            boolean sudoer) throws IllegalArgumentException {

        User user = new User(username, password, sudoer);
        internalManagers.get(databaseName).insertUser(user);
        return user;
    }

    public void removeLocalInstance(String databaseName) {
        internalManagers.remove( databaseName );
        applicationContexts.remove( databaseName );
        HsqldbUtil.getInstance().remove( databaseName + ".sdb" );
    }

    public List<Folder> allFolders(String databaseName) {
        return internalManagers.get(databaseName).retrieveAllFolders();
    }

    public List<Structure> allStructures(String databaseName) {
        return internalManagers.get(databaseName).retrieveAllStructures();
    }

    public List<User> allUsers(String databaseName) {
        return internalManagers.get(databaseName).retrieveAllUsers();
    }

    public Folder folderByName( String databaseName,
                                        String folderName ) {

        return internalManagers.get(databaseName)
                               .retrieveFolderByName(folderName);
    }

    public List<Structure> allStructuresByName( String databaseName,
                                                     String structureName ) {
        return internalManagers.get(databaseName)
                               .retrieveStructureByName(structureName);
    }

    public User userByName(String databaseName, String username) {
        return internalManagers.get(databaseName)
                               .retrieveUserByUsername(username);
    }

    public String getNamespace() {
        return "structuredb";
    }

    public void addStructuresFromSDF( String databaseName,
                                      String filePath,
                                      IProgressMonitor monitor)
                                      throws BioclipseException {
        // first, count the number of items to read. It's a bit of overhead,
        // but adds to the user experience
        int moleculesToRead = 0;
        try {
            FileInputStream counterStream = new FileInputStream(filePath);
            int c = 0;
            while (c != -1) {
                c = counterStream.read();
                if (c == '$') {
                    c = counterStream.read();
                    if (c == '$') {
                        c = counterStream.read();
                        if (c == '$') {
                            c = counterStream.read();
                            if (c == '$') {
                                moleculesToRead++;
                                counterStream.read();
                            }
                        }
                    }
                }
            }
            counterStream.close();
        } catch (Exception exception) {
            // ok, I give up...
            logger.debug("Could not determine the number of molecules to read, because: " +
                         exception.getMessage(), exception
            );
        }

        // now really read the structures
        if(monitor != null) {
            monitor.beginTask( "Reading molecules from sdf file", 
                               moleculesToRead );
        }
        Iterator<ICDKMolecule> iterator;
        int moleculesRead = 0;
        try {
            iterator = cdk.creatMoleculeIterator( 
                new FileInputStream(filePath) );
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException( "Could not open file:" + 
                                                filePath );
        }
        File file = new File(filePath);
        String folderId = createFolder( databaseName,
                                        file.getName().replaceAll("\\..*?$", "") )
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

            internalManagers.get(databaseName).insertStructureInFolder(s, folderId);
            if(monitor != null) {
                monitor.worked( 1 );
            }
        }
        if(monitor != null) {
            monitor.done();
        }
    }

    public List<String> listDatabaseNames() {
        return new ArrayList<String>( internalManagers.keySet() );
    }

    public List<Structure> allStructureFingerprintSearch( String databaseName,
                                                    ICDKMolecule molecule ) 
                           throws BioclipseException {
        
        List<Structure> structures = new BioList<Structure>();
        Iterator<Structure> iterator 
            = internalManagers.get( databaseName ).allStructuresIterator();
        while( iterator.hasNext() ) {
            Structure current = iterator.next();
            if( cdk.fingerPrintMatches( new CDKMolecule( current
                                                         .getAtomContainer() ), 
                                        molecule ) ) {
                structures.add( current );
            }
        }
        return structures;
    }

    public void addStructuresFromSDF( String databaseName, String filePath )
                throws BioclipseException {

        addStructuresFromSDF( databaseName, filePath, null );
    }

    public Iterator<Structure> subStructureSearchIterator( 
            String databaseName,
            IMolecule queryMolecule,
            IProgressMonitor monitor ) throws BioclipseException {

        
        if(monitor != null) {
            monitor.beginTask( "substructure search", 
                               internalManagers.get( databaseName )
                                               .numberOfStructures() );
        }
        ICDKMolecule cdkQueryMolecule;
        if(queryMolecule instanceof Structure) {
            cdkQueryMolecule = toCDKMolecule( (Structure) queryMolecule );
        }
        else {
            cdkQueryMolecule = cdk.fromSmiles( queryMolecule.getSmiles() );
        }
        Structure queryStructure = new Structure("", cdkQueryMolecule);
         
        return new SubStructureIterator( internalManagers
                                         .get( databaseName )
                                         .fingerprintSubstructureSearchIterator(
                                             queryStructure),
                                         cdk,
                                         cdkQueryMolecule, 
                                         this, 
                                         monitor );
   }
    
    public Iterator<Structure> subStructureSearchIterator(String databaseName,
                                                          IMolecule molecule)
                               throws BioclipseException {

         return subStructureSearchIterator( databaseName, molecule, null );
    }
    
    public static class SubStructureIterator 
                  implements Iterator<Structure> {

        private Structure next = null;
        private Iterator<Structure> parent;
        private ICDKManager cdk;
        private ICDKMolecule subStructure;
        private IStructuredbManager structuredb;
        private IProgressMonitor monitor;
        
        public SubStructureIterator( Iterator<Structure> iterator, 
                                     ICDKManager cdk,
                                     ICDKMolecule subStructure,
                                     IStructuredbManager structuredb, 
                                     IProgressMonitor monitor ) {
            parent   = iterator;
            this.cdk = cdk;
            this.subStructure = subStructure;
            this.structuredb = structuredb;
            this.monitor = monitor;
        }

        public boolean hasNext() {

            if( next != null ) {
                return true;
            }
            try {
                next = findNext();
            } catch ( BioclipseException e ) {
                throw new RuntimeException(e);
            }
            return next != null;
        }

        private Structure findNext() throws BioclipseException {

            while( parent.hasNext() ) {
                Structure next = parent.next();
                if(monitor != null) {
                    monitor.worked( 1 );
                }
                ICDKMolecule molecule;
                molecule = structuredb.toCDKMolecule( next );
                if( cdk.subStructureMatches( molecule, subStructure ) ) {
                    return next;
                }
            }
            if( monitor != null ) {
                monitor.done();
            }
            return null;
        }

        public Structure next() {

            if( !hasNext() ) {
                throw new IllegalStateException( "there are no more " +
                                                 "such structures" );
            }
            Structure next = this.next;
            this.next = null;
            return next;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public ICDKMolecule toCDKMolecule( Structure structure ) {
        try {
           return new CDKMolecule( structure.getName(), 
                             (IAtomContainer) structure.getAtomContainer().clone(),
                             structure.getSmiles(),
                             structure.getFingerPrint() );
        } catch ( CloneNotSupportedException e ) {
            throw new RuntimeException(e);
        }
    }

    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule ) 
                           throws BioclipseException {
        return subStructureSearch( databaseName, molecule, null );
    }

    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule,
                                               IProgressMonitor monitor )
                           throws BioclipseException {
        
        List<Structure> structures = new BioList<Structure>();
        Iterator<Structure> iterator = subStructureSearchIterator( databaseName, 
                                                                   molecule, 
                                                                   monitor );
        int numOfStructures = internalManagers.get( databaseName )
                                              .numberOfStructures();
        while( iterator.hasNext() ) {
            structures.add( iterator.next() );
        }
        return structures;
    }
}

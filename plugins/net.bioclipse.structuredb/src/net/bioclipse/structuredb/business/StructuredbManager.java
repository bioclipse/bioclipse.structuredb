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
package net.bioclipse.structuredb.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.domain.RecordableList;
import net.bioclipse.core.util.TimeCalculator;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.structuredb.Activator;
import net.bioclipse.structuredb.FileStoreKeeper;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.business.IStructureDBChangeListener.DatabaseUpdateType;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.Property;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.internalbusiness.IStructuredbInstanceManager;
import net.bioclipse.structuredb.persistency.tables.TableCreator;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.openscience.cdk.CDKConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author jonalv
 */
public class StructuredbManager implements IBioclipseManager {

    private Logger logger = Logger.getLogger(StructuredbManager.class);

    private static final Pattern databaseNamePattern 
        = Pattern.compile( "(.*?)\\.sdb.*" );
    
    protected ICDKManager cdk 
        = net.bioclipse.cdk.business.Activator.getDefault()
             .getJavaScriptCDKManager();
    
    //Package protected for testing purposes
    Map<String, IStructuredbInstanceManager> internalManagers
        = Collections.synchronizedMap( 
              new HashMap<String, IStructuredbInstanceManager>() );

    //Package protected for testing purposes
    Map<String, ApplicationContext> applicationContexts
        = Collections.synchronizedMap( 
              new HashMap<String, ApplicationContext>() );

    private Collection<IStructureDBChangeListener> listeners 
        = Collections.synchronizedSet( 
              new HashSet<IStructureDBChangeListener>() );

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
        logger.info( "A new local instance of Structuredb named "
                      + databaseName + " has been created" );
        fireDatabasesChanged();
        updateDatabaseDecorators();
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
                + "'.\n" + "Use `" + getManagerName() 
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
        updateDatabaseDecorators();
        return m;
    }

    public void deleteDatabase(String databaseName, IProgressMonitor monitor) {
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).dropDataBase(monitor);
        internalManagers.remove( databaseName );
        applicationContexts.remove( databaseName );
        HsqldbUtil.getInstance().remove( databaseName + ".sdb" );
        fireDatabasesChanged();
        updateDatabaseDecorators();
        monitor.done();
    }

    public List<Annotation> allAnnotations(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllAnnotations();
    }

    public List<DBMolecule> allMolecules(String databaseName) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName).retrieveAllMolecules();
    }

    public List<DBMolecule> allMoleculesByName( String databaseName,
                                                String structureName ) {
        checkDatabaseName(databaseName);
        return internalManagers.get(databaseName)
                               .retrieveStructureByName(structureName);
    }

    public String getManagerName() {
        return "structuredb";
    }

    public void addMoleculesFromSDF( String databaseName, 
                                     IFile file,
                                     IProgressMonitor monitor )
                throws BioclipseException {
        
        IStructuredbInstanceManager manager 
            = internalManagers.get(databaseName);
        
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
        monitor.subTask( "Importing structure: " 
                         + firstTaskTicks + "/" + entries );
        // now really read the structures
        
        Iterator<ICDKMolecule> iterator;
        int moleculesRead = 0;
        monitor.subTask("reading " + moleculesRead + "/" + entries);
        try {
            iterator = cdk.createMoleculeIterator( file );
        } 
        catch ( CoreException e ) {
            throw new IllegalArgumentException( "Could not open file:" + 
                                                file );
        } 
        TextAnnotation label 
            = createTextAnnotation( databaseName,
                                    "label",
                                    file.getName()
                                     //extracts a name for our 
                                     //new annotation
                                    .replaceAll("\\..*?$", "") );

        long start = System.currentTimeMillis();
        int current = 0;
        while ( iterator.hasNext() && !monitor.isCanceled()) {
            String timeEstimation = "";
            
            if ( entries < 500 || current++ % 50 == 0 ) {
                if ( System.currentTimeMillis() - start > 5000 ) {
                    timeEstimation 
                        = " (" + TimeCalculator
                                     .generateTimeRemainEst( start, 
                                                             moleculesRead, 
                                                             entries )
                          + " for file: " + file.getName() + ")";
                }
                monitor.subTask( "Read: " + moleculesRead + "/" + entries 
                                 + timeEstimation );
            }


            ICDKMolecule molecule = iterator.next();
            moleculesRead++;
            
            Object title = molecule.getAtomContainer()
                                   .getProperty(CDKConstants.TITLE);

            DBMolecule s = new DBMolecule( title == null ? ""
                                                         : title.toString(),
                                           molecule );

            if ( "".equals( s.getName() ) ) {
                s.setName( "\"" + s.toSMILES(
                ) + "\"" );
            }
            
            manager.insertMoleculeInAnnotation( s, label.getId() );
//            s.addAnnotation( label );
            
            Map<?, ?> properties = molecule.getAtomContainer().getProperties();
            
            for ( Object o : properties.keySet() ) {
                String key = o.toString();
                
                Property p = manager.retrievePropertyByName( key );
                if ( p == null ) {
                    p = new TextProperty(key);
                    manager.insertTextProperty( (TextProperty) p );
                }
                if ( !(p instanceof TextProperty) ) {
                    p = new TextProperty( "Stringified:" + key );
                    manager.insertTextProperty( (TextProperty) p );
                }
                Annotation a = new TextAnnotation( properties.get( key )
                                                             .toString(), 
                                                   (TextProperty)p );
                manager.insertTextAnnotation( (TextAnnotation) a );
                manager.annotate(s, a);
                
            }
            
            monitor.worked( maintTaskTick );
        }
        long end = System.currentTimeMillis();
        logger.debug("addMoleculesFromSDF took " + (end - start) + " ms");
        iterator = null;
        monitor.done();
        fireAnnotationsChanged(); 
        updateDatabaseDecorators();
    }

    public List<String> allDatabaseNames() {
        return new ArrayList<String>( internalManagers.keySet() );
    }

    public List<DBMolecule> allStructureFingerprintSearch( 
        String databaseName, ICDKMolecule molecule ) 
                            throws BioclipseException {
        
        checkDatabaseName(databaseName);
        List<DBMolecule> dBMolecules = new RecordableList<DBMolecule>();
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

    public Iterator<DBMolecule> subStructureSearchIterator( 
            String databaseName,
            IMolecule queryMolecule,
            IProgressMonitor monitor ) throws BioclipseException {

        checkDatabaseName(databaseName);
        if ( !(queryMolecule instanceof ICDKMolecule) ) {
            queryMolecule = cdk.fromSMILES( queryMolecule.toSMILES(
            ));
        }
        DBMolecule queryStructure 
            = new DBMolecule( "", (ICDKMolecule)queryMolecule );
        int ticks = internalManagers.get( databaseName )
                                    .numberOfFingerprintMatches( 
                                        queryStructure );
        if (monitor != null) {
            monitor.beginTask( "Sub Structure Search", 
                                ticks );
        }
         
        return new SubStructureIterator( 
            internalManagers.get( databaseName )
                            .fingerprintSubstructureSearchIterator(
                                queryStructure ),
            cdk,
            (ICDKMolecule)queryMolecule, 
            monitor,
            ticks );
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
                                                IMolecule molecule,
                                                IProgressMonitor monitor )
                           throws BioclipseException {
        
        if ( monitor == null ) {
            monitor = new NullProgressMonitor();
        }
        
        checkDatabaseName(databaseName);
        List<DBMolecule> dBMolecules = new RecordableList<DBMolecule>();
        Iterator<DBMolecule> iterator 
            = subStructureSearchIterator( databaseName, 
                                          molecule, 
                                          monitor );
        while ( iterator.hasNext() ) {
            dBMolecules.add( iterator.next() );
            if ( monitor.isCanceled() ) {
                throw new OperationCanceledException();
            }
        }
        return dBMolecules;
    }

    public void deleteAnnotation( String databaseName, 
                                  Annotation annotation ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).delete( annotation );
        fireAnnotationsChanged();
        updateDatabaseDecorators();
    }

    public void deleteStructure( String databaseName, 
                                 DBMolecule dBMolecule ) {
     
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).delete( dBMolecule );
        updateDatabaseDecorators();
    }

    public void save( String databaseName, DBMolecule dBMolecule ) {

        checkDatabaseName(databaseName);
        internalManagers.get( databaseName ).update( dBMolecule );
        updateDatabaseDecorators();
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
        fireAnnotationsChanged();
        updateDatabaseDecorators();
    }

    public Iterator<DBMolecule> smartsQueryIterator( String databaseName,
                                                     String smarts ) {
        checkDatabaseName(databaseName);
        return smartsQueryIterator( databaseName, smarts, null );
    }
    
    public static class SMARTSQueryResultList 
                  extends RecordableList<DBMolecule> {
        
        private List<DBMolecule> failedMolecules = new ArrayList<DBMolecule>();
        
        public void addFailedMolecules( List<DBMolecule> failedMolecules ) {
            this.failedMolecules.addAll( failedMolecules );
        }
        
        public boolean hasFailedMolecules() {
            return failedMolecules.size() > 0;
        }
        
        public List<DBMolecule> getFailedMolecules() {
            return failedMolecules;
        }
    }
    
    public List<DBMolecule> smartsQuery( String databaseName, 
                                         String smarts,
                                         IProgressMonitor monitor ) {
    
        checkDatabaseName(databaseName);
        long start = System.currentTimeMillis();
        SMARTSQueryResultList hits = new SMARTSQueryResultList();
        SmartsQueryIterator iterator = smartsQueryIterator( databaseName, 
                                                             smarts, 
                                                             monitor );
        while ( iterator.hasNext() ) {
            hits.add( iterator.next() );
        }
        
        hits.addFailedMolecules( iterator.getFailedMolecules() );
        
        logger.debug( "Time to perform smartQuery: " 
                      + (System.currentTimeMillis() - start)
                      + " ms." );
        return hits;
    }

    public SmartsQueryIterator smartsQueryIterator( 
        String databaseName, String smarts, IProgressMonitor monitor) {

        checkDatabaseName(databaseName);
        int numOfMolecules = internalManagers.get( databaseName )
                                             .numberOfMolecules();
        if (monitor != null) {
            monitor.beginTask( "SMARTS querying", 
                               numOfMolecules );
        }
        
        return new SmartsQueryIterator( internalManagers
                                            .get( databaseName )
                                            .allStructuresIterator(),
                                        cdk,
                                        smarts, 
                                        this,
                                        numOfMolecules,
                                        monitor );
    }

    public void addListener( IStructureDBChangeListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( IStructureDBChangeListener listener ) {
        listeners.remove( listener );
    }
    
    public void fireDatabasesChanged() {
        //the original listeners collection gets edited during the loop
        for ( IStructureDBChangeListener l 
              : new ArrayList<IStructureDBChangeListener>(listeners) ) {
            l.onDataBaseUpdate( DatabaseUpdateType.DATABASES_CHANGED );
        }
    }
    
    public void fireAnnotationsChanged() {
        //the original listeners collection gets edited during the loop
        for ( IStructureDBChangeListener l 
              : new ArrayList<IStructureDBChangeListener>(listeners) ) {
            l.onDataBaseUpdate( DatabaseUpdateType.LABELS_CHANGED );
        }
    }

    public void deleteWithMolecules( String databaseName, 
                                     Annotation annotation ) {
        checkDatabaseName(databaseName);
        deleteWithMolecules( databaseName, annotation, null );
        updateDatabaseDecorators();
    }

    public void deleteWithMolecules( String databaseName, 
                                     Annotation annotation,
                                     IProgressMonitor monitor ) {
        checkDatabaseName(databaseName);
        internalManagers.get( databaseName )
                        .deleteWithMolecules( annotation, monitor );
        fireAnnotationsChanged();
        updateDatabaseDecorators();
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
        fireAnnotationsChanged();
        updateDatabaseDecorators();
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
        fireAnnotationsChanged();
        updateDatabaseDecorators();
        return annotation;
    }

    public List<TextAnnotation> allLabels( String databaseName ) {

        IStructuredbInstanceManager m = internalManagers.get( databaseName ); 
        if ( m == null ) {
            return Collections.EMPTY_LIST;
        }
        return  m.allLabels();
    }

    public DBMolecule moleculeAtIndexInLabel( String databaseName, int index,
                                              TextAnnotation annotation ) {

        return internalManagers.get( databaseName )
                               .moleculeAtIndexInLabel(index, annotation);
        
    }

    public int numberOfMoleculesInLabel( String databaseName,
                                         TextAnnotation annotation ) {

        IStructuredbInstanceManager m = internalManagers.get( databaseName );
        if ( m != null ) {
            return m.numberOfMoleculesInLabel(annotation);
        }
        return 0;
    }

    public int numberOfMoleculesInDatabaseInstance( String databaseName ) {

        IStructuredbInstanceManager m = internalManagers.get( databaseName );
        if ( m == null ) {
            return 0;
        }
        return m.numberOfMolecules();
    }
    
    private void updateDatabaseDecorators() {
        if ( Activator.getDefault() != null ) {
            Activator.getDefault().triggerDatabaseDecoratorsUpdate();
        }
    }

    public void addMoleculesFromFiles( String dbName,
                                       List<?> files,
                                       IProgressMonitor monitor ) {
        
        int ticks = 1000000;
        monitor.beginTask( "Importing from files", ticks );
        List<IFile> fileList = new ArrayList<IFile>();
        for ( Object o : files ) {
            if ( o instanceof IFile ) {
                fileList.add( (IFile) o );
            }
            else if ( o instanceof String ) {
                fileList.add( 
                    ResourcePathTransformer.getInstance()
                                           .transform( (String) o ) );
            }
            else {
                throw new IllegalArgumentException( 
                    o.toString() + " is not a String nor an IFile " );
            }
        }
        
        ICDKManager cdk = net.bioclipse.cdk.business.Activator
                             .getDefault().getJavaCDKManager();
        
        for ( IFile f : fileList ) {
            try {
                String id = f.getContentDescription().getContentType().getId();
                if ( id.equals( "net.bioclipse.contenttypes.sdf" )   ||
                     id.equals( "net.bioclipse.contenttypes.sdf2d" ) ||
                     id.equals( "net.bioclipse.contenttypes.sdf3d" ) ||
                     id.equals( "net.bioclipse.contenttypes.sdf0d" ) ) { 
                    
                
                    addMoleculesFromSDF( 
                        dbName, 
                        f,
                        new SubProgressMonitor( monitor, 
                                                ticks/fileList.size() ) );
                }
                else {
                    for ( ICDKMolecule m : cdk.loadMolecules( f ) ) {
                        createMolecule( dbName, "", m );
                    }
                }
            }
            catch ( Exception e ) {
                throw new RuntimeException("Ooops", e);
            }
        }
    }

    public Iterator<DBMolecule> allStructuresIterator( String databaseName ) {
        checkDatabaseName( databaseName );
        return internalManagers.get( databaseName ).allStructuresIterator();
    }

    public void updateMolecule( String dbName, DBMolecule molecule ) {
        checkDatabaseName( dbName );
        internalManagers.get( dbName ).update( molecule );
        fireAnnotationsChanged();
        updateDatabaseDecorators();
    }
    
    public void deleteAllDatabases(IProgressMonitor m) {
        m.beginTask( "Deleting databases (can not be aborted)", 
                     IProgressMonitor.UNKNOWN );
        for ( String databaseName : new HashSet<String>( 
                                            internalManagers.keySet() ) ) {
            internalManagers.remove( databaseName );
            applicationContexts.remove( databaseName );
            HsqldbUtil.getInstance().remove( databaseName + ".sdb" );
        }
        
        FileStoreKeeper.FILE_STORE.deleteAll();
        
        fireDatabasesChanged();
        updateDatabaseDecorators();
        m.done();
    }
    
    public void annotate( String databaseName, DBMolecule m, Annotation a ) {
        checkDatabaseName( databaseName );
        internalManagers.get( databaseName ).annotate( m, a );
    }
    
    public Collection<String> 
           getAvailableProperties( String databaseName,
                                   TextAnnotation annotation ) {
        checkDatabaseName( databaseName );
        return internalManagers.get(databaseName)
                               .getAvailableProperties(annotation);
    }
}

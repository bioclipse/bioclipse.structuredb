/* *****************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.hsqldb;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.hsqldb.Server;
import org.hsqldb.ServerConstants;

/**
 * @author jonalv
 *
 */
public class HsqldbUtil {

    private static final Logger logger = Logger.getLogger(HsqldbUtil.class);
    private File fileFolder;
    private Set<String> urls = new HashSet<String>();
    private static final HsqldbUtil INSTANCE = new HsqldbUtil();
    
    private HsqldbUtil() {
        
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        String path;
        try {
             path = ResourcesPlugin.getWorkspace()
                                   .getRoot()
                                   .getLocation()
                                   .toString() 
                                   + File.separator; 
        }
        catch (IllegalStateException e) {
            path = HsqldbUtil.class.getClassLoader()
                                   .getResource(".").toString();
        }
        if( path.startsWith("file:") ) {
            path = path.substring(5);
        }
        path += ".hsqldbDatabases";
        File f = new File(path);
        logger.debug( "created directory: " + path 
                      + " for storing databasefile");
        fileFolder = f;
    }
    
    public static HsqldbUtil getInstance() {
        return INSTANCE;
    }
    
    /**
     * Returns the url to connect to the database identified with the given 
     * name with
     * 
     * @param name - use a unique name preferably with some form of extra info 
     *               saying which sort of database for example a structure 
     *               database might be structures.sdb
     * @return url to database
     */
    public String getConnectionUrl(String name) {
        String url = buildUrl(name);
        urls.add( url );
        logger.debug("Connection URL: " + url + " returned");
        return url;
    }
    
    private String buildUrl( String name ) {
        return "jdbc:hsqldb:file:" + fileFolder + File.separator + name;
    }

    /**
     * Stops the Hsqldb-server
     * 
     * @throws ClassNotFoundException
     */
    public void stopAllDatabaseInstances() {

        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for(String url : urls) {
            try {
                Connection con = DriverManager.getConnection(url);
            
                Statement stmt = con.createStatement();
                stmt.executeUpdate("SHUTDOWN");
                stmt.close();
                con.close();
                logger.debug("SHUTDOWN sent to " + url);
            } 
            catch (SQLException e) {
                throw new IllegalStateException(
                        "Could not perform shutdown statement", e);
            }
        }
        urls.clear();
    }

    public File getDatabaseFilesDirectory() {
        return fileFolder;
    }

    public void remove( String databaseName ) {
        
        File[] files = fileFolder.listFiles(); 
        if ( files == null ) {
           logger.error( fileFolder + " doesn't seem to be a directory" );
           return;
        }
        
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } 
        catch (ClassNotFoundException e1) {
            throw new RuntimeException(e1);
        }
        
        String url = buildUrl( databaseName );
        try {
            Connection con = DriverManager.getConnection(url);
        
            Statement stmt = con.createStatement();
            String statement = "SHUTDOWN";
            stmt.executeUpdate( statement );
            stmt.close();
            con.close();
            logger.debug(statement + " sent to " + url);
        } 
        catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not perform shutdown statement", e);
        }
        urls.remove( url );
        String lockFileName = databaseName + ".lck";
        boolean foundLock;
        do {
            foundLock = false;
            for ( File f : fileFolder.listFiles() ) {
                if ( f.getName().equals( lockFileName ) ) {
                    foundLock = true;
                    break;
                }
            }
            if ( foundLock ) {
                try {
                    Thread.sleep( 100 );
                }
                catch ( InterruptedException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        while ( foundLock ); 
        for ( File f : fileFolder.listFiles() ) {
            if( f.getName().contains( databaseName ) ) {
                f.delete();
            }
        }
    }
}

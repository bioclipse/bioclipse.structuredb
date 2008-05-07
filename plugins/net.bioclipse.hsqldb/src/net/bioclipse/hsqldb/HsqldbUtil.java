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
    private String fileFolder;
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
        logger.debug("created directory: " + path + " for storing databasefile");
        fileFolder = path;
    }
    
    public static HsqldbUtil getInstance() {
        return INSTANCE;
    }
    
    /**
     * Returns the url to connect to the database identified with the given 
     * name with
     * 
     * @param name
     * @return url to database
     */
    public String getConnectionUrl(String name) {
        String url = "jdbc:hsqldb:file:" 
                     + fileFolder + File.separator + name + ".data";
        urls.add(url);
        logger.debug("Connection URL: " + url + " returned");
        return url;
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
    }
}

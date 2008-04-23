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
import java.util.Map;

import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;


import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.PlatformUI;
import org.hsqldb.Server;
import org.hsqldb.ServerConstants;



public class HsqldbUtil {

	private static final Logger logger = Logger.getLogger(HsqldbUtil.class);
	private static String fileFolder;
	static {
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
		path += ".hsqldbDatabases" + File.separator;
		File f = new File(path);
		try {
			f.createNewFile();
		} catch (IOException e) {
			LogUtils.debugTrace(logger, e);
		}
		fileFolder = path;
	}
	
    

	private static Server server;
	private final static HsqldbUtil instance = new HsqldbUtil();
	private int nextFreePos = 1;
	
	private Map<Integer, String> paths = new HashMap<Integer, String>();
	private Map<Integer, String> names = new HashMap<Integer, String>();
	
	private HsqldbUtil() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		server = new Server();
	}
	
	public static HsqldbUtil getInstance() {
		return instance;
	}
	
	public boolean serverIsAlive() {
		return server != null && 
			   server.getState() == ServerConstants.SERVER_STATE_ONLINE;
	}
	
	/**
	 * Starts the Hsqldb server with one local database
	 */
	public void startHsqldbServer() {

		if( serverIsAlive() ) {
			logger.debug( "startHsqldbServer called but the " +
					      "server was alredy running" );
			return;
		}
		
		String database;
		try {
			 database =	ResourcesPlugin.getWorkspace()
			                           .getRoot()
			                           .getLocation()
			                           .toString() 
			                           + File.separator + ".database";
		}
		catch (IllegalStateException e) {
			database = this.getClass()
                           .getClassLoader().getResource(".").toString()
                           + ".database";
		}

		server.setDatabaseName(0, "localServer");
        server.setDatabasePath(0, database);
        server.setLogWriter( new PrintWriter(System.out) );
        server.setErrWriter( new PrintWriter(System.err) );
        server.start();
	}
	
	/**
	 * Stops the Hsqldb-server
	 * 
	 * @throws ClassNotFoundException
	 */
	public void stopHsqldbServer() {

		try {
			Class.forName("org.hsqldb.jdbcDriver");
			String url = "jdbc:hsqldb:hsql://127.0.0.1";
			Connection con;
			
			con = DriverManager.getConnection(url);
		
			String sql = "SHUTDOWN";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} 
		catch (SQLException e) {
			throw new IllegalArgumentException("Could not perform shutdown statement", e);
		} 
		catch (ClassNotFoundException e) {
			throw new IllegalStateException("Could not find the jdbcDriver class", e);
		}
		// no need to close a dead connection!
		Activator.getDefault().setHsqldbServer(null);
	}
	
	public void addDatabase(String name) {
		
		//This is a hack to circumvent a strange deadlock problem. 
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		server.stop();
		try {
			while(true) {
				server.checkRunning(true);
				Thread.sleep(100);
			}
		}
		catch (RuntimeException e) {
			//now the server is dead
		} catch (InterruptedException e) {
			LogUtils.debugTrace(logger, e);
		}
		names.put(nextFreePos,   name);
		paths.put(nextFreePos++, fileFolder + name);
		
		server = new Server();
		server.setLogWriter(null);
        server.setErrWriter(null);
        
		for( int key : names.keySet() ) {
			server.setDatabasePath( key, paths.get(key) );
			server.setDatabaseName( key, names.get(key) );
		}

		server.start();
	}
}

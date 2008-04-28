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
		f.mkdir();
		logger.debug("created directory: " + f + " for storing databasefile");
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
		
		String database = fileFolder + "local.hsqldb";

		server.setDatabaseName(0, "localServer");
        server.setDatabasePath(0, database);
        server.setLogWriter( new PrintWriter(System.out) );
        server.setErrWriter( new PrintWriter(System.err) );
        Thread serverThread = new Thread() {
        	public void run() {
        		server.start();
        	}
        };
        serverThread.run();
        waitFor(ServerConstants.SERVER_STATE_ONLINE);
        logger.debug("HSQLDB server online");
	}
	
	private void waitFor(int serverConstant) {
		long waited = 0;
		long sleepTime = 500;
		do {
			if( server.getState() == serverConstant ) {
				return;
			}
			try {
				System.out.println("The time is now " + waited + ", " + Thread.currentThread().hashCode());
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			waited += sleepTime;
		}
		while( waited < 5000 ); 
		throw new RuntimeException( "Waited too long for server to get " +
				                    "to state: " + serverConstant );
	}
	
	/**
	 * Stops the Hsqldb-server
	 * 
	 * @throws ClassNotFoundException
	 */
	public void stopHsqldbServer() {

		try {
			Class.forName("org.hsqldb.jdbcDriver");
			String url = "jdbc:hsqldb:hsql://127.0.0.1/localServer";
			Connection con;
			
			con = DriverManager.getConnection(url);
		
			String sql = "SHUTDOWN";
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} 
		catch (SQLException e) {
			throw new IllegalStateException(
			        "Could not perform shutdown statement", e);
		} 
		catch (ClassNotFoundException e) {
			throw new IllegalStateException(
			        "Could not find the jdbcDriver class", e);
		}
		// no need to close a dead connection!
		try {
			Activator.getDefault().setHsqldbServer(null);
		}
		catch (Error e) {
			LogUtils.debugTrace(logger, e);
		}
	}
	
	public void addDatabase(String name) {
		
		stopHsqldbServer();
		server.shutdown();
		waitFor(ServerConstants.SERVER_STATE_SHUTDOWN);
		
		String path = fileFolder + name;
		
		names.put(nextFreePos, name);
		paths.put(nextFreePos, path);
		
		server.setDatabasePath( nextFreePos,   path );
		server.setDatabaseName( nextFreePos++, name );
		
		logger.debug( "Database named " + name 
				      + " is gonna be saved at " + path );
		
		Thread thread = new Thread() {
			public void run() {
				server.start();
			}
		};
		thread.start();
		waitFor(ServerConstants.SERVER_STATE_ONLINE);
		logger.debug("HSQLDB server online");
	}
}

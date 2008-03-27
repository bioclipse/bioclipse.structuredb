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
package net.bioclipse.structuredb.persistency;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import net.bioclipse.structuredb.persistency.tables.TableCreator;

import org.hsqldb.Server;

public class HsqldbTestServerManager {

	private Thread server;
	
	public static final HsqldbTestServerManager INSTANCE = new HsqldbTestServerManager();
	
	private HsqldbTestServerManager() {
	}
	
	public boolean serverIsAlive() {
		return server != null && server.isAlive();
	}
	
	public void startServer() {
		
		if( serverIsAlive() ) {
			return;
		}

		String database = this.getClass().getClassLoader().getResource(".").toString();
		System.out.println("Gonna create a database at: \'" + database + "\'...");
		
		Server server = new Server();
        server.setDatabaseName(0, "testServer");
        server.setDatabasePath(0, database);
        server.setLogWriter(null);
        server.setErrWriter(null);
        server.start();
	}

	public void setupTestEnvironment() {
		TableCreator.INSTANCE.createTables("jdbc:hsqldb:hsql://127.0.0.1/testServer");
	}
}

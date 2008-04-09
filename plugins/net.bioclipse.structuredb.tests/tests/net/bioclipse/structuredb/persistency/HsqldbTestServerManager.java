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

import net.bioclipse.structuredb.persistency.tables.TableCreator;

import org.hsqldb.Server;
import org.hsqldb.ServerConstants;

public class HsqldbTestServerManager {

	private Server server;
	
	public static final HsqldbTestServerManager INSTANCE = new HsqldbTestServerManager();
	
	private HsqldbTestServerManager() {
	}
	
	public boolean serverIsAlive() {
		return server != null && 
			   server.getState() == ServerConstants.SERVER_STATE_ONLINE;
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
	
	public void stopServer() {
		server.stop();
	}

	public void setupTestEnvironment() {
		TableCreator.INSTANCE.createTables("jdbc:hsqldb:hsql://127.0.0.1/testServer");
		
	}
}

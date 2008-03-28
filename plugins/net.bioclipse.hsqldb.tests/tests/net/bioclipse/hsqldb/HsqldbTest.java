package net.bioclipse.hsqldb;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class HsqldbTest {

	@Test
	public void testCreatingExtraDatabases() {
		String database = this.getClass().getClassLoader().getResource(".").toString();
//		HsqldbUtil.getInstance().startHsqldbServer();
		HsqldbUtil.getInstance().addDatabase(database, "testDatabase");
		
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		Connection con = getConnection("jdbc:hsqldb:hsql://127.0.0.1/testDatabase");
		assertNotNull(con);
		HsqldbUtil.getInstance().addDatabase(database, "testDatabase2");
		Connection con1 = getConnection("jdbc:hsqldb:hsql://127.0.0.1/testDatabase");
		Connection con2 = getConnection("jdbc:hsqldb:hsql://127.0.0.1/testDatabase2");
		assertNotNull(con1);
		assertNotNull(con2);
	}
	
	private Connection getConnection(String url) {
		Connection conn = null;
		boolean gotConnection = false;
		int slept = 0;
		Exception exception = null;
		while(!gotConnection && slept < 5000) {
			try {
				conn = DriverManager.getConnection(url);
				gotConnection = true;
			} catch (SQLException e) {
				exception = e;
				try {
					int sleepTime = 100;
					Thread.sleep(sleepTime);
					slept += sleepTime;
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		if( !gotConnection ) {
			throw new RuntimeException("Could not get connection to database", exception);
		}
		return conn;
	}
}

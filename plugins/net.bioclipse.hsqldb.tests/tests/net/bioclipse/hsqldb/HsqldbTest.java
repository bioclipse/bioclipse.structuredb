package net.bioclipse.hsqldb;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import net.bioclipse.core.util.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author jonalv
 *
 */
public class HsqldbTest {

    private static final Logger logger = Logger.getLogger(HsqldbTest.class);

    @Test
    public void testGettingConnection() {

    	HsqldbUtil.getInstance().startHsqldbServer();
    	Connection con 
			= getConnection("jdbc:hsqldb:hsql://127.0.0.1/localServer");
    	assertNotNull(con);
    }
    
    @Test
	public void testCreatingExtraDatabases() {

    	testGettingConnection();
    	
		String database = this.getClass()
		                      .getClassLoader().getResource(".").toString();

		HsqldbUtil.getInstance().addDatabase(database, "testDatabase");
		HsqldbUtil.getInstance().addDatabase(database, "testDatabase2");
		Connection con1 
			= getConnection("jdbc:hsqldb:hsql://127.0.0.1/testDatabase");
		Connection con2 
			= getConnection("jdbc:hsqldb:hsql://127.0.0.1/testDatabase2");
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
					LogUtils.debugTrace(logger, e1);
				}
			}
		}
		if( !gotConnection ) {
			throw new RuntimeException( "Could not get connection to database", 
					                    exception );
		}
		return conn;
	}
}

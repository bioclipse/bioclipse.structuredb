/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
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
    public void testCreatingExtraDatabases() {
        String url1
            = HsqldbUtil.getInstance().getConnectionUrl("testDatabase");
        String url2
            = HsqldbUtil.getInstance().getConnectionUrl("testDatabase2");
        Connection con1 = getConnection(url1);
        Connection con2 = getConnection(url2);
        assertNotNull(con1);
        assertNotNull(con2);
        HsqldbUtil.getInstance().stopAllDatabaseInstances();
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

/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistency.tables;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;

/**
 * @author jonalv
 *
 */
public class TableCreator {
    
    private static final Logger logger 
        = Logger.getLogger(TableCreator.class);

    public static final String[] SQL_FILES_RUNORDER 
        = { 
            "BaseObject.sql", 
            "User.sql",
            "Annotation.sql",
            "DBMolecule.sql",
            "DBMoleculeAnnotation.sql",
            "ChoiceAnnotation.sql",
            "RealNumberAnnotation.sql",
            "TextAnnotation.sql",
            "TextProperty.sql",
            "RealNumberProperty.sql",
            "ChoiceProperty.sql",
            "PropertyChoice.sql",
          };

    public static final TableCreator INSTANCE = new TableCreator();
    
    private List<String> createTableStatements = new ArrayList<String>();
    private List<String> alterTableStatements  = new ArrayList<String>();
    
    private TableCreator() {
        
    }
    
    public void createTables(String url) {
        createTableStatements.clear();
        alterTableStatements.clear();
        try {
            
            Class.forName("org.hsqldb.jdbcDriver");
            
            Connection con = getConnection(url);
            
            for ( String sqlFile : SQL_FILES_RUNORDER ) {
                Scanner scanner 
                    = new Scanner( this.getClass().getResourceAsStream(
                        "/net/bioclipse/structuredb/persistency/tables"
                        + File.separator + sqlFile ) );
                while ( scanner.hasNextLine() ) {
                    String sql = readStatement(scanner);
                    if ( sql.matches("^CREATE.*$") ) {
                        createTableStatements.add(sql);
                    }
                    else if( sql.matches("^ALTER.*$") ) {
                        alterTableStatements.add(sql);
                    }
                    else if( sql.matches("^--.*$") ) {
                        //ignore commented lines
                    }
                    else {
                        throw new RuntimeException(
                            "Unknown sql commando neither create " +
                            "nor alter:" + sql);
                    }
                }
            }
            for( String createStatement : createTableStatements ) {
                try {
                    String dropStatement 
                        = createDropStatement(createStatement);
                    runStatement( con, dropStatement );
                }
                catch( Exception e) {
                    continue;
                }
            }
            for( String createStatement : createTableStatements ) {
                runStatement( con, createStatement );
            }
            for( String alterStatement : alterTableStatements ) {
                runStatement( con, alterStatement );
            }
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                "Could not find the jdbcDriver class", e);
        }
    }

    private String createDropStatement(String createStatement) {
        String result = "DROP TABLE";
        Pattern regexp = Pattern.compile("^CREATE\\s+CACHED\\s+TABLE\\s+(\\w+)");
        Matcher m = regexp.matcher(createStatement);
        if( !m.find() ) {
            throw new IllegalArgumentException(
                "Did not find a create table statement");
        }
        result = result + " " + m.group(1) + " IF EXISTS CASCADE;";
        return result;
    }

    private String readStatement(Scanner scanner) {
        StringBuilder result = new StringBuilder();
        String line;
        Pattern p = Pattern.compile(".*;$");
        Matcher m;
        do {
            line = scanner.nextLine();
            result.append(line);
            m = p.matcher(line);
        }
        while( !m.find() );  //<== Still doesn't work
        return result.toString();
    }

    private void runStatement(Connection con, String statement) {
        try {
            Statement stmt = con.createStatement();
            System.out.println(statement);
            stmt.executeUpdate(statement);
            stmt.close();
        } catch (SQLException e) {
            LogUtils.debugTrace( logger, e );
            throw new RuntimeException("error running statement", e);
        }
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
            throw new RuntimeException( 
                "Could not get connection to database", 
                exception );
        }
        return conn;
    }
}

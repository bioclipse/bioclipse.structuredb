/*******************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.filestore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.UUID;


/**
 * @author jonalv
 *
 */
public class FileStore {

    private File root;
    public final static int RECURSION_DEPTH = 8;
    
    /**
     * Create a <code>FileStore</code> using the given directory as root for 
     * saving given fileContents
     * 
     * @param directory
     */
    public FileStore(File directory) {
        if ( !directory.isDirectory() ) {
            throw new IllegalArgumentException( 
                          directory + " is not a directory" );
        }
        root = directory;
    }
    
    /**
     * Store a <code>CharSequence</code> as a file using the store
     * 
     * @param fileContent
     * @return an unique key used for retrieving again
     */
    public UUID store(CharSequence fileContent) {
        if ( fileContent == null ) {
            throw new IllegalArgumentException("fileContent can not be null");
        }
        UUID key = UUID.randomUUID();
        
        store(key, fileContent);
 
        return key;
    }

    /**
     * @param key
     * @param fileContent
     */
    private void store( UUID key, CharSequence fileContent ) {

        String keyString = key.toString();
        
        StringBuffer directoryString = new StringBuffer();
        directoryString.append( root.getPath() );
        for ( int i = 0 ; i < RECURSION_DEPTH ; i++ ) {
            directoryString.append( File.separatorChar  );
            directoryString.append( keyString.charAt(i) );
            
            File directory = new File( directoryString.toString() );
            if ( !directory.exists() ) {
                directory.mkdir();
            }
        }
        
        File file = new File( directoryString.toString() + File.separatorChar
                              + keyString + ".txt" );
        
        BufferedWriter writer = null;
        try {
            file.createNewFile();
            writer = new BufferedWriter( new FileWriter(file) );
            writer.append(fileContent);
        }
        catch ( IOException e ) {
            throw new IllegalStateException("Could not write to file", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch ( IOException e ) {
                    throw new IllegalStateException("Could not close file");
                }
            }
        }        
    }

    /**
     * @param key
     * @return a <code>CharBuffer</code> equal to the <code>CharSequence</code> 
     * that was associated with the given key using the <code>store</code> 
     * method.
     */
    public InputStream retrieve( UUID key ) {
        File file = locateFile( key );
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        }
        catch ( FileNotFoundException e ) {
            throw new IllegalArgumentException( 
                "No file content is associated with the given key", e );
        }
        return inputStream;
    }

    /**
     * @param key
     * @return
     */
    private File locateFile( UUID key ) {

        String keyString = key.toString();

        StringBuffer directoryString = new StringBuffer();
        directoryString.append( root.getPath() );
        for ( int i = 0 ; i < RECURSION_DEPTH ; i++ ) {
            directoryString.append( File.separatorChar  );
            directoryString.append( keyString.charAt(i) );
        }

        return new File( directoryString.toString() + File.separatorChar
                         + keyString + ".txt" );
    }

    /**
     * @return
     */
    public File getRootFolder() {
        return root;
    }

    /**
     * @param key
     */
    public void delete( UUID key ) {

        File file = locateFile( key );
        file.delete();
    }

    /**
     * Does a delete followed by a store using the old key.
     * 
     * @param key
     * @param string
     */
    public void update( UUID key, CharSequence newContent ) {
        delete(key);
        store(key, newContent);
    }
}

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
        String keyString = key.toString();
        
        String directoryString = keyString.charAt( 0 ) + "";
        File directory = new File( root.getPath() + File.separatorChar 
                                   + directoryString );
        if ( !directory.exists() ) {
            directory.mkdir();
        }
        File file = new File( root.getPath() + File.separatorChar
                              + directoryString + File.separatorChar
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
        return key;
    }

    /**
     * @param key
     * @return a <code>CharBuffer</code> equal to the <code>CharSequence</code> 
     * that was associated with the given key using the <code>store</code> 
     * method.
     */
    public InputStream retrieve( UUID key ) {
        String keyString = key.toString();
        String directoryString = keyString.charAt( 0 ) + "";
        File file = new File( root.getPath() + File.separatorChar
                              + directoryString + File.separatorChar
                              + keyString + ".txt" );
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
}

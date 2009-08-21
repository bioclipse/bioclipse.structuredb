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

import java.io.File;
import java.nio.CharBuffer;
import java.util.UUID;


/**
 * @author jonalv
 *
 */
public class FileStore {

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
        return UUID.randomUUID();
    }

    /**
     * @param key
     * @return a <code>CharBuffer</code> equal to the <code>CharSequence</code> 
     * that was connected to the given key using the <code>store</code> method.
     */
    public CharBuffer retrieve( UUID key ) {


        return null;
    }
}

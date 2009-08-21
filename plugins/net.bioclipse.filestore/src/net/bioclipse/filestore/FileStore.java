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
     * @param directory
     */
    public FileStore(File directory) {
        if ( !directory.isDirectory() ) {
            throw new IllegalArgumentException( 
                          directory + " is not a directory" );
        }
    }
    
    public UUID store(CharSequence fileContent) {
        if ( fileContent == null ) {
            throw new IllegalArgumentException("fileContent can not be null");
        }
        return UUID.randomUUID();
    }
}

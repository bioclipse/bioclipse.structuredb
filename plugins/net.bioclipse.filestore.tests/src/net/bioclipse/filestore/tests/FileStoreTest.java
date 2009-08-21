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
package net.bioclipse.filestore.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;

import net.bioclipse.filestore.FileStore;

/**
 * @author jonalv
 *
 */
public class FileStoreTest {

    private FileStore fs;
    
    @Test( expected = IllegalArgumentException.class )
    public void dontInitializeWithFile() throws URISyntaxException {
        new FileStore( 
            new File( this.getClass()
                          .getClassLoader()
                          .getResource( "./testFolder/testfile.txt" )
                          .toURI() ) );
    }
    
    @Test
    public void doInializeWithDirectory() throws URISyntaxException {
        fs = new FileStore( 
                 new File( this.getClass()
                               .getClassLoader()
                               .getResource( "./testFolder" )
                               .toURI() ) );
        assertNotNull(fs);
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void dontStoreNull() throws URISyntaxException {
        doInializeWithDirectory();
        fs.store( null );
    }
    
    @Test
    public void doStoreString() throws URISyntaxException {
        doInializeWithDirectory();
        UUID key = fs.store( "example String" );
        assertNotNull( key );
    }
}

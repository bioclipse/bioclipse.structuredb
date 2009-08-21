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
import java.nio.CharBuffer;
import java.util.UUID;

import org.junit.Test;

import net.bioclipse.filestore.FileStore;

/**
 * @author jonalv
 *
 */
public class FileStoreTest {

    private FileStore fs;
    private UUID key;
    
    public static final String EXAMPLE_STRING = "example String";
    
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
    public void doStoreExampleString() throws URISyntaxException {
        doInializeWithDirectory();
        key = fs.store( EXAMPLE_STRING );
        assertNotNull( key );
    }
    
    @Test
    public void doRetrieveStored() throws URISyntaxException {
        doStoreExampleString();
        CharBuffer retrieved = fs.retrieve(key);
        assertEquals( EXAMPLE_STRING, retrieved.toString() );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void dontRetrieveNotStored() throws URISyntaxException {
        doStoreExampleString();
        UUID unstoredKey = UUID.randomUUID();
        fs.retrieve( unstoredKey );
    }
}

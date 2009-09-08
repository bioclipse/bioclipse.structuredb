/*******************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb;

import java.io.File;

import net.bioclipse.filestore.FileStore;
import net.bioclipse.hsqldb.HsqldbUtil;


/**
 * @author jonalv
 *
 */
public abstract class FileStoreKeeper {

    public static final FileStore FILE_STORE;
    
    static {
        String moleculesPath = HsqldbUtil.getInstance()
                                         .getDatabaseFilesDirectory()
                                         .getAbsoluteFile() 
                                         + File.pathSeparator + "molecules";
        File moleculesDirectory = new File(moleculesPath);
        if ( !moleculesDirectory.exists() ) {
            moleculesDirectory.mkdir();
        }
        else {
            if ( moleculesDirectory.isFile() ) {
                throw new IllegalStateException(
                    "Can not create directory for storing molecules. A file " +
                    moleculesDirectory + " is in the way." );
            }
        }
        FILE_STORE = new FileStore(moleculesDirectory);
    }
    
    private FileStoreKeeper() {
        
    }
}

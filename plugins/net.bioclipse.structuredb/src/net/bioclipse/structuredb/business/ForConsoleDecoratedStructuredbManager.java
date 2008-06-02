/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.business;

import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.recording.JsScriptGenerator;
import net.bioclipse.structuredb.domain.Label;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.ui.views.JsConsoleView;

import org.eclipse.core.runtime.IProgressMonitor;

import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;


/**
 * Redirects calls to methods without a progressmonitor to methods with 
 * progressmonitors where appropriate, using a JSConsole specific 
 * progressmonitor.    
 * 
 * @author jonalv
 *
 */
public class ForConsoleDecoratedStructuredbManager implements
             IStructuredbManager {

    private IStructuredbManager manager;
    
    public ForConsoleDecoratedStructuredbManager( 
        IStructuredbManager toBeDecorated) {
        this.manager = toBeDecorated;
    }
    
    public void addStructuresFromSDF( String databaseName, String filePath )
                throws BioclipseException {

        addStructuresFromSDF( databaseName, 
                              filePath, 
                              new JsConsoleView.ConsoleProgressMonitor() );
    }

    public void addStructuresFromSDF( String databaseName, 
                                      String filePath,
                                      IProgressMonitor monitor )
                throws BioclipseException {

        manager.addStructuresFromSDF( databaseName, filePath, monitor );
    }

    public List<Label> allLabels( String databaseName ) {
        return manager.allLabels( databaseName );
    }

    public List<Structure> allStructures( String databaseName ) {
        return manager.allStructures( databaseName );
    }

    public List<Structure> allStructuresByName( String databaseName,
                                                String structureName ) {
        return manager.allStructuresByName( databaseName, structureName );
    }

    public List<User> allUsers( String databaseName ) {
        return manager.allUsers( databaseName );
    }

    public Label createLabel( String databaseName, String folderName )
                  throws IllegalArgumentException {
        return manager.createLabel( databaseName, folderName );
    }

    public void createLocalInstance( String databaseName )
                throws IllegalArgumentException {
        manager.createLocalInstance( databaseName );
    }

    public Structure createStructure( String databaseName, 
                                      String moleculeName,
                                      ICDKMolecule cdkMolecule )
                     throws BioclipseException {
        return manager.createStructure( databaseName, 
                                        moleculeName, 
                                        cdkMolecule );
    }

    public User createUser( String databaseName, String username,
                            String password, boolean sudoer )
                throws IllegalArgumentException {
        return manager.createUser( databaseName, username, password, sudoer );
    }

    public Label labelByName( String databaseName, String folderName ) {
        return manager.labelByName( databaseName, folderName );
    }

    public List<String> listDatabaseNames() {
        return manager.listDatabaseNames();
    }

    public void removeLocalInstance( String databaseName ) {
        manager.removeLocalInstance( databaseName );
    }

    public User userByName( String databaseName, String username ) {
        return manager.userByName( databaseName, username );
    }

    public String getNamespace() {
        return manager.getNamespace();
    }

    public Iterator<Structure> subStructureSearchIterator( String databaseName,
                                                           IMolecule molecule )
                               throws BioclipseException {
        return manager.subStructureSearchIterator( databaseName, molecule );
    }

    public ICDKMolecule toCDKMolecule( Structure structure ) {

        return manager.toCDKMolecule( structure );
    }

    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule )
                           throws BioclipseException {
        
        return manager.subStructureSearch( databaseName, 
                                           molecule, 
                                           new JsConsoleView
                                               .ConsoleProgressMonitor() );
    }

    public List<Structure> subStructureSearch( String databaseName,
                                               IMolecule molecule,
                                               IProgressMonitor monitor )
                           throws BioclipseException {

        return manager.subStructureSearch( databaseName, molecule, monitor );
    }

    public void delete( String database, Label label ) {

        manager.delete( database, label );
    }

    public void delete( String database, Structure structure ) {

        manager.delete( database, structure );
    }

    public void save( String database, Structure structure ) {

        manager.save( database, structure );
    }

    public void save( String database, Label label ) {

        manager.save( database, label );
    }
}

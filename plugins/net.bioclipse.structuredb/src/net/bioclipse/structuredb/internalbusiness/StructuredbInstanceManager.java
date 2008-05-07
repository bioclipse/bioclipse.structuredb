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
package net.bioclipse.structuredb.internalbusiness;

import java.util.List;

import net.bioclipse.core.domain.BioList;
import net.bioclipse.structuredb.domain.Folder;
import net.bioclipse.structuredb.domain.Structure;
import net.bioclipse.structuredb.domain.User;

public class StructuredbInstanceManager 
       extends AbstractStructuredbInstanceManager 
       implements IStructuredbInstanceManager {

    private User loggedInUser;
    
    public Folder createLibrary(String name) {
        Folder folder = new Folder(name);
        folderDao.insert(folder);
        return folder;
    }

    public void insertFolder(Folder folder) {
        folderDao.insert(folder);
        persistRelatedStructures(folder);
    }
    
    private void persistRelatedStructures(Folder folder) {
        for( Structure s : folder.getStructures() ) {
            if( structureDao.getById(s.getId()) == null) {
                structureDao.insert(s);
            }
            else {
                structureDao.update(s);
            }
        }
    }

    public void insertStructure(Structure structure) {
        structureDao.insert(structure);
    }

    public void insertUser(User user) {
        userDao.insert(user);
    }

    public void delete(Folder folder) {
        folderDao.delete( folder.getId() );
    }

    public void delete(User user) {
        userDao.delete( user.getId() );
    }

    public void delete(Structure structure) {
        structureDao.delete( structure.getId() );
    }

    public List<Folder> retrieveAllFolders() {
        return new BioList<Folder>( folderDao.getAll() );
    }

    public List<Structure> retrieveAllStructures() {
        return new BioList<Structure>( structureDao.getAll() );
    }

    public List<User> retrieveAllUsers() {
        return new BioList<User>( userDao.getAll() );
    }

    public Folder retrieveFolderByName(String name) {
        return folderDao.getByName(name);
    }

    public List<Structure> retrieveStructureByName(String name) {
        return structureDao.getByName(name);
    }

    public User retrieveUserByUsername(String username) {
        return userDao.getByUserName(username);
    }

    public void update(Folder folder) {
        folderDao.update(folder);
        persistRelatedStructures(folder);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void update(Structure structure) {
        structureDao.update(structure);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }
}

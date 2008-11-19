/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Alvarsson
 *     
 *******************************************************************************/

package net.bioclipse.structuredb.domain;

import java.util.HashSet;
import java.util.Set;

import net.bioclipse.encryption.EncryptedPassword;

/**
 * A representation of a User in the database system
 * 
 * @author jonalv
 *
 */
public class User extends BaseObject {

    private String  userName;
    private boolean sudoer;
    private Set<BaseObject> createdBaseObjects;
    
    private EncryptedPassword encryptedPassword; 
    
    public User() {
        super();
        userName           = "user" + getId();
        encryptedPassword  = EncryptedPassword.fromPlaintextPassword( "" );
        createdBaseObjects = new HashSet<BaseObject>();
    }

    public User(String userName, String password, boolean sudoer) {
        super();
        this.userName      = userName;
        this.sudoer        = sudoer;
        encryptedPassword  = EncryptedPassword.fromPlaintextPassword(password);
        createdBaseObjects = new HashSet<BaseObject>();
    }

    /**
     * Creates a new User that is an exact copy of the 
     * given instance including the same id.
     * 
     * @param user
     */
    public User(User user) {
        super(user);
        
        this.userName = user.getUserName();
        this.encryptedPassword = EncryptedPassword
                                     .fromAlreadyEncryptedPassword( 
                                         user.getPassWordMd5() );
        this.sudoer = user.isSudoer();
    }
    
    public boolean hasValuesEqualTo( BaseObject object ) {
        if( !super.hasValuesEqualTo(object) ) {
            return false;
        }
        if( !(object instanceof User) ) {
            return false;
        }
        
        User user = (User)object;
        
        return userName.equals( user.getUserName() ) 
            && encryptedPassword.toString().equals( user.getPassWordMd5() )
            && sudoer == user.isSudoer();
    }
    
    /**
     * Checks whether a password matches the users password
     * 
     * @param password to check
     * @return whether the password matches
     */
    public boolean passWordMatches( String password ) {
        return encryptedPassword.matches( password );
    }

    /**
     * @return the user's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the username to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the md5 encrypted password
     */
    public String getPassWordMd5() {
        return encryptedPassword.toString();
    }

    /**
     * @param passWordMd5 md5 encrypted password to set
     */
    public void setPassWordMd5(String passWordMd5) {
        encryptedPassword = EncryptedPassword
                                .fromAlreadyEncryptedPassword( passWordMd5 );
    }

    /**
     * @return whether the user is sudoer 
     */
    public boolean isSudoer() {
        return sudoer;
    }

    /**
     * @param sudoer whether the user is sudoer to set
     */
    public void setSudoer(boolean sudoer) {
        this.sudoer = sudoer;
    }
    
    /**
     * @param password the unencrypted password to be md5 encrypted 
     * and set
     */
    public void setPassWord(String password) {
        encryptedPassword = EncryptedPassword.fromPlaintextPassword(password);
    }

    /**
     * @return the objects created by this user
     */
    public Set<BaseObject> getCreatedBaseObjects() {
        return createdBaseObjects;
    }

    /**
     * @param createdBaseObjects the objects to set
     */
    public void setCreatedBaseObjects( 
        Set<BaseObject> createdBaseObjects ) {
        
        this.createdBaseObjects = createdBaseObjects;
    }

    /**
     * Adds a new object among this users created objects
     * 
     * @param baseObject the object to add
     */
    public void addCreatedBaseObject(BaseObject baseObject) {
        createdBaseObjects.add(baseObject);
        if( baseObject.getCreator() != this ) {
            baseObject.setCreator(this);
        }
    }

    /**
     * Removes an object from a user created objects set
     * 
     * @param baseObject object to remove
     */
    public void removeCreatedBaseObject(BaseObject baseObject) {
        createdBaseObjects.remove(baseObject);
        if( baseObject.getCreator() != null ) {
            baseObject.setCreator(null);
        }
    }
}

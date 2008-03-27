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

package net.bioclipse.structuredb.domain;

import java.util.HashSet;
import java.util.Set;

import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 * A representation of a User in the database system
 * 
 * @author jonalv
 *
 */
public class User extends BaseObject {

	private String  userName;
	private String  passWordMd5;
	private boolean sudoer;
	private Set<BaseObject> createdBaseObjects;
	
	private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
	
	public User() {
		super();
		userName           = "";
		passWordMd5        = passwordEncryptor.encryptPassword("");
		createdBaseObjects = new HashSet<BaseObject>();
	}

	public User(String name, String userName, String password) {
		super(name);
		this.userName      = userName;
		passWordMd5        = passwordEncryptor.encryptPassword(password);
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
		
		this.userName    = user.getUserName();
		this.passWordMd5 = user.getPassWordMd5();
		this.sudoer      = user.isSudoer();
	}
	
	/* (non-Javadoc)
	 * @see net.bioclipse.structuredb.domain.BaseObject#hasValuesEqualTo(net.bioclipse.structuredb.domain.BaseObject)
	 */
	public boolean hasValuesEqualTo( BaseObject object ) {
		if( !super.hasValuesEqualTo(object) ) {
			return false;
		}
		if( !(object instanceof User) ) {
			return false;
		}
		
		User user = (User)object;
		
		return userName.equals( user.getUserName() ) 
		    && passWordMd5.equals( user.getPassWordMd5() )
		    && sudoer == user.isSudoer();
	}
	
	/**
	 * Checks whether a password matches the users password
	 * 
	 * @param password to check
	 * @return whether the password matches
	 */
	public boolean passWordMatches( String password ) {
		return passwordEncryptor.checkPassword( password, passWordMd5 );
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
		return passWordMd5;
	}

	/**
	 * @param passWordMd5 md5 encrypted password to set
	 */
	public void setPassWordMd5(String passWordMd5) {
		this.passWordMd5 = passWordMd5;
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
	 * @param password the unencrypted password to be md5 encrypted and set
	 */
	public void setPassWord(String password) {
		passWordMd5 = passwordEncryptor.encryptPassword(password);
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
	public void setCreatedBaseObjects(Set<BaseObject> createdBaseObjects) {
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

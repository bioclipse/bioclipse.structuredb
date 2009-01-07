/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import net.bioclipse.structuredb.domain.User;
import net.bioclipse.usermanager.UserManagerEvent;
import net.bioclipse.usermanager.business.IUserManager;

/**
 * @author jonalv
 *
 */
public class LoggedInUserKeeper implements ILoggedInUserKeeper {

    private User         loggedInUser;
    private IUserManager userManager;
    
    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void receiveUserManagerEvent(UserManagerEvent event) {
        //TODO: implement receiveUserManagerEvent (need to do 
        //      the strucutredb 
        //      usermanager account type first) 
        throw new RuntimeException("Not implemented yet");
//        switch (event) {
//        case LOGIN:
//            
//            break;
//        case LOGOUT:
//            
//            break;
//        case UPDATE:
//            break;
//        default:
//            break;
//        }
    }
    
    /**
     * method used for testing purposes
     * 
     * @param loggedInUser
     */
    public void setLoggedInUser( User loggedInUser ) {
        this.loggedInUser = loggedInUser;
    }
    
    public void setUserManager( IUserManager userManager ) {
        this.userManager = userManager;
        userManager.addListener(this);
    }
}

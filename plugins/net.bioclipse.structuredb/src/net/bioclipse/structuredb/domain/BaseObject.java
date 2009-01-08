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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.core.domain.IBioObject;

/**
 * A baseclass for all domain objects. 
 * It creates a unique id for the object and contains code for comparisons 
 * between two domain objects.
 * 
 * @author jonalv
 */
public class BaseObject implements IBioObject, Comparable {

    private String id; //36 chars long unique id 
    
    private User creator;
    private User lastEditor;
    
    private Timestamp created;
    private Timestamp edited;
    
    public BaseObject() {
        super();
        this.id = UUID.randomUUID().toString();
    }
    
    /**
     * Creates a new BaseObject that is an exact copy of the 
     * given instance with the same id.
     * 
     * @param obj
     */
    public BaseObject( BaseObject obj ) {
        super();
        this.id         = obj.id;
        this.creator    = obj.getCreator();
        this.lastEditor = obj.getLastEditor();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to be set
     */
    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     * 
     * Final since the id is a sufficient hashcode.
     */
    @Override
    public final int hashCode() {
        return id.hashCode(); 
    }

    /**
     * Checks whether two objects correspond to the same database row.
     * Final since we only want equals to check the id for the domain 
     * objects. This implementation provides an equals method that 
     * doesn't break the equals / hashcode contract and works with 
     * database mapped objects. In order to check whether they 
     * correspond to two identical versions of said row use: 
     * <code>hasValuesEqualTo</code>
     * 
     * @param anObject - the object to compare this String  against.
     * @return true if the domain object refer to the same database row; 
     *         false otherwise.
     */
    @Override
    public final boolean equals(Object obj) {
        if( !(obj instanceof BaseObject) ) {
            return false;
        }
        return id.equals( ( (BaseObject)obj ).getId() );
    }

    /**
     * @param obj a BaseObject to be compared
     * @return whether all fields (including id) of the given object 
     * equals this objects fields
     */
    public boolean hasValuesEqualTo( BaseObject obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( this.creator != null) {
            if ( !this.creator.hasValuesEqualTo(obj.getCreator()) ) {
                return false;
            }
        }
        if ( this.lastEditor != null ) {
            if ( !this.lastEditor.hasValuesEqualTo(
                    obj.getLastEditor()) ) {
                return false;
            }
        }
        return id.equals( obj.getId() );
    }
    
    /**
     * Checks that the objects in both sets are alike and that no 
     * object exists in one set but not in the other.
     * 
     * @param set1
     * @param set2
     * @return whether the sets are alike
     */
    /*
     * This can not be done with equals since it is used for checking id
     */
    protected boolean objectsInHasSameValues( 
        Set<? extends BaseObject> set1, 
        Set<? extends BaseObject> set2 ) {

        if ( set1.size() != set2.size() ) {
            return false;    
        }
        HashMap<String, BaseObject> hash2 
            = new HashMap<String, BaseObject>();
        for ( BaseObject obj : set2) {
            hash2.put(obj.getId(), obj);
        }
        
        for ( BaseObject obj : set1) {
            if ( hash2.get(obj.getId()) == null 
                 || !hash2.get( obj.getId() ).hasValuesEqualTo(obj) ) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks that the objects in both lists are alike and in the 
     * same order.
     * 
     * @param list1
     * @param list2
     * @return whether the sets are alike
     */
    /*
     * This can not be done with equals since it is used for checking id
     */
    @SuppressWarnings("unchecked")
    protected boolean objectsInHasSameValues( 
            List<? extends BaseObject> list1, 
            List<? extends BaseObject> list2 ) {
        
        list1 = new ArrayList<BaseObject>(list1);
        list2 = new ArrayList<BaseObject>(list2);
        
        Collections.sort( list1 );
        Collections.sort( list2 );
        
        if ( list1.size() != list2.size() ) {
            return false;    
        }
    
        for (int i = 0; i < list1.size(); i++) {
            if ( !list2.get( i ).hasValuesEqualTo( list1.get(i) ) ) {
                return false;
            }
        }
        return true;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setLastEditor(User editor) {
        this.lastEditor = editor;
    }

    public User getCreator() {
        return creator;
    }

    public User getLastEditor() {
        return lastEditor;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getEdited() {
        return edited;
    }

    public void setEdited(Timestamp edited) {
        this.edited = edited;
    }

    public IResource getResource() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setResource(IResource resource) {
    	//Do nothing as resource is not used
	}

    public String getUID() {
        return getId();
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        if(adapter.isAssignableFrom(this.getClass() ))
            return this;
        return Platform.getAdapterManager().getAdapter(this, adapter );
    }
    
    public String toString() {
        return "{" + this.getClass().getSimpleName() + "}"; 
    }

    public int compareTo( Object o ) {
        if ( !(o instanceof BaseObject) ) {
            return 0;
        }
        return this.id.compareTo( ( (BaseObject)o ).getId() );
    }
}

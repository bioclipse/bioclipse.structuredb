/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.chemoinformatics.contentlabelproviders;

import java.util.ArrayList;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** 
 * A class implementing ITreeContentProvider and only returning child elements 
 * which are molecule files. This can be used to build TreeViewers for browsing 
 * for molecules.
 *
 */
public class MoleculeFileContentProvider implements ITreeContentProvider {

    private static final Logger logger 
        = Logger.getLogger(MoleculeFileContentProvider.class);

    public MoleculeFileContentProvider() {
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    public Object[] getChildren(Object parentElement) {

        ArrayList<IResource> childElements = new ArrayList<IResource>();
        
        if ( parentElement instanceof IContainer 
             && ( (IContainer)parentElement ).isAccessible() ) {
            
            IContainer container = (IContainer)parentElement;
            try {
                for ( int i=0 ; i < container.members().length ; i++ ) {
                    IResource resource = container.members()[i];
                    if ( resource instanceof IFile  ) {
                        String fileExtension 
                            = ( (IFile)resource ).getFileExtension();
                        if ( fileExtension != null 
                             && ( fileExtension.equals(ICDKManager.mol) 
                               || fileExtension.equals(ICDKManager.cml) ) ) {
                               
                               childElements.add(resource);
                           }
                    }
                    if ( resource instanceof IContainer 
                         && resource.isAccessible() ) {
                        childElements.add(resource);
                    }
                }
            } 
            catch (CoreException e) {
                LogUtils.handleException(e,logger);
            }
        }
        return childElements.toArray();
    }

    public Object getParent(Object element) {
        return ( (IFolder)element ).getParent();
    }

    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }
}
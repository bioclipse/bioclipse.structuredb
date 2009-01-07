/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;


/**
 * @author jonalv
 *
 */
public class ResourcePathTransformer {

    private static ResourcePathTransformer instance = 
        new ResourcePathTransformer();
    
    private ResourcePathTransformer() {
        
    }
    
    public static ResourcePathTransformer getInstance() {
        return instance;
    }
    
    public IFile transform(String resourceString) {
  
        IFile result;
        result = parseRelative(resourceString);
        if (result == null) result = parseURI(resourceString);
        if (result == null) result = parsePath(resourceString);
        if (result == null) throw new IllegalArgumentException(
                            "Could not handle " + resourceString );
        return result;
    }

    private IFile parsePath( String resourceString ) {
    	URI uri;
    	java.io.File localFile=new java.io.File(resourceString);
    	if(!localFile.exists()) return null;
    	try{
    		uri=new URI("file:"+localFile.getAbsolutePath());
    	}catch (URISyntaxException e) {
			return null;
		}
    	IProject vProject=Activator.getVirtualProject();
    	IFile vFile=vProject.getFile(localFile.getName());
    	// if file already exist in Virtual project
    	if(vFile.exists()) {
    	    if(vFile.isLinked()) {
    	        if( uri.equals( vFile.getLocationURI()) ) {
    	            try {
                        vFile.refreshLocal( IResource.DEPTH_ONE, null );
                        return vFile;
                    } catch ( CoreException e ) {
                        return null;
                    }
    	        }
    	    }
    	    vFile = createAlternativeFile(vFile);
    	    if( vFile == null)
    	        return null;
    	}    	
    	try {
			vFile.createLink(uri,IResource.NONE, null);	
			vFile.refreshLocal(0, new NullProgressMonitor());
		} catch (CoreException e) {
			return null;
		}
    	return vFile;       
    }
    
    /*
     * Recursive algorithm for creating unique file
     */
    private IFile createAlternativeFile( IFile file ) {
        return createAlternativeFile( file, 0 );
    }
    private IFile createAlternativeFile( IFile file , int count) {
        int MAX_RECURSION = 10;
        if (count > MAX_RECURSION) return null;
        file =file.getParent().getFile(
                       new Path(generateUniqueFileName( file,++count)));
        if(file.exists()) return createAlternativeFile( file, count );        
        
        return file;
    }
    private String generateUniqueFileName(IFile file,int count) {
        // FIXME : this should replace last entered number with new
        String name = file.getName();
        name = name.replaceAll( "\\..*$", Integer.toString( count ));
        name = name + "."+file.getFileExtension();
        return name;
    }
    
    private IFile parseRelative( String resourceString ) {

       IPath path = new Path(resourceString);
       IFile file = ResourcesPlugin.getWorkspace()
                                   .getRoot().getFile( path );
       if ( file.exists() )
           return file;
        return null;
    }

    private IFile parseURI( String resourceString ) {
        try {
            IFile[] files = ResourcesPlugin.getWorkspace()
                                           .getRoot()
                                           .findFilesForLocationURI(
                                               new URI(resourceString) );
            if ( files.length == 1 )
                return files[0];
            
            throw new IllegalStateException( 
                "Multiple IFiles correspond to the uri:" 
                + resourceString);               
        } 
        catch ( URISyntaxException e ) {
            return null; //It wasn't an uri...
        }catch (IllegalArgumentException e){
        	return null;
        }
    }
}

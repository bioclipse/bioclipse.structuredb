package net.bioclipse.cdk.domain;

import java.io.IOException;

import net.bioclipse.core.business.BioclipseException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


public class MoleculesIndexEditorInput implements IFileEditorInput{

    SDFElement element;
    
    public MoleculesIndexEditorInput(SDFElement element) {
        this.element = element;
    }    
   
    
    public IFile getFile() {

        return (IFile) element.getResource();
    }

    public IStorage getStorage() throws CoreException {
        
        return (IFile) element.getResource();
    }

    public boolean exists() {

        return ((IFile) element.getResource()).exists();
    }

    public ImageDescriptor getImageDescriptor() {
        IFile file = (IFile) element.getResource();
        IContentType contentType = IDE.getContentType(file);
        return PlatformUI.getWorkbench().getEditorRegistry()
            .getImageDescriptor(file.getName(), contentType);
    }

    public String getName() {

        //return element.getResource().getName()+": "+element.getNumber();
        return element.getName();
    }

    public IPersistableElement getPersistable() {

        // TODO Auto-generated method stub
        return null;
    }

    public String getToolTipText() {
        IFile file = (IFile) element.getResource();
        int index = element.getNumber();
        return file.getFullPath().makeRelative().toString()+": "+index;
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        if(SDFElement.class.isAssignableFrom( adapter )) {
            return element;
        }
        if(ICDKMolecule.class.isAssignableFrom( adapter )) {
            return element.getAdapter( adapter );
        }
        return Platform.getAdapterManager().getAdapter( this, adapter );
    }


    public String getData() throws CoreException, IOException, BioclipseException{
        if(element.getResource() != null)
            return SDFAdapterFactory.getSDFPart( element );
        else {
            ICDKMolecule mol = (ICDKMolecule)
                                      element.getAdapter( ICDKMolecule.class );
            if(mol != null)
                return mol.getCML();
        }
        return null;
    }
    
    
}

package net.bioclipse.structuredb.viewer;

import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.IStructuredbInstance;
import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.StructuredbFactory;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author jonalv
 *
 */
public class DatabaseContentProvider implements ITreeContentProvider {

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(Object parent) {
        return new Object[] { StructuredbFactory.getStructuredb() };
    }

    public Object[] getChildren(Object parentElement) {
        
        if (parentElement instanceof Structuredb) {
            Structuredb structuredb = (Structuredb)parentElement;
            return structuredb.getChildren().toArray();
        }
        
        if (parentElement instanceof Database) {
            Database instance = (Database) parentElement;
            return instance.getChildren().toArray();
        }
        
        return new Object[0];
    }
    
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean hasChildren(Object element) {
        if (element instanceof Structuredb) {
            return true;
        }
        if ( element instanceof Database ) {
            return true;
        }
        return true;
    }
}

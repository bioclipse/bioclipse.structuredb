package net.bioclipse.structuredb.viewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DatabaseContentProvider implements ITreeContentProvider {
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		return new String[] { "One", "Two", "Three" };
	}
	
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}}

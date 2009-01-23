package net.bioclipse.structuredb.viewer;

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.structuredb.DBMoleculesEditorModel;
import net.bioclipse.structuredb.Database;
import net.bioclipse.structuredb.IStructuredbInstance;
import net.bioclipse.structuredb.Label;
import net.bioclipse.structuredb.Structuredb;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author jonalv
 *
 */
public class DatabaseLabelProvider extends LabelProvider 
                                   implements ITableLabelProvider {

    Map<String, Image> images = new HashMap<String, Image>() {
        private static final long serialVersionUID = 1L;
        {
           put( "db_16", 
                new Image( Display.getDefault(), 
                           DatabaseLabelProvider.class.getResourceAsStream( 
                               "/icons/db_16.gif") ) );
           put( "db_mol", 
                new Image( Display.getDefault(), 
                           DatabaseLabelProvider.class.getResourceAsStream( 
                               "/icons/db_with_mol_16.png") ) );
           put( "molecules",
                new Image( Display.getDefault(),
                           DatabaseLabelProvider.class.getResourceAsStream( 
                               "/icons/many_molecules.png") ) );
        }
    };
    
    public String getColumnText(Object obj, int index) {
        return getText(obj);
    }
    
    public Image getColumnImage(Object obj, int index) {
        return getImage(obj);
    }
    
    public Image getImage(Object obj) {
        if ( obj instanceof Structuredb )
            return images.get( "db_16" );
        if ( obj instanceof Database ) {
            return images.get( "db_mol" );
        }
        if ( obj instanceof Label ) {
            return images.get( "molecules" );
        }
        return PlatformUI.getWorkbench()
                         .getSharedImages()
                         .getImage( ISharedImages.IMG_OBJ_ELEMENT );
    }
    
    @Override
    public void dispose() {
        super.dispose();
        for (Image i : images.values()) {
            i.dispose();
        }
    }
}
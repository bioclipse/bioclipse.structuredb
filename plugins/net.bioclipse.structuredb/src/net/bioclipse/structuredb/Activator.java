/* *****************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/

package net.bioclipse.structuredb;

import java.io.File;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.databases.IDatabasehangeListener;
import net.bioclipse.filestore.FileStore;
import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.business.IStructureDBChangeListener;
import net.bioclipse.structuredb.business.IJavaScriptStructuredbManager;
import net.bioclipse.structuredb.business.IJavaStructuredbManager;
import net.bioclipse.structuredb.viewer.IStructureDBLabelDecoratorChangeListener;
import net.bioclipse.structuredb.viewer.StructureDBLightweightLabelDecorator;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin 
                       implements IStructureDBChangeListener {

    private Logger logger = Logger.getLogger(Activator.class);
    
    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.structuredb";

    // The shared instance
    private static Activator plugin;
    
    private ServiceTracker finderTracker;
    private ServiceTracker jsFinderTracker;
    private ServiceTracker dbChangeListenersTracker;
    private ServiceTracker dbDecoratorChangeTracker;

    private BundleContext bundleContext;

    /**
     * The constructor
     */
    public Activator() {

    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        finderTracker = new ServiceTracker( context, 
                                            IJavaStructuredbManager.class
                                                .getName(), 
                                            null );
        finderTracker.open();
        jsFinderTracker = new ServiceTracker( context,
                                            IJavaScriptStructuredbManager.class
                                                                 .getName(),
                                            null );
        jsFinderTracker.open();
        dbChangeListenersTracker 
            = new ServiceTracker( context,
                                  IDatabasehangeListener.class.getName(),
                                  null );
        dbChangeListenersTracker.open();
        this.bundleContext = context;
        dbDecoratorChangeTracker
            = new ServiceTracker( 
                context,
                IStructureDBLabelDecoratorChangeListener.class.getName(),
                null );
        dbDecoratorChangeTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public IJavaStructuredbManager getStructuredbManager() {
        IJavaStructuredbManager manager = null;
        try {
            manager = (IJavaStructuredbManager) finderTracker.waitForService(1000*30);
        } 
        catch (InterruptedException e) {
            logger.warn("Exception occurred while attempting " +
            		    "to get the StructuredbManager" + e);
            LogUtils.debugTrace(logger, e);
        }
        if (manager == null) {
            throw new IllegalStateException("Could not get the structuredb manager");
        }
        manager.addListener( this );
        return manager;
    }
    
    public IJavaScriptStructuredbManager getJavaScriptStructuredbManager() {
        IJavaScriptStructuredbManager manager = null;
        try {
            manager = (IJavaScriptStructuredbManager) jsFinderTracker.waitForService(1000*30);
        } 
        catch (InterruptedException e) {
            logger.warn("Exception occurred while attempting " +
                    "to get the JSStructuredbManager");
            LogUtils.debugTrace(logger, e);
        }
        if (manager == null) {
            throw new IllegalStateException("Could not get the structuredb manager");
        }
        return manager;
    }

    /* (non-Javadoc)
     * @see net.bioclipse.structuredb.business.IDatabaseListener#onDataBaseUpdate(net.bioclipse.structuredb.business.IDatabaseListener.DatabaseUpdateType)
     */
    public void onDataBaseUpdate( DatabaseUpdateType updateType ) {

        
        Object[] services = dbChangeListenersTracker.getServices();
        if (services == null) {
            return;
        }
        for ( Object o : services ) {
            if ( o instanceof IDatabasehangeListener ) {
                ( (IDatabasehangeListener)o ).fireRefresh();
            }
        }
    }
    
    public void triggerDatabaseDecoratorsUpdate() {
        Object[] services = dbDecoratorChangeTracker.getServices();
        if ( services == null ) {
            return;
        }
        for ( Object o : services ) {
            if ( o instanceof IStructureDBLabelDecoratorChangeListener ) {
                ( (IStructureDBLabelDecoratorChangeListener) o ).fireRefresh();
            }
        }
    }
    
    /**
     * @param structureDBLightweightLabelDecorator
     */
    public void publishStructureDBDecoratorChangeListener( 
                    IStructureDBLabelDecoratorChangeListener l ) {

        bundleContext.registerService( 
                          IStructureDBLabelDecoratorChangeListener.class
                                                                  .getName(), 
                          l, 
                          null );
    }
}

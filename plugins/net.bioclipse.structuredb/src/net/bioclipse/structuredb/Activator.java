/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson 
 *     
 *******************************************************************************/

package net.bioclipse.structuredb;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.structuredb.business.IJSStructuredbManager;
import net.bioclipse.structuredb.business.IStructuredbManager;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    private Logger logger = Logger.getLogger(Activator.class);
    
    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.structuredb";

    // The shared instance
    private static Activator plugin;
    
    private ServiceTracker finderTracker;
    private ServiceTracker jsFinderTracker;

    /**
     * The constructor
     */
    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        finderTracker = new ServiceTracker( context, 
                                            IStructuredbManager.class
                                                .getName(), 
                                            null );
        finderTracker.open();
        jsFinderTracker = new ServiceTracker( context,
                                            IJSStructuredbManager.class
                                                                 .getName(),
                                            null );
        jsFinderTracker.open();
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

    public IStructuredbManager getStructuredbManager() {
        IStructuredbManager manager = null;
        try {
            manager = (IStructuredbManager) finderTracker.waitForService(1000*30);
        } 
        catch (InterruptedException e) {
            logger.warn("Exception occurred while attempting " +
            		    "to get the StructuredbManager" + e);
            LogUtils.debugTrace(logger, e);
        }
        if (manager == null) {
            throw new IllegalStateException("Could not get the structuredb manager");
        }
        return manager;
    }
    
    public IJSStructuredbManager getJSStructuredbManager() {
        IJSStructuredbManager manager = null;
        try {
            manager = (IJSStructuredbManager) jsFinderTracker.waitForService(1000*30);
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
}

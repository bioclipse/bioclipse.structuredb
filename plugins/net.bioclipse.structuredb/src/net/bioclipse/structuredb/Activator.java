/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/

package net.bioclipse.structuredb;

import java.io.IOException;
import java.net.URL;

import net.bioclipse.hsqldb.HsqldbUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.structuredb";

	// The shared instance
	private static Activator plugin;

	private ApplicationContext applicationContext;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		HsqldbUtil.getInstance().startHsqldbServer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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
	
	/**
	 * @return the Spring ApplicationContext
	 */
	public ApplicationContext getApplicationContext() {
		
		if(applicationContext == null) {
			try {
				applicationContext = new FileSystemXmlApplicationContext( getPluginURL() + 
				                                                          "src" +
				                                                          java.io.File.separator + 
				                                                          "applicationContext.xml" );
			} catch (Exception e) {
				throw new RuntimeException("Could not create applicationContext", e);
			}
		}
		return applicationContext;
	}
	
	/**
	 * @return the URL for this plugin
	 * @throws IOException
	 */
	public static URL getPluginURL() throws IOException {
        return FileLocator.toFileURL( Platform.getBundle(PLUGIN_ID).getEntry("/") );
	}
}

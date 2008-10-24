/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.jmol.business;

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.core.jobs.Job;

public interface IJmolManager extends IBioclipseManager{

    /**
     * Execute a script in Jmol. If editor active, run script there. 
     * In the future, if JmolView active, there too.
     * @param script The script command to run
     */
    @PublishedMethod( methodSummary = "Execute the given String as a " +
    		                              "script in Jmol.",
                      params = "String script" )
    @Recorded
    public void run(String script);

    
    /**
     * Load jmoo with a file
     * @param path Path to file, relative workspace
     */
    @PublishedMethod( methodSummary = "Load jmol with a file", 
                      params = "Path to file" )
    @Recorded
    public void load(String path);

    /**
     * @param file
     */
    public void load(IFile file);
    
    
    /**
     * Runs "spin on" in jmol
     */
    @Recorded
    @PublishedMethod( methodSummary = "Causes active jmol to " +
    		                              "spin molecule")
    public void spinOn();
    
    /**
     * Runs "spin off" in jmol
     */
    @Recorded
    @PublishedMethod( methodSummary = "Causes active jmol to stop " +
    		                              "spinning molecule" )
    public void spinOff();

    @Recorded
    @Job 
    @PublishedMethod(methodSummary = "Optimizes the geometry of the structure in the active JmolEditor" )
    public void minimize();
}

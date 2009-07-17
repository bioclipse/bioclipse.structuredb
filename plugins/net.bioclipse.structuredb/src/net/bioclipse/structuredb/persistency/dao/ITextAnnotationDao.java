/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.persistency.dao;

import java.util.List;

import net.bioclipse.structuredb.domain.TextAnnotation;


/**
 * @author jonalv
 *
 */
public interface ITextAnnotationDao 
       extends IGenericDao<TextAnnotation> {

    List<TextAnnotation> getAllLabels();

}

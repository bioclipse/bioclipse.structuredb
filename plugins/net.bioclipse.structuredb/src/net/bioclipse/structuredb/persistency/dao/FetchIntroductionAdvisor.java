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
package net.bioclipse.structuredb.persistency.dao;

import org.springframework.aop.support.DefaultIntroductionAdvisor;

/**
 * @author jonalv
 *
 */
public class FetchIntroductionAdvisor 
             extends DefaultIntroductionAdvisor {

    private static final long serialVersionUID = -6956114827617421806L;

    public FetchIntroductionAdvisor() {
        super( new FetchIntroductionInterceptor() );
    }
}

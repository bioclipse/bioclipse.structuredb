/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import net.bioclipse.structuredb.Structuredb;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;

/**
 * @author jonalv
 */
public class SQLMapPathBeanPostProcessor implements BeanPostProcessor {

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if( beanName.equals("sqlMapClient") &&
			bean instanceof SqlMapClientFactoryBean	) {
			
			String path = Structuredb.class
	                                 .getClassLoader()
	                                 .getResource("sqlMapConfig.xml")
		                             .toString();
			if( path.contains("file:") ) {
				path = path.substring( 5 );
			}
			
			( (SqlMapClientFactoryBean)bean ).setConfigLocation( 
					new FileSystemResource(path) );
		}
		return bean;
	}
}

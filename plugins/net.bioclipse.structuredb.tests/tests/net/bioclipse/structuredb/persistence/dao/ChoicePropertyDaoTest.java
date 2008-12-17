/*******************************************************************************
 * Copyright (c) 2007 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Alvarsson
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.persistence.dao;
import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.Property;
import net.bioclipse.structuredb.domain.PropertyChoice;
import net.bioclipse.structuredb.persistency.dao.IChoicePropertyDao;
/**
 * @author jonalv
 *
 */
public class ChoicePropertyDaoTest 
             extends GenericDaoTest<ChoiceProperty> {
    private IChoicePropertyDao choicePropertyDao;
    public ChoicePropertyDaoTest() {
        super( ChoiceProperty.class );
    }
    @Override
    public void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        choicePropertyDao = (IChoicePropertyDao) 
                            applicationContext.getBean( "choicePropertyDao" );
    }
    private ChoiceProperty createChoicePropertyWithPropertyChoice() {
        ChoiceProperty choiceProperty = new ChoiceProperty("name");
        PropertyChoice propertyChoice = new PropertyChoice("value");
        choiceProperty.addPropertyChoice( propertyChoice );
        choicePropertyDao.insert( choiceProperty );
        return choiceProperty;
    }
    public void testInsertingWithPropertyChoice() {
        ChoiceProperty choiceProperty 
            = createChoicePropertyWithPropertyChoice();
        ChoiceProperty loaded 
            = choicePropertyDao.getById( choiceProperty.getId() );
        assertTrue( choiceProperty.hasValuesEqualTo( loaded ) );
        assertEquals( choiceProperty.getPropertyChoices().size(),
                      loaded.getPropertyChoices().size() );
    }
    public void testUpdateWithPropertyChoice() {
        ChoiceProperty choiceProperty 
            = createChoicePropertyWithPropertyChoice();
        PropertyChoice propertyChoice = new PropertyChoice("another value");
        choiceProperty.addPropertyChoice( propertyChoice );
        choicePropertyDao.update( choiceProperty );
        ChoiceProperty loaded 
            = choicePropertyDao.getById( choiceProperty.getId() );
        assertTrue( choiceProperty.hasValuesEqualTo( loaded ) );
        assertEquals( choiceProperty.getPropertyChoices().size(),
                      loaded.getPropertyChoices().size() );
    }
    public void testGetByName() {
        ChoiceProperty choiceProperty 
            = createChoicePropertyWithPropertyChoice();
        choiceProperty.setName( "name" );
        choicePropertyDao.update( choiceProperty );
        ChoiceProperty loaded 
            = choicePropertyDao.getByName( choiceProperty.getName() );
        assertTrue( choiceProperty.hasValuesEqualTo( loaded ) );
    }
}

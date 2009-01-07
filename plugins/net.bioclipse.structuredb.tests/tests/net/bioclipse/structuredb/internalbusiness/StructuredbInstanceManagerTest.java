/*******************************************************************************
 * Copyright (c) 2007-2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     
 *******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.structuredb.Structuredb;
import net.bioclipse.structuredb.domain.Annotation;
import net.bioclipse.structuredb.domain.ChoiceAnnotation;
import net.bioclipse.structuredb.domain.ChoiceProperty;
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.PropertyChoice;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.persistence.HsqldbTestServerManager;
import net.bioclipse.structuredb.persistency.dao.ChoiceAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IChoiceAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IChoicePropertyDao;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
import net.bioclipse.structuredb.persistency.dao.IRealNumberAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IRealNumberPropertyDao;
import net.bioclipse.structuredb.persistency.dao.ITextAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.ITextPropertyDao;
import net.bioclipse.structuredb.persistency.dao.IUserDao;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import testData.TestData;

public class StructuredbInstanceManagerTest 
       extends AbstractAnnotationAwareTransactionalTests  {
    
    protected IStructuredbInstanceManager manager;
    
    protected IUserDao                 userDao;
    protected IDBMoleculeDao           dBMoleculeDao;
    protected IChoicePropertyDao       choicePropertyDao;
    protected IRealNumberPropertyDao   realNumberPropertyDao;
    protected ITextPropertyDao         textPropertyDao;
    protected IChoiceAnnotationDao     choiceAnnotationDao;
    protected IRealNumberAnnotationDao realNumberAnnotationDao;
    protected ITextAnnotationDao       textAnnotationDao;
    
    private TextProperty textProperty;

    public StructuredbInstanceManagerTest() {
        super();
        HsqldbTestServerManager.INSTANCE.startServer();
        HsqldbTestServerManager.INSTANCE.setupTestEnvironment();
    }
    
    static {
        System.setProperty(
           "javax.xml.parsers.SAXParserFactory", 
           "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
        );
        System.setProperty(
           "javax.xml.parsers.DocumentBuilderFactory", 
           "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
        );        
    }
    
    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
        super.onSetUpBeforeTransaction();
        manager                = (IStructuredbInstanceManager) 
                                 applicationContext
                                     .getBean("structuredbInstanceManager");
        dBMoleculeDao          = (IDBMoleculeDao) 
                                 applicationContext
                                     .getBean("dBMoleculeDao");
        userDao                = (IUserDao) 
                                 applicationContext
                                     .getBean("userDao");
        choicePropertyDao      = (IChoicePropertyDao) 
                                 applicationContext
                                     .getBean( "choicePropertyDao" );
        realNumberPropertyDao  = (IRealNumberPropertyDao) 
                                 applicationContext
                                     .getBean( "realNumberPropertyDao" );
        textPropertyDao        = (ITextPropertyDao) 
                                 applicationContext
                                     .getBean( "textPropertyDao" );
        choiceAnnotationDao    = (IChoiceAnnotationDao) 
                                 applicationContext
                                      .getBean( "choiceAnnotationDao" );
        realNumberAnnotationDao = (IRealNumberAnnotationDao) 
                                  applicationContext
                                      .getBean( "realNumberAnnotationDao" );
        textAnnotationDao       = (ITextAnnotationDao) 
                                  applicationContext
                                      .getBean( "textAnnotationDao" );
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        ( (ILoggedInUserKeeper) applicationContext
                .getBean("loggedInUserKeeper") )
                .setLoggedInUser(null);
        User testUser = new User("username", "password", true);
        userDao.insert(testUser);
        ( (ILoggedInUserKeeper) applicationContext
                                .getBean("loggedInUserKeeper") )
                                .setLoggedInUser(testUser);
    }
    
    private TextAnnotation createAnnotation(String name) {
        if ( textProperty == null ) {
            textProperty = new TextProperty("testProperty");
            manager.insertTextProperty(textProperty);
        }
        TextAnnotation annotation = new TextAnnotation(name, textProperty);
        manager.insertTextAnnotation(annotation);
        return annotation;
    }
    
    private DBMolecule createMolecule( String name, 
                                        AtomContainer atomContainer) 
                       throws CDKException {
        long before = System.currentTimeMillis();
        DBMolecule dBMolecule = new DBMolecule( name, atomContainer );
        long inBetween = System.currentTimeMillis();
        manager.insertMolecule(dBMolecule);
        long after = System.currentTimeMillis();
        System.out.println("Creating structure took: " + (inBetween - before) + "ms");
        System.out.println("Persisting structure took: " + (after - inBetween) + "ms");
        return dBMolecule;
    }
    
    public void testInsertDBMolecule() throws CDKException {
        
        DBMolecule dBMolecule = createMolecule( "CycloOctan", 
                                                 TestData.getCycloOctan() );
        List<DBMolecule> allMolecules = dBMoleculeDao.getAll(); 
        assertTrue( allMolecules.contains(dBMolecule) );
    }

    private User createUser(String username, String password, boolean sudoer) {
        User user = new User(username, password, sudoer);
        manager.insertUser(user);
        return user;
    }
    
    public void testInsertUser() {
        User user = createUser("another username", "secrest", false);
        List<User> allUsers = userDao.getAll(); 
        assertTrue( allUsers.contains(user) );
    }

    private ChoiceProperty createChoiceProperty( String name ) {
        ChoiceProperty choiceProperty = new ChoiceProperty(name);
        manager.insertChoiceProperty(choiceProperty);
        PropertyChoice propertyChoice = new PropertyChoice("value");
        choiceProperty.addPropertyChoice( propertyChoice );
        manager.update( choiceProperty );
        return choiceProperty;
    }
    
    public void testInsertChoiceProperty() {
        ChoiceProperty choiceProperty = createChoiceProperty("name");
        List<ChoiceProperty> allChoiceProperties = choicePropertyDao.getAll();
        assertTrue( allChoiceProperties.contains( choiceProperty ) );
    }

    private RealNumberProperty createRealNumberProperty( String name ) {
        RealNumberProperty realNumberProperty 
            = new RealNumberProperty(name);
        manager.insertRealNumberProperty( realNumberProperty );
        return realNumberProperty;
    }

    public void testInsertRealNumberProperty() {
        RealNumberProperty realNumberProperty 
            = createRealNumberProperty( "name" );
        List<RealNumberProperty> allRealNumberProperties 
            = realNumberPropertyDao.getAll();
        assertTrue( allRealNumberProperties.contains( realNumberProperty ) );
    }
    
    private TextProperty createTextProperty( String name ) {
        TextProperty textProperty = new TextProperty(name);
        manager.insertTextProperty( textProperty );
        return textProperty;
    }
    
    public void testInsertTextProperty() {
        TextProperty textProperty = createTextProperty( "name" );
        List<TextProperty> allTextProperties = textPropertyDao.getAll();
        assertTrue( allTextProperties.contains( textProperty ) );
    }
    
    private ChoiceAnnotation createChoiceAnnotation( 
            String name, ChoiceProperty property ) {
        
        ChoiceAnnotation choiceAnnotation = new ChoiceAnnotation( name, 
                                                                  property );
        manager.insertChoiceAnnotation(choiceAnnotation);
        return choiceAnnotation; 
    }
    
    public void testInsertChoiceAnnotation() {
        ChoiceAnnotation choiceAnnotation 
            = createChoiceAnnotation( "name", createChoiceProperty( "name" ) );
        List<ChoiceAnnotation> allChoiceAnnotations 
            = choiceAnnotationDao.getAll();
        assertTrue( allChoiceAnnotations.contains( choiceAnnotation ) );
    }
    
    private RealNumberAnnotation createRealNumberAnnotation( 
            double value, RealNumberProperty property ) {
        
        RealNumberAnnotation realNumberAnnotation 
            = new RealNumberAnnotation(value, property);
        manager.insertRealNumberAnnotation(realNumberAnnotation);
        return realNumberAnnotation;
    }
    
    public void testInsertRealNumberAnnotation() {
        RealNumberAnnotation realNumberAnnotation 
            = createRealNumberAnnotation( 0, 
                                          createRealNumberProperty( "name" ) );
        List<RealNumberAnnotation> allRealNumberAnnotations 
            = realNumberAnnotationDao.getAll();
        assertTrue( allRealNumberAnnotations
                        .contains( realNumberAnnotation ) );
    }
    
    private TextAnnotation createTextAnnotation( String value, 
                                                 TextProperty property ) {
        TextAnnotation textAnnotation 
            = new TextAnnotation( value, property );
        manager.insertTextAnnotation( textAnnotation );
        return textAnnotation;
    }
    
    public void testInsertTextAnnotation() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "name", 
                                    createTextProperty( "name" ) );
        List<TextAnnotation> allTextAnnotations = textAnnotationDao.getAll();
        assertTrue( allTextAnnotations.contains( textAnnotation ) );
    }

    public void testDeleteUser() {
        User user = createUser("another username", "secrest", true);
        assertTrue( userDao.getAll().contains(user) );
        manager.delete(user);
        assertFalse( userDao.getAll().contains(user) );
    }

    public void testDeleteStructure() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOcan", 
                                               TestData.getCycloOctan() );
        assertTrue( dBMoleculeDao.getAll().contains(dBMolecule) );
        manager.delete(dBMolecule);
        assertFalse( dBMoleculeDao.getAll().contains(dBMolecule) );
    }
    
    public void testDeleteChoiceProperty() {
        ChoiceProperty choiceProperty = createChoiceProperty( "name" );
        assertTrue( choicePropertyDao.getAll().contains( choiceProperty ) );
        manager.delete( choiceProperty );
        assertFalse( choicePropertyDao.getAll().contains( choiceProperty ) );
    }
    
    public void testDeleteRealNumberProperty() {
        RealNumberProperty realNumberProperty 
            = createRealNumberProperty( "name" );
        assertTrue( realNumberPropertyDao.getAll()
                                         .contains( realNumberProperty ) );
        manager.delete( realNumberProperty );
        assertFalse( realNumberPropertyDao.getAll()
                                          .contains( realNumberProperty ) );
    }
    
    public void testDeleteTextProperty() {
        TextProperty textProperty = createTextProperty( "name" );
        assertTrue( textPropertyDao.getAll().contains( textProperty ) );
        manager.delete( textProperty );
        assertFalse( textPropertyDao.getAll().contains( textProperty ) );
    }
    
    public void testDeleteTextAnnotation() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "value", 
                                    createTextProperty( "name" ) );
        assertTrue( textAnnotationDao.getAll().contains( textAnnotation ) );
        manager.delete( textAnnotation );
        assertFalse( textAnnotationDao.getAll().contains( textAnnotation ) );
    }
    
    public void testRealNumberAnnotationDelete() {
        RealNumberAnnotation realNumberAnnotation 
            = createRealNumberAnnotation( 1, 
                                          createRealNumberProperty( "name" ) );
        assertTrue( realNumberAnnotationDao.getAll()
                                           .contains(realNumberAnnotation) );
        manager.delete( realNumberAnnotation );
        assertFalse( realNumberAnnotationDao.getAll()
                                            .contains(realNumberAnnotation) );
    }
    
    public void testTextAnnotationDelete() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "value",
                                    createTextProperty( "name" ) );
        assertTrue( textAnnotationDao.getAll().contains( textAnnotation ) );
        manager.delete( textAnnotation );
        assertFalse( textAnnotationDao.getAll().contains( textAnnotation ) );
    }
    
    public void testRetrieveAllAnnotations() {
        Annotation annotation1 = createAnnotation("testAnnotation1");
        Annotation annotation2 = createAnnotation("testAnnotation2");
        
        assertTrue( manager.retrieveAllAnnotations().containsAll( 
                Arrays.asList(new Annotation[] {annotation1, annotation2}) ) );
    }

    public void testRetrieveAllStructures() throws CDKException {
        DBMolecule structure1 = createMolecule( "CycloOctan", 
                                                 TestData.getCycloOctan() );
        DBMolecule structure2 = createMolecule( "CycloPropane", 
                                                 TestData.getCycloPropane() );
        
        assertTrue( manager.retrieveAllMolecules().containsAll(
                Arrays.asList(new DBMolecule[] {structure1, structure2}) ) );
    }

    public void testRetrieveAllUsers() {
        User user1 = createUser("username1", "secret", false);
        User user2 = createUser("username2", "masterkey", true);
        
        assertTrue( manager.retrieveAllUsers().containsAll( 
                Arrays.asList(new User[] {user1, user2}) ) );
    }

    public void testRetrieveStructureByName() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOctan", 
                                                TestData.getCycloOctan() );
        assertTrue( manager
                    .retrieveStructureByName("CycloOctan")
                    .contains(dBMolecule) );
    }

    public void testRetrieveUserByName() {
        User user = createUser("another username", "secret", false);
        
        assertNotNull(user);
        
        assertTrue( user.hasValuesEqualTo( 
                        manager.retrieveUserByUsername("another username") ) );
    }
    
    public void testRetrievePropertyByName() {
        final String NAME1 = "1";
        final String NAME2 = "2";
        final String NAME3 = "3";
        TextProperty textProperty             = new TextProperty(NAME1);
        RealNumberProperty realNumberProperty = new RealNumberProperty(NAME2);
        ChoiceProperty choiceProperty         = new ChoiceProperty(NAME3);
        manager.insertTextProperty(       textProperty       );
        manager.insertRealNumberProperty( realNumberProperty );
        manager.insertChoiceProperty(     choiceProperty     );
        
        assertTrue( textProperty.hasValuesEqualTo( 
                        manager.retrievePropertyByName(NAME1) ) );
        assertTrue( realNumberProperty.hasValuesEqualTo( 
                        manager.retrievePropertyByName(NAME2) ) );
        assertTrue( choiceProperty.hasValuesEqualTo(
                        manager.retrievePropertyByName(NAME3 ) ) );
    }

    public void testUpdateLibrary() {
        TextAnnotation annotation = createAnnotation("testAnnotatin");
        annotation.setValue("edited");
        manager.update(annotation);
        assertTrue( 
            annotation.hasValuesEqualTo( 
                textAnnotationDao.getById(annotation.getId()) ) );
    }

    public void testUpdateUser() {
        User user = createUser("another username", "secret", false);
        user.setUserName("edited");
        manager.update(user);
        assertTrue( user.hasValuesEqualTo( userDao.getById(user.getId()) ) );
    }

    public void testUpdateMolecule() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOctan", 
                                                TestData.getCycloOctan() );
        dBMolecule.setName("edited");
        manager.update(dBMolecule);
        assertTrue( 
            dBMolecule.hasValuesEqualTo( 
                dBMoleculeDao.getById(dBMolecule.getId()) ) );
    }
    
    public void testUpdateChoiceProperty() {
        ChoiceProperty choiceProperty = createChoiceProperty( "name" );
        choiceProperty.setName( "edited" );
        manager.update( choiceProperty );
        assertTrue( 
            choiceProperty.hasValuesEqualTo( 
                choicePropertyDao.getById( choiceProperty.getId() ) ) );
    }
    
    public void testUpdateRealNumberProperty() {
        RealNumberProperty realNumberProperty 
            = createRealNumberProperty( "name" );
        realNumberProperty.setName( "edited" );
        manager.update( realNumberProperty );
        assertTrue(
            realNumberProperty.hasValuesEqualTo( 
                realNumberPropertyDao.getById( 
                    realNumberProperty.getId() ) ) );
    }
    
    public void testUpdateTextProperty() {
        TextProperty textProperty = createTextProperty( "name" );
        textProperty.setName( "edited" );
        manager.update( textProperty );
        assertTrue(
            textProperty.hasValuesEqualTo( 
                textPropertyDao.getById( textProperty.getId() ) ) );
    }
    
    public void testUpdateChoiceAnnotation() {
        ChoiceAnnotation choiceAnnotation 
            = createChoiceAnnotation( "name", createChoiceProperty( "name" ) );
        choiceAnnotation.setValue( "edited" );
        manager.update( choiceAnnotation );
        assertTrue(
            choiceAnnotation.hasValuesEqualTo( 
                choiceAnnotationDao.getById( choiceAnnotation.getId() ) ) );
    }
    
    public void testUpdateRealNumberAnnotation() {
        RealNumberAnnotation realNumberAnnotation
            = createRealNumberAnnotation( 1, 
                                          createRealNumberProperty( "name" ) );
        manager.update( realNumberAnnotation );
        assertTrue( 
            realNumberAnnotation.hasValuesEqualTo( 
                realNumberAnnotationDao.getById( 
                    realNumberAnnotation.getId() ) ) );
    }
    
    public void testUpdateTextAnnotation() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "value", createTextProperty( "name" ) );
        manager.update( textAnnotation );
        assertTrue( 
            textAnnotation.hasValuesEqualTo( 
                textAnnotationDao.getById( textAnnotation.getId() ) ) );
    }
    
    public void testDeleteMoleculeAndStructures() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOcan", 
                                               TestData.getCycloOctan() );
        TextAnnotation annotation = createAnnotation( "test" );
        dBMolecule.addAnnotation( annotation );
        manager.update( annotation );
        
        assertTrue( textAnnotationDao.getById( annotation.getId() )
                                     .getDBMolecules()
                                     .contains(dBMolecule) );
        manager.deleteWithMolecules( annotation, null );
        assertFalse( dBMoleculeDao.getAll().contains(dBMolecule) );
        assertFalse( textAnnotationDao.getAll().contains(annotation) );
    }
    
    public void testAllStructureIterator() throws CDKException {
        testRetrieveAllStructures();
        List<DBMolecule> dBMolecules = manager.retrieveAllMolecules();
        Iterator<DBMolecule> structureIterator = manager.allStructuresIterator();
        assertTrue( structureIterator.hasNext() );
        while ( structureIterator.hasNext() ) {
            assertTrue( dBMolecules.contains( structureIterator.next() ) );
        }
    }
    
    public void testAllLabels() {
        TextAnnotation a = new TextAnnotation( "a label", 
                                               new TextProperty("label") );
        TextAnnotation b = new TextAnnotation( "not a label", 
                                               new TextProperty("not label") );
        manager.insertTextAnnotation( a );
        manager.insertTextAnnotation( b );
        
        assertTrue( manager.allLabels().contains( a ) );
        assertFalse( manager.allLabels().contains( b ) );
    }
    
    protected String[] getConfigLocations() {
        String path = Structuredb.class.getClassLoader()
                                 .getResource("applicationContext.xml")
                                 .toString();
        
        return new String[] { path };
    }
}

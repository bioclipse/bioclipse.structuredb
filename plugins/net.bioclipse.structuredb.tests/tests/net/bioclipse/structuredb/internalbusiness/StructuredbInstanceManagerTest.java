/* *****************************************************************************
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
import net.bioclipse.structuredb.domain.DBMolecule;
import net.bioclipse.structuredb.domain.RealNumberAnnotation;
import net.bioclipse.structuredb.domain.RealNumberProperty;
import net.bioclipse.structuredb.domain.TextAnnotation;
import net.bioclipse.structuredb.domain.TextProperty;
import net.bioclipse.structuredb.persistence.HsqldbTestServerManager;
import net.bioclipse.structuredb.persistency.dao.IDBMoleculeDao;
import net.bioclipse.structuredb.persistency.dao.IRealNumberAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.IRealNumberPropertyDao;
import net.bioclipse.structuredb.persistency.dao.ITextAnnotationDao;
import net.bioclipse.structuredb.persistency.dao.ITextPropertyDao;

import org.junit.Assert;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import testData.TestData;

public class StructuredbInstanceManagerTest 
       extends AbstractAnnotationAwareTransactionalTests  {
    
    protected IStructuredbInstanceManager manager;
    
    protected IDBMoleculeDao           dBMoleculeDao;
    protected IRealNumberPropertyDao   realNumberPropertyDao;
    protected ITextPropertyDao         textPropertyDao;
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
        realNumberPropertyDao  = (IRealNumberPropertyDao) 
                                 applicationContext
                                     .getBean( "realNumberPropertyDao" );
        textPropertyDao        = (ITextPropertyDao) 
                                 applicationContext
                                     .getBean( "textPropertyDao" );
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
        Assert.assertTrue( allMolecules.contains(dBMolecule) );
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
        Assert.assertTrue( allRealNumberProperties.contains( realNumberProperty ) );
    }
    
    private TextProperty createTextProperty( String name ) {
        TextProperty textProperty = new TextProperty(name);
        manager.insertTextProperty( textProperty );
        return textProperty;
    }
    
    public void testInsertTextProperty() {
        TextProperty textProperty = createTextProperty( "name" );
        List<TextProperty> allTextProperties = textPropertyDao.getAll();
        Assert.assertTrue( allTextProperties.contains( textProperty ) );
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
        Assert.assertTrue( allRealNumberAnnotations
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
        Assert.assertTrue( allTextAnnotations.contains( textAnnotation ) );
    }

    public void testDeleteStructure() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOcan", 
                                               TestData.getCycloOctan() );
        Assert.assertTrue( dBMoleculeDao.getAll().contains(dBMolecule) );
        manager.delete(dBMolecule);
        Assert.assertFalse( dBMoleculeDao.getAll().contains(dBMolecule) );
    }
    
    public void testDeleteRealNumberProperty() {
        RealNumberProperty realNumberProperty 
            = createRealNumberProperty( "name" );
        Assert.assertTrue( realNumberPropertyDao.getAll()
                                               .contains( realNumberProperty ) );
        manager.delete( realNumberProperty );
        Assert.assertFalse( realNumberPropertyDao.getAll()
                                                 .contains( realNumberProperty ) );
    }
    
    public void testDeleteTextProperty() {
        TextProperty textProperty = createTextProperty( "name" );
        Assert.assertTrue( textPropertyDao.getAll().contains( textProperty ) );
        manager.delete( textProperty );
        Assert.assertFalse( textPropertyDao.getAll().contains( textProperty ) );
    }
    
    public void testDeleteTextAnnotation() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "value", 
                                    createTextProperty( "name" ) );
        Assert.assertTrue( textAnnotationDao.getAll().contains( textAnnotation ) );
        manager.delete( textAnnotation );
        Assert.assertFalse( textAnnotationDao.getAll().contains( textAnnotation ) );
    }
    
    public void testRealNumberAnnotationDelete() {
        RealNumberAnnotation realNumberAnnotation 
            = createRealNumberAnnotation( 1, 
                                          createRealNumberProperty( "name" ) );
        Assert.assertTrue( realNumberAnnotationDao.getAll()
                                           .contains(realNumberAnnotation) );
        manager.delete( realNumberAnnotation );
        Assert.assertFalse( realNumberAnnotationDao.getAll()
                                            .contains(realNumberAnnotation) );
    }
    
    public void testTextAnnotationDelete() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "value",
                                    createTextProperty( "name" ) );
        Assert.assertTrue( textAnnotationDao.getAll().contains( textAnnotation ) );
        manager.delete( textAnnotation );
        Assert.assertFalse( textAnnotationDao.getAll().contains( textAnnotation ) );
    }
    
    public void testRetrieveAllAnnotations() {
        Annotation annotation1 = createAnnotation("testAnnotation1");
        Annotation annotation2 = createAnnotation("testAnnotation2");
        
        Assert.assertTrue( manager.retrieveAllAnnotations().containsAll( 
                Arrays.asList(new Annotation[] {annotation1, annotation2}) ) );
    }

    public void testRetrieveAllStructures() throws CDKException {
        DBMolecule structure1 = createMolecule( "CycloOctan", 
                                                 TestData.getCycloOctan() );
        DBMolecule structure2 = createMolecule( "CycloPropane", 
                                                 TestData.getCycloPropane() );
        
        Assert.assertTrue( manager.retrieveAllMolecules().containsAll(
                Arrays.asList(new DBMolecule[] {structure1, structure2}) ) );
    }

    public void testRetrieveStructureByName() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOctan", 
                                                TestData.getCycloOctan() );
        Assert.assertTrue( manager
                    .retrieveStructureByName("CycloOctan")
                    .contains(dBMolecule) );
    }

    public void testRetrievePropertyByName() {
        final String NAME1 = "1";
        final String NAME2 = "2";
        final String NAME3 = "3";
        TextProperty textProperty             = new TextProperty(NAME1);
        RealNumberProperty realNumberProperty = new RealNumberProperty(NAME2);
        manager.insertTextProperty(       textProperty       );
        manager.insertRealNumberProperty( realNumberProperty );
        
        Assert.assertTrue( textProperty.hasValuesEqualTo( 
                        manager.retrievePropertyByName(NAME1) ) );
        Assert.assertTrue( realNumberProperty.hasValuesEqualTo( 
                        manager.retrievePropertyByName(NAME2) ) );
    }

    public void testUpdateLibrary() {
        TextAnnotation annotation = createAnnotation("testAnnotatin");
        annotation.setValue("edited");
        manager.update(annotation);
        Assert.assertTrue( 
            annotation.hasValuesEqualTo( 
                textAnnotationDao.getById(annotation.getId()) ) );
    }

    public void testUpdateMolecule() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOctan", 
                                                TestData.getCycloOctan() );
        dBMolecule.setName("edited");
        manager.update(dBMolecule);
        Assert.assertTrue( 
            dBMolecule.hasValuesEqualTo( 
                dBMoleculeDao.getById(dBMolecule.getId()) ) );
    }
    
    public void testUpdateRealNumberProperty() {
        RealNumberProperty realNumberProperty 
            = createRealNumberProperty( "name" );
        realNumberProperty.setName( "edited" );
        manager.update( realNumberProperty );
        Assert.assertTrue(
            realNumberProperty.hasValuesEqualTo( 
                realNumberPropertyDao.getById( 
                    realNumberProperty.getId() ) ) );
    }
    
    public void testUpdateTextProperty() {
        TextProperty textProperty = createTextProperty( "name" );
        textProperty.setName( "edited" );
        manager.update( textProperty );
        Assert.assertTrue(
            textProperty.hasValuesEqualTo( 
                textPropertyDao.getById( textProperty.getId() ) ) );
    }
    
    public void testUpdateRealNumberAnnotation() {
        RealNumberAnnotation realNumberAnnotation
            = createRealNumberAnnotation( 1, 
                                          createRealNumberProperty( "name" ) );
        manager.update( realNumberAnnotation );
        Assert.assertTrue( 
            realNumberAnnotation.hasValuesEqualTo( 
                realNumberAnnotationDao.getById( 
                    realNumberAnnotation.getId() ) ) );
    }
    
    public void testUpdateTextAnnotation() {
        TextAnnotation textAnnotation 
            = createTextAnnotation( "value", createTextProperty( "name" ) );
        manager.update( textAnnotation );
        Assert.assertTrue( 
            textAnnotation.hasValuesEqualTo( 
                textAnnotationDao.getById( textAnnotation.getId() ) ) );
    }
    
    public void testDeleteAnnotationAndStructures() throws CDKException {
        DBMolecule dBMolecule = createMolecule( "CycloOcan", 
                                               TestData.getCycloOctan() );
        TextAnnotation annotation = createAnnotation( "test" );
        dBMolecule.addAnnotation( annotation );
        manager.update( annotation );
        
        Assert.assertTrue( textAnnotationDao.getById( annotation.getId() )
                                     .getDBMolecules()
                                     .contains(dBMolecule) );
        manager.deleteWithMolecules( annotation, null );
        Assert.assertFalse( dBMoleculeDao.getAll().contains(dBMolecule) );
        Assert.assertFalse( textAnnotationDao.getAll().contains(annotation) );
    }
    
    public void testAllStructureIterator() throws CDKException {
        testRetrieveAllStructures();
        List<DBMolecule> dBMolecules = manager.retrieveAllMolecules();
        Iterator<DBMolecule> structureIterator = manager.allStructuresIterator();
        Assert.assertTrue( structureIterator.hasNext() );
        while ( structureIterator.hasNext() ) {
        	Assert.assertTrue( dBMolecules.contains( structureIterator.next() ) );
        }
    }
    
    public void testAllLabels() {
        TextAnnotation a = new TextAnnotation( "a label", 
                                               new TextProperty("label") );
        TextAnnotation b = new TextAnnotation( "not a label", 
                                               new TextProperty("not label") );
        manager.insertTextAnnotation( a );
        manager.insertTextAnnotation( b );
        
        Assert.assertTrue( manager.allLabels().contains( a ) );
        Assert.assertFalse( manager.allLabels().contains( b ) );
    }
    
    protected String[] getConfigLocations() {
        String path = Structuredb.class.getClassLoader()
                                 .getResource("applicationContext.xml")
                                 .toString();
        
        return new String[] { path };
    }
}

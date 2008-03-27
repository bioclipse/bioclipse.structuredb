package net.bioclipse.structuredb.persistency.dao;

import java.sql.Timestamp;
import java.util.List;

import net.bioclipse.hsqldb.HsqldbUtil;
import net.bioclipse.structuredb.domain.BaseObject;
import net.bioclipse.structuredb.domain.User;
import net.bioclipse.structuredb.persistency.HsqldbTestServerManager;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic base class for testing the daos. Performs tests of the basic dao methods.
 * Usage: Extend and give the domain class to be tested as generic class parameter.
 *        When extra testsmethods are written setUpTestEnvironment() needs to be called
 *        to initiate the dao.
 * 
 * @author jonalv
 *
 * @param <DomainType>
 */
public abstract class GenericDaoTest<DomainType extends BaseObject> extends AbstractAnnotationAwareTransactionalTests  {

	static {
		HsqldbTestServerManager.INSTANCE.startServer();
		HsqldbTestServerManager.INSTANCE.setupTestEnvironment();
	}
	
	protected IGenericDao<DomainType> dao;
	
	private Class<DomainType> domainClass;
	
	protected User god;
	
	protected DomainType object1;
	protected DomainType object2;

	/**
	 * @param c Class for the domain type to be tested
	 */
	public GenericDaoTest( Class<DomainType> c ) {
		super();
		this.domainClass = c;
	}
	
	@Transactional
	public void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		
		god = new User("God", "god", "fiat lux");
		
		IUserDao userDao = (IUserDao) applicationContext.getBean("userDao");
		
		userDao.persistGodObject(god);
		String daoName = domainClass.getSimpleName() + "Dao";
		daoName = firstToLowerCase(daoName);
		dao = (IGenericDao<DomainType>) applicationContext.getBean(daoName);
		try {
			object1 = domainClass.newInstance();
			object2 = domainClass.newInstance();
			object2.setName("otherName");
			setCreatorAndEditor(object1);
			setCreatorAndEditor(object2);
		} 
		catch (Exception e) {
			fail( e.toString() );
		}
		dao.insert(object1);
		dao.insert(object2);
	}
	
	private String firstToLowerCase(String daoName) {
		return Character.toLowerCase( daoName.charAt(0) ) + daoName.substring(1);
	}

	protected void setCreatorAndEditor(BaseObject object) {
		Timestamp now = new Timestamp( System.currentTimeMillis() );
		object.setCreated(now);
		object.setEdited(now);
		object.setCreator(god);
		object.setLastEditor(god);
	}

	/**
	 * tests getting all objects of the domain type handled by the tested dao
	 */
	public void testGetAll() {
		List<DomainType> objects = dao.getAll();
		assertTrue( objects.contains(object1) );
		assertTrue( objects.contains(object2) );
	}
	
	/**
	 * tests getting an instance by id of the domain type handled by the tested dao
	 */
	public void testGetById() {
		DomainType loadedObject1 = dao.getById( object1.getId() );
		assertNotNull( "The lodaded object should not be null", loadedObject1 );
		assertTrue( "The loaded object should have values equal to the original object", object1.hasValuesEqualTo(loadedObject1) );
		assertNotSame( "The loaded object and the original object shuold not be the same",  object1, loadedObject1 );
	}
	
	/**
	 * tests deleting of an instance of the domain type handled by the tested dao 
	 */
	public void testDelete() {
		dao.delete( object1.getId() );
		DomainType loadedObject1 = dao.getById( object1.getId() );
		assertNull(loadedObject1);
		String sql = "SELECT COUNT(*) FROM " + domainClass.getSimpleName() + " WHERE id='" + object1.getId() + "'";
		System.out.println(sql);
		int numberof = jdbcTemplate.queryForInt(sql);
		assertEquals( "The object should be deleted", 0, numberof);
	}
	
	/**
	 * tests updating an instance of the domain type handled by the tested dao
	 */
	public void testUpdate() {
		DomainType loadedObject1 = dao.getById( object1.getId() );
		assertFalse(loadedObject1.getName().equals("edited"));
		loadedObject1.setName("edited");
		dao.update(loadedObject1);
		loadedObject1 = dao.getById( object1.getId() );
		assertEquals("name should have changed", "edited", loadedObject1.getName());
	}
	
	protected String[] getConfigLocations() {
		return new String[] { "classpath:applicationContext.xml" };
	}
}

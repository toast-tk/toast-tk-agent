/**
 * 
 */
package com.synaptix.toast.core.runtime;

import java.io.Serializable;
import java.util.Map;

import com.synaptix.toast.core.annotation.craft.FixMe;

/**
 * @author E413544
 * 
 */
@FixMe(todo = "should be a service adapter interface ? keep in core, runtime or move in specific plugin module ?")
public interface ITestManager {

	/**
	 * Empty the test database and cache.
	 */
	public abstract void initDb();

	/**
	 * Clean and close the test database.
	 */
	public abstract void closeDb();

	/**
	 * Get instance for a class.
	 * 
	 * @param <T>
	 * 
	 * @param serviceClass
	 * @return
	 */
	public abstract <T> T getClassInstance(Class<T> serviceClass);

	// public abstract void beginTransaction();

	// public abstract void endTransaction();

	/**
	 * @param <E>
	 * @param entity
	 * @return
	 * @throws Exception
	 */

	// public abstract void stopXMPP(boolean b);

	// public abstract void startXMPP(boolean b);

	/**
	 * Find a component and save it.
	 * 
	 * @param componentName
	 *            Test name of the component class
	 * @param idValue
	 *            Value to search (ID, business key,...), the field to search is defined in test configuration
	 * @return The found component
	 */
	public abstract Object findObjectAndStorAsVarialble(String componentName, String idValue);

	public abstract Serializable addObject(Object entity);

	/**
	 * 
	 * @param entity
	 * @return Object if successfully inserted
	 * @throws Exception
	 */
	public int insertObject(Object entity) throws Exception;

	/**
	 * @param tableName
	 * @param valueMap
	 * @return
	 * @throws Exception
	 */
	public abstract int insertType(String tableName, Map<String, String> valueMap) throws Exception;

	/**
	 * TODO To DO type handler
	 * 
	 * @param property
	 * @param value
	 * @param object
	 * @return
	 */
	public abstract String findStringValueForProperty(String property, String value, Object object);

	/**
	 * TODO
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public abstract void saveEntity(Object entity) throws Exception;

	public abstract Object findObject(String entityName, String propertyName, String propertyValue);

	/**
	 * 
	 * @param entityClass
	 * @param valueMap
	 * @throws Exception
	 */
	public abstract void saveEntity(Class<?> entityClass, Map<String, Object> valueMap) throws Exception;

}
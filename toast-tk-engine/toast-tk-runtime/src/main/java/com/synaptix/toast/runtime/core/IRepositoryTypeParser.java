package com.synaptix.toast.runtime.core;

public interface IRepositoryTypeParser {

	/**
	 * 
	 * @param propertyClass
	 * @return
	 */
	public boolean hanldeClass(
		Class<?> propertyClass);

	/**
	 * FIXME use object ?
	 * 
	 * @param entityName
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	public Object parse(
		Class<?> propertyClass,
		String entityName,
		String propertyName,
		String propertyValue);
}

package com.synpatix.toast.runtime.core.runtime;

import com.google.inject.Inject;
import com.synaptix.toast.adapter.utils.FixtureHelper;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synpatix.toast.runtime.guice.IRepositoryTypeParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.math.BigDecimal;


public class DefaultRespositoryTypeParser implements IRepositoryTypeParser {

	private final static Log LOG = LogFactory.getLog(DefaultRespositoryTypeParser.class);

	
	@Inject
	IRepositorySetup repoSetup;
	
	@Inject
	ClassConfigProvider classConfigProvider;

	@Override
	public boolean hanldeClass(Class<?> propertyClass) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object parse(Class<?> propertyClass, String entityName, String propertyName, String propertyValue) {
		Object valueToSet = null;
		if (String.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.getString(propertyValue);
		} else if (Class.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.loadClass(propertyValue);
		} else if (BigDecimal.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseBigDecimal(propertyValue);
		} else if (Duration.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseDuration(propertyValue);
		} else if (Long.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseLong(propertyValue);
		} else if (Integer.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseInteger(propertyValue);
		} else if (Double.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseDouble(propertyValue);
		} else if (LocalDateTime.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseLocalDateTimeFromString(propertyValue);
		} else if (LocalDate.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseDateFromString(propertyValue);
		} else if (LocalTime.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseTimeFromString(propertyValue);
		} else if (Boolean.class.isAssignableFrom(propertyClass) || boolean.class.isAssignableFrom(propertyClass)) {
			valueToSet = FixtureHelper.parseBoolean(propertyValue);
		} else if (Enum.class.isAssignableFrom(propertyClass) && !isAssociation(entityName, propertyName)) {
			valueToSet = selectEnum(propertyClass, propertyValue);
		} else if (Enum.class.isAssignableFrom(propertyClass) && isAssociation(entityName, propertyName)) {
			valueToSet = selectEnum(entityName, propertyName, propertyValue);
		} else if (Serializable.class.isInstance(propertyClass) && isAssociation(entityName, propertyName)) {
			valueToSet = selectEntity(entityName, propertyName, propertyValue);
		}
		return valueToSet;
	}

	private Object selectEntity(String entityName, String propertyName, String propertyValue) {
		return repoSetup.getTestManager().findObject(entityName, propertyName, propertyValue);
	}

	private boolean isAssociation(String entityName, String propertyName) {
		entityName = FixtureHelper.parseTestString(entityName);
		propertyName = FixtureHelper.parseTestString(propertyName);

		return classConfigProvider.getConfigMap().get(entityName).propertiesMap.get(propertyName).entityType != null;
	}

	/**
	 * Get enum value from enum class and string value
	 * 
	 * @param propertyClass
	 * @param propertyValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object selectEnum(Class<?> propertyClass, String propertyValue) {
		if (Enum.class.isAssignableFrom(propertyClass)) {
			@SuppressWarnings("rawtypes")
			Class<? extends Enum> enumClass = (Class<? extends Enum>) propertyClass;
			return FixtureHelper.parseEnum(enumClass, propertyValue);
		}
		return null;
	}

	private Object selectEnum(String entityName, String propertyName, String propertyValue) {
		entityName = FixtureHelper.parseTestString(entityName);
		propertyName = FixtureHelper.parseTestString(propertyName);

		String entityTypeName = classConfigProvider.getConfigMap().get(entityName).propertiesMap.get(propertyName).entityType;
		if (classConfigProvider.getConfigMap().get(entityTypeName) == null) {
			LOG.error("Entity " + entityTypeName + " associated to " + entityName + " not found.");
			return null;
		}

		Class<Enum<?>> enumClass = classConfigProvider.getConfigMap().get(entityTypeName).enumClass;

		return FixtureHelper.parseEnum(enumClass, propertyValue);
	}

}

package com.synaptix.toast.adapter.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.inject.Inject;
import com.synaptix.toast.adapter.utils.FixtureHelper;
import com.synaptix.toast.core.runtime.IRepositorySetup;

public abstract class AbstractServiceActionAdapter {

	public Map<String, Object> getUserVariables() {
		return repositorySetup.getUserVariables();
	}

	public void setUserVariables(Map<String, Object> userVariables) {
		repositorySetup.setUserVariables(userVariables);
	}


	@Inject
	private IRepositorySetup repositorySetup;

	public AbstractServiceActionAdapter() {
		super();
	}

	/**
	 * Get a component saved in user variables.
	 */
	public Object getComponent(String componentName) {
		Object result = null;
		if (getUserVariables().get(componentName) instanceof Object) {
			result = getUserVariables().get(componentName);
		}
		return result;
	}

	/**
	 * Save an object in user variables.
	 */
	public <E extends Object> void register(String componentName, Object component) {
		getUserVariables().put(componentName, component);
	}

	/**
	 * Get a property from a component saved in user variables.
	 */
	public String getProperty(String componentName, String propertyName) {
		if (propertyName == null || componentName == null) {
			return null;
		}
		Object component = getUserVariables().get(componentName);
		if (component == null) {
			return null;
		}
		String value = getValue(propertyName, component);
		if (value == null) {
			value = "null";
		}
		return value;
	}

	private String formatValue(Object value, String fullPropertyName) {
		if (fullPropertyName != null) {
			String[] split = fullPropertyName.split(".");
			String propertyString = null;
			if (split.length > 0) {
				propertyString = split[split.length - 1];
			} else {
				propertyString = fullPropertyName;
			}
			if (propertyString.startsWith("id")) {
				return FixtureHelper.decodeId(value.toString());
			}
		}

		if (value instanceof LocalDateTime) {
			LocalDateTime result = (LocalDateTime) value;
			return result.toString(FixtureHelper.DateTimePattern);
		} else if (value instanceof LocalDate) {
			LocalDate result = (LocalDate) value;
			return result.toString(FixtureHelper.DatePattern);
		} else if (value instanceof LocalTime) {
			LocalTime result = (LocalTime) value;
			return result.toString(FixtureHelper.TimePattern);
		}
		return value.toString();
	}

	protected String getValue(String propertyName, Object component) {
		Object value = getValue(component, propertyName);
		if (value == null) {
			return null;
		}
		if (Enum.class.isAssignableFrom(value.getClass())) {
			return ((Enum<?>) value).name();
		}
		return formatValue(value, propertyName);
	}

	/**
	 * Gets the value of a property for a component.
	 * 
	 * @param component
	 * @param propertyName
	 * @return Object
	 */
	public Object getValue(Object component, String propertyName) {
		int index = propertyName.indexOf('.');
		if (index == -1) {
			return straightGetProperty(component, propertyName);
		} else {
			String head = propertyName.substring(0, index);
			Object temp = null;
			if (head.contains("[")) {
				Pattern regexPattern = Pattern.compile("(\\w+)\\[(\\w+)=(\\w+)\\]");
				Matcher matcher = regexPattern.matcher(head);
				boolean result = matcher.matches();
				if (result == false) {
					return null;
				} else {
					String property = matcher.group(1);
					String findBy = matcher.group(2);
					String value = matcher.group(3);
					Collection<?> list = (Collection<?>) straightGetProperty(component, property);
					for (Object object : list) {
						String parsedValue = findStringValueForProperty(findBy, value, object);
						if (straightGetProperty(object, findBy).toString().equals(parsedValue)) {
							temp = object;
						}
					}
				}
			} else {
				temp = straightGetProperty(component, head);
				if (!(temp instanceof Object)) {
					return temp;
				}
			}
			if (temp == null) {
				return null;
			}
			return getValue(temp, propertyName.substring(index + 1));
		}
	}

	private String findStringValueForProperty(String property, String value, Object object) {
		return repositorySetup.getTestManager().findStringValueForProperty(property, value, object);
	}

	/**
	 * Find a component and save it.
	 * 
	 * @param componentName
	 *            Test name of the component class
	 * @param idValue
	 *            Value to search (ID, business key,...), the field to search is defined in test configuration
	 * @param variableName
	 *            Name of the variable where the component will be saved.
	 * @return The found component
	 */
	public Object findComponent(String componentName, String idValue, String variableName) {
		if(idValue == null){
			return null;
		}
		Object object = repositorySetup.getTestManager().findObjectAndStorAsVarialble(componentName, idValue);
		if (object != null) {
			getUserVariables().put(variableName, object);
		}
		// TODO: log that object hasn't been found or throw exception
		return object;
	}

	/**
	 * Find a component and save it.
	 *
	 * @param componentName
	 *            Test name of the component class
	 * @param value
	 *            Value to search (ID, business key,...), the field to search is defined in test configuration
	 * @param variableName
	 *            Name of the variable where the component will be saved.
	 * @return The found component
	 */
	public Object findComponent(String componentName, String propertyName, String value, String variableName) {
		if(value == null){
			return null;
		}
		Object object = repositorySetup.getTestManager().findObject(componentName, propertyName, value);
		if (object != null && StringUtils.isNotEmpty(value)) {
			getUserVariables().put(variableName, object);
		}
		// TODO: log that object hasn't been found or throw exception
		return object;
	}

	private Object straightGetProperty(Object object, String property) {
		try {
			return PropertyUtils.getProperty(object, property);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

}

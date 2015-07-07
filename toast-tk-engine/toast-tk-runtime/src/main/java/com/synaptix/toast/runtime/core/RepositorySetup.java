package com.synaptix.toast.runtime.core;

//package com.synpatix.redpepper.backend.core;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.joda.time.Duration;
//import org.joda.time.LocalDate;
//import org.joda.time.LocalDateTime;
//import org.joda.time.LocalTime;
//
//import com.google.inject.Inject;
//import com.mongo.test.domain.impl.test.TestResult;
//import com.mongo.test.domain.impl.test.TestResult.ResultKind;
//import com.synaptix.component.IComponent;
//import com.synaptix.component.factory.ComponentFactory;
//import com.synaptix.entity.IEntity;
//import com.synaptix.entity.IdRaw;
//import com.synaptix.redpepper.automation.elements.impl.DefaultWebPage;
//import com.synaptix.redpepper.commons.init.ITestManager;
//import com.synaptix.service.IComponentService;
//import com.synaptix.service.filter.builder.RootNodeBuilder;
//import com.synpatix.redpepper.backend.helper.FixtureHelper;
//
//
//public class RepositorySetup {
//
//	private final static Log LOG = LogFactory.getLog(RepositorySetup.class);
//
//	@Inject
//	private ITestManager manager;
//
//	@Inject
//	private IComponentService componentService;
//
//	private final Map<String, TestComponentConfig> classesConfig = new HashMap<String, TestComponentConfig>();
//
//	HashMap<String, DefaultWebPage> pages = new HashMap<String, DefaultWebPage>();
//
//	private Map<String, Class<?>> services = new HashMap<String, Class<?>>();
//
//	public RepositorySetup() {
//	}
//
//	/**
//	 * Insert component to database.
//	 *
//	 * @param componentName
//	 *            Name of the component to insert
//	 * @param values
//	 * @return
//	 */
//	public TestResult insertComponent(String componentName, Map<String, String> values) {
//		componentName = FixtureHelper.parseTestString(componentName);
//
//		TestComponentConfig testEntityConfig = classesConfig.get(componentName);
//		if (testEntityConfig != null) {
//			if (testEntityConfig.isDomain) {
//				return insertDomainValue(componentName, values);
//			} else {
//				Class<? extends IComponent> componentClass = testEntityConfig.getComponentClass();
//				if (componentClass != null) {
//					return insertComponent(componentName, values, componentClass);
//				} else {
//					LOG.error("Class not configured: " + componentClass);
//					return new TestResult("Class " + componentClass + " is not configured. Check entity configuration.", ResultKind.ERROR);
//				}
//			}
//		} else {
//			LOG.error("Entity not configured: " + componentName);
//			return new TestResult("Entity not configured: " + componentName);
//		}
//	}
//
//	private TestResult insertComponent(String componentName, Map<String, String> values, Class<? extends IComponent> entityClass) {
//		componentName = FixtureHelper.parseTestString(componentName);
//
//		IComponent component = ComponentFactory.getInstance().createInstance(entityClass);
//
//		// Fill properties
//		for (Entry<String, String> entrySet : values.entrySet()) {
//			String columnName = entrySet.getKey();
//			String propertyName = getTranslatedPropertyName(componentName, columnName);
//			String propertyValue = entrySet.getValue();
//
//			Class<?> propertyClass = component.straightGetPropertyClass(propertyName);
//			if (propertyClass == null) {
//				return new TestResult("Property " + propertyName + " is not configured. Check entity configuration.", ResultKind.ERROR);
//			}
//			if (!component.straightGetPropertyNames().contains(propertyName)) {
//				return new TestResult("Property " + propertyName + " not found in entity " + componentName, ResultKind.ERROR);
//			}
//			boolean columnNameExists = checkThatColumnNameExists(componentName, columnName);
//			if (!columnNameExists) {
//				return new TestResult("Property " + columnName + " not found in configuration for entity " + componentName, ResultKind.ERROR);
//			}
//
//			if (FixtureHelper.isNotEmptyOrNull(propertyValue)) {
//				Object valueToSet = null;
//				try {
//					valueToSet = parseObject(componentName, columnName, propertyValue, propertyClass);
//				} catch (Exception e) {
//					e.printStackTrace();
//					return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
//				}
//
//				if (valueToSet == null) {
//					// The object corresponding to propertyValue was not found
//					return new TestResult("Could not parse property " + columnName + " (class: " + propertyClass + ") of value " + propertyValue, ResultKind.ERROR);
//				} else {
//					if (component.straightGetPropertyClass(propertyName).isAssignableFrom(IdRaw.class) && IEntity.class.isAssignableFrom(valueToSet.getClass())) {
//						Serializable id = ((IEntity) valueToSet).getId();
//						component.straightSetProperty(propertyName, id);
//					} else {
//						component.straightSetProperty(propertyName, valueToSet);
//					}
//				}
//			} else {
//				component.straightSetProperty(propertyName, null);
//			}
//		}
//
//		// Save the entity
//		try {
//			if (component instanceof IEntity) {
//				IEntity entity = (IEntity) component;
//				if (entity.getId() == null) {
//					manager.addEntity(entity);
//				} else {
//					manager.insertEntity(entity);
//				}
//			} else {
//				return new TestResult("This component is not an entity");
//			}
//		} catch (Exception e) {
//			LOG.error("Could not insert entity " + componentName);
//			e.printStackTrace();
//			return new TestResult("Could not insert entity " + componentName + ". [" + ExceptionUtils.getRootCauseMessage(e) + "]", ResultKind.ERROR);
//		}
//		return new TestResult("Done", ResultKind.INFO);
//	}
//
//	/**
//	 * Use the fixtureHelper to parse a string to an object.
//	 */
//	private Object parseObject(String entityName, String propertyName, String propertyValue, Class<?> propertyClass) {
//		Object valueToSet = null;
//
//		if (propertyClass == null) {
//			return null;
//		}
//
//		if (String.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.getString(propertyValue);
//		} else if (Class.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.loadClass(propertyValue);
//		} else if (BigDecimal.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseBigDecimal(propertyValue);
//		} else if (Duration.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseDuration(propertyValue);
//		} else if (Long.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseLong(propertyValue);
//		} else if (Integer.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseInteger(propertyValue);
//		} else if (Double.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseDouble(propertyValue);
//		} else if (LocalDateTime.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseLocalDateTimeFromString(propertyValue);
//		} else if (LocalDate.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseDateFromString(propertyValue);
//		} else if (LocalTime.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseTimeFromString(propertyValue);
//		} else if (Boolean.class.isAssignableFrom(propertyClass) || boolean.class.isAssignableFrom(propertyClass)) {
//			valueToSet = FixtureHelper.parseBoolean(propertyValue);
//		} else if (Enum.class.isAssignableFrom(propertyClass) && !isAssociation(entityName, propertyName)) {
//			valueToSet = selectEnum(propertyClass, propertyValue);
//		} else if (Enum.class.isAssignableFrom(propertyClass) && isAssociation(entityName, propertyName)) {
//			valueToSet = selectEnum(entityName, propertyName, propertyValue);
//		} else if (IEntity.class.isAssignableFrom(propertyClass) && isAssociation(entityName, propertyName)) {
//			valueToSet = selectEntity(entityName, propertyName, propertyValue);
//		} else if (Serializable.class.isInstance(propertyClass) && isAssociation(entityName, propertyName)) {
//			valueToSet = selectEntity(entityName, propertyName, propertyValue);
//		} else if (Serializable.class.isInstance(propertyClass)) {// && propertyName.equals("id")) {
//			valueToSet = FixtureHelper.parseId(propertyValue);
//		}
//
//		return valueToSet;
//	}
//
//	/**
//	 * Get enum value from enum class and string value
//	 *
//	 * @param propertyClass
//	 * @param propertyValue
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	private Object selectEnum(Class<?> propertyClass, String propertyValue) {
//		if (Enum.class.isAssignableFrom(propertyClass)) {
//			@SuppressWarnings("rawtypes")
//			Class<? extends Enum> enumClass = (Class<? extends Enum>) propertyClass;
//			return FixtureHelper.parseEnum(enumClass, propertyValue);
//		}
//		return null;
//	}
//
//	private Object selectEnum(String entityName, String propertyName, String propertyValue) {
//		entityName = FixtureHelper.parseTestString(entityName);
//		propertyName = FixtureHelper.parseTestString(propertyName);
//
//		String entityTypeName = this.classesConfig.get(entityName).propertiesMap.get(propertyName).entityType;
//		if (this.classesConfig.get(entityTypeName) == null) {
//			LOG.error("Entity " + entityTypeName + " associated to " + entityName + " not found.");
//			return null;
//		}
//
//		Class<Enum<?>> enumClass = this.classesConfig.get(entityTypeName).enumClass;
//
//		return FixtureHelper.parseEnum(enumClass, propertyValue);
//	}
//
//	private IComponent selectEntity(String entityName, String propertyName, String propertyValue) {
//		LOG.debug("RepositorySetup.selectEntity() entityName = " + entityName);
//		entityName = FixtureHelper.parseTestString(entityName);
//		propertyName = FixtureHelper.parseTestString(propertyName);
//
//		String entityTypeName = this.classesConfig.get(entityName).propertiesMap.get(propertyName).entityType;
//		if (this.classesConfig.get(entityTypeName) == null) {
//			LOG.error("Entity " + entityTypeName + " associated to " + entityName + " not found.");
//			return null;
//		}
//		String searchBy = this.classesConfig.get(entityTypeName).searchBy;
//		searchBy = this.getTranslatedPropertyName(entityTypeName, searchBy);
//		Class<? extends IComponent> appClass = this.classesConfig.get(entityTypeName).componentClass;
//		Object value = searchBy.equalsIgnoreCase("id") ? FixtureHelper.parseId(propertyValue) : propertyValue;
//		IComponent component = componentService.selectOne(appClass, new RootNodeBuilder().addEqualsPropertyValue(searchBy, value).build());
//		return component;
//	}
//
//	private boolean isAssociation(String entityName, String propertyName) {
//		entityName = FixtureHelper.parseTestString(entityName);
//		propertyName = FixtureHelper.parseTestString(propertyName);
//
//		return this.classesConfig.get(entityName).propertiesMap.get(propertyName).entityType != null;
//	}
//
//	/**
//	 * Configure a new class.
//	 *
//	 * @param className
//	 *            Full class name in the application (i.e. "fr.gefco.tli.psc.ref.model.ICountry").
//	 * @param greenpepperName
//	 *            Name of the class in the test files.
//	 * @param searchBy
//	 * @return
//	 */
//	public TestResult addClass(String className, String greenpepperName, String searchBy) {
//		greenpepperName = FixtureHelper.parseTestString(greenpepperName);
//		searchBy = FixtureHelper.parseTestString(searchBy);
//
//		if (classesConfig.containsKey(greenpepperName)) {
//			return new TestResult("This entity has already been configured", ResultKind.INFO);
//		}
//		classesConfig.put(greenpepperName, new TestComponentConfig(className, searchBy));
//		TestComponentConfig testComponentConfig = classesConfig.get(greenpepperName);
//		if (testComponentConfig != null) {
//			if (testComponentConfig.isError) {
//				return new TestResult(testComponentConfig.error, ResultKind.ERROR);
//			}
//		}
//		return new TestResult("Done", ResultKind.INFO);
//	}
//
//	/**
//	 * Configure a property for a component.
//	 *
//	 * @param testClassName
//	 *            Test name of the component
//	 * @param testPropertyName
//	 *            Test name of the property
//	 * @param appPropertyName
//	 *            Property name in the application
//	 * @param objectType
//	 *            Test name of the component association
//	 * @return
//	 */
//	public TestResult addProperty(String testClassName, String testPropertyName, String appPropertyName, String objectType) {
//		testClassName = FixtureHelper.parseTestString(testClassName);
//		testPropertyName = FixtureHelper.parseTestString(testPropertyName);
//		objectType = FixtureHelper.parseTestString(objectType);
//
//		TestComponentConfig entityConfig = classesConfig.get(testClassName);
//		if (entityConfig != null) {
//			Result result = entityConfig.addProperty(testPropertyName, appPropertyName, objectType);
//			if (result.isSuccess) {
//				return new TestResult("Done", ResultKind.INFO);
//			} else {
//				return new TestResult(result.errorMessage, ResultKind.ERROR);
//			}
//		}
//		return new TestResult("Class has not be configured.");
//	}
//
//	/**
//	 * Try to find the application name for the test name passed in parameter. If not found returns the test name.
//	 *
//	 * @param testClassName
//	 * @param testPropertyName
//	 * @return A String (not null).
//	 */
//	private String getTranslatedPropertyName(String testClassName, String testPropertyName) {
//		testClassName = FixtureHelper.parseTestString(testClassName);
//		testPropertyName = FixtureHelper.parseTestString(testPropertyName);
//
//		TestEntityProperty testEntityProperty = classesConfig.get(testClassName).getFieldNameMap().get(testPropertyName);
//		if (testEntityProperty != null) {
//			return testEntityProperty.getAppName();
//		} else {
//			return testPropertyName;
//		}
//	}
//
//	private TestResult insertDomainValue(String domainTestName, Map<String, String> valueMap) {
//		domainTestName = FixtureHelper.parseTestString(domainTestName);
//
//		if (!classesConfig.containsKey(domainTestName)) {
//			return new TestResult("Domain " + domainTestName + " has not been configured", ResultKind.ERROR);
//		}
//		String tableName = this.classesConfig.get(domainTestName).getTableName();
//		if (tableName == null) {
//			return new TestResult("Table name for domain " + domainTestName + " has not been configured", ResultKind.ERROR);
//		}
//		try {
//			insertType(tableName, valueMap);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new TestResult("Could not insert values in " + domainTestName + ". Exception:\n" + e.getMessage());
//		}
//		return new TestResult("Done", ResultKind.INFO);
//	}
//
//	protected final int insertType(String tableName, Map<String, String> valueMap) throws Exception {
//		return manager.insertType(tableName, valueMap);
//	}
//
//	/**
//	 * Checks if a column is correctly setup (with config entity table).
//	 *
//	 * @param entityName
//	 * @param content
//	 */
//	public boolean checkThatColumnNameExists(String entityName, String content) {
//		entityName = FixtureHelper.parseTestString(entityName);
//		content = FixtureHelper.parseTestString(content);
//
//		if (classesConfig.containsKey(entityName)) {
//			if (classesConfig.get(entityName).propertiesMap.containsKey(content)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public boolean checkThatEntityExists(String entityName) {
//		return classesConfig.containsKey(FixtureHelper.parseTestString(entityName));
//	}
//
//	public TestResult addDomain(String domainClassName, String domainTestName, String tableName) {
//		domainTestName = FixtureHelper.parseTestString(domainTestName);
//
//		TestComponentConfig config = new TestComponentConfig(domainClassName);
//		config.setTableName(tableName);
//		classesConfig.put(domainTestName, config);
//		if (config.enumClass == null) {
//			return new TestResult("Failed", ResultKind.ERROR);
//		}
//		return new TestResult("OK", ResultKind.INFO);
//	}
//
//	public Class<? extends IComponent> getComponentClassFullNameFromTestName(String testName) {
//		testName = FixtureHelper.parseTestString(testName);
//
//		TestComponentConfig config = this.classesConfig.get(testName);
//		if (config != null) {
//			return config.componentClass;
//		}
//		return null;
//	}
//
//	public String getComponentClassSearchByFromTestName(String testName) {
//		testName = FixtureHelper.parseTestString(testName);
//
//		TestComponentConfig config = this.classesConfig.get(testName);
//		if (config != null) {
//			return config.searchBy;
//		}
//		return null;
//	}
//
//	public void addPage(String entityName) {
//		pages.put(entityName, new DefaultWebPage());
//	}
//
//	public DefaultWebPage getPage(String entityName) {
//		return pages.get(entityName);
//	}
//
//	public Collection<DefaultWebPage> getWebPages() {
//		return pages.values();
//	}
//
//	/**
//	 * @param testName
//	 * @param className
//	 */
//	public TestResult addService(String testName, String className) {
//		testName = FixtureHelper.parseTestString(testName);
//
//		Class<?> forName;
//		try {
//			forName = Class.forName(className);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return new TestResult("Class not found", ResultKind.ERROR);
//		}
//		services.put(testName, forName);
//		return new TestResult("Done", ResultKind.INFO);
//	}
//
//	public Class<?> getService(String testName) {
//		return services.get(FixtureHelper.parseTestString(testName));
//	}
//
//	/**
//	 *
//	 */
//	public void clean() {
//		classesConfig.clear();
//		services.clear();
//		pages.clear();
//	}
//
//	private class TestComponentConfig { // A découper en 2 classes, une pour les entités, une pour les domaines
//		private Class<? extends IComponent> componentClass;
//		private Class<Enum<?>> enumClass;
//		private String searchBy;
//		private Map<String, TestEntityProperty> propertiesMap;
//		private boolean isDomain;
//		private String tableName;
//		private boolean isError;
//		private String error;
//
//		/**
//		 * Constructor for entities configuration.
//		 *
//		 * @param appClassName
//		 * @param searchBy
//		 */
//		@SuppressWarnings("unchecked")
//		public TestComponentConfig(String appClassName, String searchBy) {
//			this.propertiesMap = new HashMap<String, RepositorySetup.TestEntityProperty>();
//			this.searchBy = searchBy;
//			this.isDomain = false;
//			this.isError = false;
//			this.error = null;
//			Class<?> entityClass = null;
//			try {
//				entityClass = Class.forName(appClassName);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				isError = true;
//				error = "Class not found. Please check entity configuration.";
//			}
//			if (entityClass != null && IEntity.class.isAssignableFrom(entityClass)) {
//				this.componentClass = (Class<? extends IEntity>) entityClass;
//			}
//		}
//
//		/**
//		 * Constructor for domains configuration.
//		 *
//		 * @param appClassName
//		 */
//		public TestComponentConfig(String appClassName) {
//			this.propertiesMap = new HashMap<String, RepositorySetup.TestEntityProperty>();
//			this.isDomain = true;
//			Class<? extends IComponent> domainInterfaceClass = null;
//			try {
//				domainInterfaceClass = Class.forName(appClassName).asSubclass(IComponent.class);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//			if (domainInterfaceClass != null) {
//				enumClass = getEnumSubClass(domainInterfaceClass);
//
//				componentClass = domainInterfaceClass;
//				addProperty("code", "code", null);
//				addProperty("meaning", "meaning", null);
//			}
//		}
//
//		@SuppressWarnings("unchecked")
//		private Class<Enum<?>> getEnumSubClass(Class<?> domainClass) {
//			Class<?>[] classes = domainClass.getClasses();
//			for (Class<?> class1 : classes) {
//				if (class1.isEnum()) {
//					return (Class<Enum<?>>) class1;
//				}
//			}
//			return null;
//		}
//
//		/**
//		 * Returns false if the property of name "appPropertyName" exists in the component. Else add the property in the map.
//		 *
//		 * @param testPropertyName
//		 * @param appPropertyName
//		 * @param objectType
//		 * @return
//		 */
//		public Result addProperty(String testPropertyName, String appPropertyName, String objectType) {
//			IComponent component = ComponentFactory.getInstance().createInstance(componentClass);
//			if (objectType != null && !classesConfig.containsKey(objectType)) {
//				return new Result(false, "Entity " + objectType + " not configured.");
//			}
//			if (component.straightGetProperties().containsKey(appPropertyName)) {
//				propertiesMap.put(testPropertyName, new TestEntityProperty(testPropertyName, appPropertyName, objectType));
//				return new Result();
//			} else {
//				return new Result(false, "The property " + appPropertyName + " does not exist in " + appPropertyName);
//			}
//		}
//
//		public Map<String, TestEntityProperty> getFieldNameMap() {
//			return propertiesMap;
//		}
//
//		public Class<? extends IComponent> getComponentClass() {
//			return componentClass;
//		}
//
//		public String getTableName() {
//			return tableName;
//		}
//
//		public void setTableName(String tableName) {
//			this.tableName = tableName;
//		}
//	}
//
//	public class TestEntityProperty {
//
//		private final String testName;
//		private final String appName;
//		private final String entityType;
//
//		public TestEntityProperty(String testName, String appName, String entityType) {
//			this.testName = testName;
//			this.appName = appName;
//			this.entityType = entityType;
//		}
//
//		public String getTestName() {
//			return testName;
//		}
//
//		public String getAppName() {
//			return appName;
//		}
//
//		public String getEntityType() {
//			return entityType;
//		}
//	}
//
//	private class Result {
//		private String errorMessage;
//		private boolean isSuccess;
//
//		public Result(boolean isSuccess, String errorMessage) {
//			this.isSuccess = isSuccess;
//			this.errorMessage = errorMessage;
//		}
//
//		public Result() {
//			this.isSuccess = true;
//			this.errorMessage = null;
//		}
//	}
// }

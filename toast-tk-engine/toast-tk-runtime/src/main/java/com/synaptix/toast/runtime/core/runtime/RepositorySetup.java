package com.synaptix.toast.runtime.core.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.toast.adapter.swing.DefaultSwingPage;
import com.synaptix.toast.adapter.utils.ActionAdapterHelper;
import com.synaptix.toast.adapter.web.DefaultWebPage;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.core.runtime.IFeedableSwingPage;
import com.synaptix.toast.core.runtime.IFeedableWebPage;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.runtime.core.runtime.utils.ClassHelper;
import com.synaptix.toast.runtime.guice.IRepositoryTypeParser;

public class RepositorySetup implements IRepositorySetup {

	private static final Log LOG = LogFactory.getLog(RepositorySetup.class);

	private ITestManager manager;

	private final Map<String, TestComponentConfig> classesConfig;

	HashMap<String, IFeedableWebPage> pages = new HashMap<String, IFeedableWebPage>();

	HashMap<String, IFeedableSwingPage> swingpages = new HashMap<String, IFeedableSwingPage>();

	private final Map<String, Class<?>> services = new HashMap<String, Class<?>>();

	private final Set<IRepositoryTypeParser> typeHandlers;

	private final ClassConfigProvider classConfigProvider;

	public Map<String, Object> getUserVariables() {
		return userVariables;
	}

	public void setUserVariables(
		Map<String, Object> userVariables) {
		this.userVariables = userVariables;
	}

	private Map<String, Object> userVariables;

	public void setTestManager(
		ITestManager manager) {
		this.manager = manager;
	}

	public ITestManager getTestManager() {
		return manager;
	}

	@Inject
	public RepositorySetup(
		Set<IRepositoryTypeParser> typeHandlers,
		ClassConfigProvider classConfigProvider) {
		this.typeHandlers = typeHandlers;
		this.classesConfig = classConfigProvider.getConfigMap();
		this.classConfigProvider = classConfigProvider;
		userVariables = new HashMap<String, Object>();
	}

	/**
	 * Insert component to database.
	 *
	 * @param componentName Name of the component to insert
	 * @param values
	 * @return
	 */
	public TestResult insertComponent(
		String componentName,
		Map<String, String> values) {
		componentName = ActionAdapterHelper.parseTestString(componentName);
		TestComponentConfig testEntityConfig = classesConfig.get(componentName);
		if(testEntityConfig != null) {
			if(testEntityConfig.isDomain) {
				return insertDomainValue(componentName, values);
			}
			else {
				Class<?> componentClass = testEntityConfig.getComponentClass();
				if(componentClass != null) {
					return insertComponent(componentName, values, componentClass);
				}
				else {
					LOG.error("Class not configured: " + componentClass);
					return new TestResult(
						"Class " + componentClass + " is not configured. Check entity configuration.", ResultKind.ERROR);
				}
			}
		}
		else {
			LOG.error("Entity not configured: " + componentName);
			return new TestResult("Entity not configured: " + componentName);
		}
	}

	private TestResult insertComponent(
		String componentName,
		Map<String, String> values,
		Class<?> entityClass) {
		componentName = ActionAdapterHelper.parseTestString(componentName);
		// Fill properties
		Map<String, Object> valueMap = new HashMap<String, Object>();
		for(Entry<String, String> entrySet : values.entrySet()) {
			String columnName = entrySet.getKey();
			String propertyName = classConfigProvider.getTranslatedPropertyName(componentName, columnName);
			String propertyValue = entrySet.getValue();
			Class<?> propertyClass = ClassHelper.getPropertyClass(entityClass, propertyName);
			if(propertyClass == null) {
				return new TestResult("Property " + propertyName
					+ " is not configured or not found. Check entity configuration.", ResultKind.ERROR);
			}
			boolean columnNameExists = checkThatColumnNameExists(componentName, columnName);
			if(!columnNameExists) {
				return new TestResult("Property " + columnName + " not found in configuration for entity "
					+ componentName, ResultKind.ERROR);
			}
			Object valueToSet = null;
			if(ActionAdapterHelper.isNotEmptyOrNull(propertyValue)) {
				try {
					valueToSet = parseObject(componentName, columnName, propertyValue, propertyClass);
				}
				catch(Exception e) {
					e.printStackTrace();
					return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
				}
				if(valueToSet == null) {
					// The object corresponding to propertyValue was not found
					return new TestResult("Could not parse property " + columnName + " (class: " + propertyClass
						+ ") of value " + propertyValue, ResultKind.ERROR);
				}
			}
			valueMap.put(propertyName, valueToSet);
		}
		// Save the entity
		try {
			manager.saveEntity(entityClass, valueMap);
		}
		catch(Exception e) {
			LOG.error("Could not insert entity " + componentName);
			e.printStackTrace();
			return new TestResult("Could not insert entity " + componentName + ". ["
				+ ExceptionUtils.getRootCauseMessage(e) + "]", ResultKind.ERROR);
		}
		return new TestResult("Done", ResultKind.INFO);
	}

	/**
	 * Use the fixtureHelper to parse a string to an object.
	 */
	private Object parseObject(
		String entityName,
		String propertyName,
		String propertyValue,
		Class<?> propertyClass) {
		Object valueToSet = null;
		if(propertyClass == null) {
			return null;
		}
		for(IRepositoryTypeParser parser : typeHandlers) {
			if(parser.hanldeClass(propertyClass)) {
				valueToSet = parser.parse(propertyClass, entityName, propertyName, propertyValue);
			}
		}
		return valueToSet;
	}

	/**
	 * Select an entity referenced by a property in an other entity.
	 * <p/>
	 *
	 * @param entityName    Test name of entity class containing the property.
	 * @param propertyName  Test name of entity property.
	 * @param propertyValue Value to select.
	 * @return
	 */
	public Object selectEntity(
		String entityName,
		String propertyName,
		String propertyValue) {
		return manager.findObject(entityName, propertyName, propertyValue);
	}

	/**
	 * Configure a new class.
	 *
	 * @param className       Full class name in the application (i.e. "fr.gefco.tli.psc.ref.model.ICountry").
	 * @param greenpepperName Name of the class in the test files.
	 * @param searchBy
	 * @return
	 */
	public TestResult addClass(
		String className,
		String greenpepperName,
		String searchBy) {
		greenpepperName = ActionAdapterHelper.parseTestString(greenpepperName);
		searchBy = ActionAdapterHelper.parseTestString(searchBy);
		if(classesConfig.containsKey(greenpepperName)) {
			return new TestResult("This entity has already been configured", ResultKind.INFO);
		}
		classesConfig.put(greenpepperName, new TestComponentConfig(className, searchBy));
		TestComponentConfig testComponentConfig = classesConfig.get(greenpepperName);
		if(testComponentConfig != null) {
			if(testComponentConfig.isError) {
				return new TestResult(testComponentConfig.error, ResultKind.ERROR);
			}
		}
		return new TestResult("Done", ResultKind.INFO);
	}

	/**
	 * Configure a property for a component.
	 *
	 * @param testClassName    Test name of the component
	 * @param testPropertyName Test name of the property
	 * @param appPropertyName  Property name in the application
	 * @param objectType       Test name of the component association
	 * @return
	 */
	public TestResult addProperty(
		String testClassName,
		String testPropertyName,
		String appPropertyName,
		String objectType) {
		testClassName = ActionAdapterHelper.parseTestString(testClassName);
		testPropertyName = ActionAdapterHelper.parseTestString(testPropertyName);
		objectType = ActionAdapterHelper.parseTestString(objectType);
		TestComponentConfig entityConfig = classesConfig.get(testClassName);
		if(entityConfig != null) {
			if(entityConfig.addProperty(testPropertyName, appPropertyName, objectType)) {
				return new TestResult("Done", ResultKind.INFO);
			}
			else {
				return new TestResult("The property " + appPropertyName + " does not exist in "
					+ entityConfig.componentClass, ResultKind.ERROR);
			}
		}
		return new TestResult("Class has not be configured.");
	}

	public class TestComponentConfig { // A découper en 2 classes, une pour les
// entités, une pour les domaines

		public Class<?> componentClass;

		public Class<Enum<?>> enumClass;

		public String searchBy;

		public final Map<String, TestEntityProperty> propertiesMap;

		private final boolean isDomain;

		private String tableName;

		private boolean isError;

		private String error;

		/**
		 * Constructor for entities configuration.
		 *
		 * @param appClassName
		 * @param searchBy
		 */
		@SuppressWarnings("unchecked")
		public TestComponentConfig(
			String appClassName,
			String searchBy) {
			this.propertiesMap = new HashMap<String, RepositorySetup.TestEntityProperty>();
			this.searchBy = searchBy;
			this.isDomain = false;
			this.isError = false;
			this.error = null;
			Class<?> entityClass = null;
			try {
				entityClass = Class.forName(appClassName);
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
				isError = true;
				error = "Class not found. Please check entity configuration.";
			}
			if(entityClass != null/* && IEntity.class.isAssignableFrom(entityClass) */) {
				this.componentClass = entityClass;
			}
		}

		/**
		 * Constructor for domains configuration.
		 *
		 * @param appClassName
		 */
		public TestComponentConfig(
			String appClassName) {
			this.propertiesMap = new HashMap<String, RepositorySetup.TestEntityProperty>();
			this.isDomain = true;
			Class<?> domainInterfaceClass = null;
			try {
				domainInterfaceClass = Class.forName(appClassName);
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(domainInterfaceClass != null) {
				enumClass = getEnumSubClass(domainInterfaceClass);
				componentClass = domainInterfaceClass;
				addProperty("code", "code", null);
				addProperty("meaning", "meaning", null);
			}
		}

		@SuppressWarnings("unchecked")
		private Class<Enum<?>> getEnumSubClass(
			Class<?> domainClass) {
			Class<?>[] classes = domainClass.getClasses();
			for(Class<?> class1 : classes) {
				if(class1.isEnum()) {
					return (Class<Enum<?>>) class1;
				}
			}
			return null;
		}

		/**
		 * Returns false if the property of name "appPropertyName" exists in the component. Else add the property in the map.
		 *
		 * @param testPropertyName
		 * @param appPropertyName
		 * @param objectType
		 * @return
		 */
		public boolean addProperty(
			String testPropertyName,
			String appPropertyName,
			String objectType) {
			if(ClassHelper.hasProperty(componentClass, appPropertyName)) {
				propertiesMap.put(testPropertyName, new TestEntityProperty(testPropertyName, appPropertyName,
					objectType));
				return true;
			}
			else {
				return false;
			}
		}

		public Map<String, TestEntityProperty> getFieldNameMap() {
			return propertiesMap;
		}

		public Class<?> getComponentClass() {
			return componentClass;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(
			String tableName) {
			this.tableName = tableName;
		}
	}

	public class TestEntityProperty {

		private final String testName;

		final String appName;

		public final String entityType;

		public TestEntityProperty(
			String testName,
			String appName,
			String entityType) {
			this.testName = testName;
			this.appName = appName;
			this.entityType = entityType;
		}
	}

	private TestResult insertDomainValue(
		String domainTestName,
		Map<String, String> valueMap) {
		domainTestName = ActionAdapterHelper.parseTestString(domainTestName);
		if(!classesConfig.containsKey(domainTestName)) {
			return new TestResult("Domain " + domainTestName + " has not been configured", ResultKind.ERROR);
		}
		String tableName = this.classesConfig.get(domainTestName).getTableName();
		if(tableName == null) {
			return new TestResult("Table name for domain " + domainTestName + " has not been configured",
				ResultKind.ERROR);
		}
		try {
			insertType(tableName, valueMap);
		}
		catch(Exception e) {
			e.printStackTrace();
			return new TestResult("Could not insert values in " + domainTestName + ". Exception:\n" + e.getMessage());
		}
		return new TestResult("Done", ResultKind.INFO);
	}

	protected final int insertType(
		String tableName,
		Map<String, String> valueMap)
		throws Exception {
		return manager.insertType(tableName, valueMap);
	}

	/**
	 * Checks if a column is correctly setup (with config entity table).
	 *
	 * @param entityName
	 * @param content
	 */
	public boolean checkThatColumnNameExists(
		String entityName,
		String content) {
		entityName = ActionAdapterHelper.parseTestString(entityName);
		content = ActionAdapterHelper.parseTestString(content);
		if(classesConfig.containsKey(entityName)) {
			if(classesConfig.get(entityName).propertiesMap.containsKey(content)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkThatEntityExists(
		String entityName) {
		return classesConfig.containsKey(ActionAdapterHelper.parseTestString(entityName));
	}

	public TestResult addDomain(
		String domainClassName,
		String domainTestName,
		String tableName) {
		domainTestName = ActionAdapterHelper.parseTestString(domainTestName);
		TestComponentConfig config = new TestComponentConfig(domainClassName);
		config.setTableName(tableName);
		classesConfig.put(domainTestName, config);
		if(config.enumClass == null) {
			return new TestResult("Failed", ResultKind.ERROR);
		}
		return new TestResult("OK", ResultKind.INFO);
	}

	public Class<?> getComponentClassFullNameFromTestName(
		String testName) {
		testName = ActionAdapterHelper.parseTestString(testName);
		TestComponentConfig config = this.classesConfig.get(testName);
		if(config != null) {
			return config.componentClass;
		}
		return null;
	}

	public String getComponentClassSearchByFromTestName(
		String testName) {
		testName = ActionAdapterHelper.parseTestString(testName);
		TestComponentConfig config = this.classesConfig.get(testName);
		if(config != null) {
			return config.searchBy;
		}
		return null;
	}

	public String getPropertyName(
		String className,
		String testName) {
		TestComponentConfig testComponentConfig = this.classesConfig.get(testName);
		if(testComponentConfig != null) {
			TestEntityProperty testEntityProperty = testComponentConfig.propertiesMap.get(testName);
			if(testEntityProperty != null) {
				return testEntityProperty.appName;
			}
		}
		return null;
	}

	public String getPropertyType(
		String className,
		String testName) {
		TestComponentConfig testComponentConfig = this.classesConfig.get(testName);
		if(testComponentConfig != null) {
			TestEntityProperty testEntityProperty = testComponentConfig.propertiesMap.get(testName);
			if(testEntityProperty != null) {
				return testEntityProperty.entityType;
			}
		}
		return null;
	}

	public void addPage(
		String entityName) {
		pages.put(entityName, new DefaultWebPage());
	}

	public void addSwingPage(
		String fixtureName) {
		swingpages.put(fixtureName, new DefaultSwingPage());
	}

	@Override
	public IFeedableSwingPage getSwingPage(
		String entityName) {
		return swingpages.get(entityName);
	}

	public IFeedableWebPage getPage(
		String entityName) {
		return pages.get(entityName);
	}

	@Override
	public Collection<IFeedableSwingPage> getSwingPages() {
		return swingpages.values();
	}

	@Override
	public Collection<IFeedableWebPage> getWebPages() {
		return pages.values();
	}

	/**
	 * @param type
	 * @param className
	 */
	public TestResult addService(
		String type,
		String className) {
		type = ActionAdapterHelper.parseTestString(type);
		Class<?> forName;
		try {
			forName = Class.forName(className);
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			return new TestResult("Class not found", ResultKind.ERROR);
		}
		services.put(type, forName);
		return new TestResult("Done", ResultKind.INFO);
	}

	public Class<?> getService(
		String testName) {
		return services.get(ActionAdapterHelper.parseTestString(testName));
	}

	/**
	 *
	 */
	public void clean() {
		classesConfig.clear();
		services.clear();
		pages.clear();
		swingpages.clear();
	}
}
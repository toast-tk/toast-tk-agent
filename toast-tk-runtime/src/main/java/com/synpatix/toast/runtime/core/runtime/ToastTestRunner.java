/**
 * 
 */
package com.synpatix.toast.runtime.core.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.IFeedableSwingPage;
import com.synaptix.toast.core.IRepositorySetup;
import com.synaptix.toast.core.ITestManager;
import com.synaptix.toast.core.annotation.Check;
import com.synaptix.toast.core.annotation.Display;
import com.synaptix.toast.core.annotation.FixtureKind;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.inspection.ISwingInspectionClient;
import com.synaptix.toast.core.setup.TestResult;
import com.synaptix.toast.core.setup.TestResult.ResultKind;
import com.synaptix.toast.dao.domain.impl.test.ComponentConfigLine;
import com.synaptix.toast.dao.domain.impl.test.SwingPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.TestLine;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.WebPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;
import com.synaptix.toast.dao.domain.impl.test.block.ConfigBlock;
import com.synaptix.toast.dao.domain.impl.test.block.InsertBlock;
import com.synaptix.toast.dao.domain.impl.test.block.SetupBlock;
import com.synaptix.toast.dao.domain.impl.test.block.SwingPageBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;
import com.synaptix.toast.dao.report.HtmlReportGenerator;
import com.synaptix.toast.fixture.api.FixtureApi;
import com.synaptix.toast.fixture.api.FixtureService;
import com.synaptix.toast.fixture.web.DefaultWebPage;

/**
 * @author E413544
 * 
 */
public class ToastTestRunner {

	private static final Logger LOG = LoggerFactory.getLogger(ToastTestRunner.class);
	private static final HtmlReportGenerator htmlReportGenerator = new HtmlReportGenerator();
	private final ITestManager testManager;
	private final IRepositorySetup repoSetup;
	private URL settingsFile;
	private Injector injector;
	private IReportUpdateCallBack reportUpdateCallBack;
	private List<FixtureService> fixtureApiServices;
	
	public ToastTestRunner(ITestManager m, Injector injector, URL settingsFile) {
		this(m, injector.getInstance(IRepositorySetup.class));
		this.injector = injector;
		this.settingsFile = settingsFile;
		if(settingsFile != null){
			LOG.info("Overriding fixture definitions with settings in " + settingsFile.getFile());
		}
		this.fixtureApiServices = FixtureApi.listAvailableServices();
	}
	
	public ToastTestRunner(ITestManager testManager, IRepositorySetup repoSetup) {
		this.testManager = testManager;
		this.repoSetup = repoSetup;
		this.fixtureApiServices = FixtureApi.listAvailableServices();
	}

	public ToastTestRunner(ITestManager testEnvManager, Injector injector, URL settings, IReportUpdateCallBack reportUpdateCallBack) {
		this(testEnvManager, injector, settings);
		this.reportUpdateCallBack = reportUpdateCallBack;
		this.fixtureApiServices = FixtureApi.listAvailableServices();
	}

	/**
	 * @param result
	 * @return
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException 
	 */
	public TestPage run(TestPage testPage, boolean inlineReport) throws IllegalAccessException, ClassNotFoundException {

		testPage.startExecution();
		for (IBlock block : testPage.getBlocks()) {
			if (block instanceof CommentBlock) {
				// runCommentBlock((CommentBlock) block, report);
			}
			else if (block instanceof SetupBlock) {
				runSetupBlock((SetupBlock) block, testPage);
			} 
			else if (block instanceof ConfigBlock) {
				runConfigBlock((ConfigBlock) block, testPage);
			} 
			else if (block instanceof InsertBlock) {
				runInsertBlock((InsertBlock) block, testPage);
			}
			else if (block instanceof WebPageBlock) {
				runWebPageBlock((WebPageBlock) block, testPage);
			} 
			else if (block instanceof SwingPageBlock) {
				runSwingPageBlock((SwingPageBlock) block, testPage);			
			}
			else if (block instanceof TestBlock) {
				runTestBlock((TestBlock) block, testPage, inlineReport);
			} 
			else if (block instanceof TestPage) {
				runTestPage((TestPage) block, testPage, inlineReport);
			} 
		}
		testPage.stopExecution();
		if (testPage.getParsingErrorMessage() != null) {
			LOG.info(testPage.getParsingErrorMessage());
		}
		return testPage;
	}
	
	private void runSwingPageBlock(SwingPageBlock block, TestPage testPage) {
		repoSetup.addSwingPage(block.getFixtureName());
		IFeedableSwingPage page = repoSetup.getSwingPage(block.getFixtureName());
		for (SwingPageConfigLine line : block.getBlockLines()) {
			page.addElement(line.getElementName(), line.getType(), line.getLocator());
		}		
	}
	
	/**
	 * @param block
	 * @param testPage
	 */
	private void runWebPageBlock(WebPageBlock block, TestPage testPage) {
		repoSetup.addPage(block.getFixtureName());
		DefaultWebPage page = (DefaultWebPage) repoSetup.getPage(block.getFixtureName());
		for (WebPageConfigLine line : block.getBlockLines()) {
			page.addElement(line.getElementName(), line.getType(), line.getMethod(), line.getLocator(), line.getPosition());
		}
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runInsertBlock(InsertBlock block, TestPage testPage) {
		String entityName = block.getComponentName();

		// Get row values
		for (BlockLine line : block.getBlockLines()) {
			Map<String, String> values = new HashMap<String, String>();
			for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
				String cell = line.getCellAt(cellIndex);
				values.put(block.getColumns().getCellAt(cellIndex), cell);
			}

			TestResult result = insertEntity(entityName, values);
			line.setTestResult(result);
			testPage.addResult(result);
		}
	}

	private TestResult insertEntity(String entityName2, Map<String, String> values2) {
		if(LOG.isDebugEnabled()){
			LOG.info("Insert entity: " + entityName2 + " [" + values2 + "]");
		}
		TestResult result;
		try {
			result = repoSetup.insertComponent(entityName2, values2);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
		}
		return result;
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runConfigBlock(ConfigBlock block, TestPage testPage) {
		for (ComponentConfigLine line : block.getLines()) {
			TestResult result = repoSetup.addProperty(block.getComponentName(), line.getTestName(), line.getSystemName(), line.getComponentAssociation());
			line.setResult(result);
			testPage.addResult(result);
		}
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runSetupBlock(SetupBlock block, TestPage testPage) {
		// Get fixture name
		String fixtureName = block.getFixtureName();

		// Get columns names
		List<String> columnsList = new ArrayList<String>();
		for (String cell : block.getColumns().getCells()) {
			if (cell.contains(" ")) {
				cell = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, cell.replace(" ", "_"));
			}
			columnsList.add(cell);
		}

		// Get values
		for (BlockLine row : block.getBlockLines()) {

			TestResult result;
			if (fixtureName.equals("domain")) {
				result = addDomain(row.getCellAt(0), row.getCellAt(1), row.getCellAt(2));
				row.setTestResult(result);
				testPage.addResult(result);
			} else if (fixtureName.equals("entity")) {
				result = addEntity(row.getCellAt(1), row.getCellAt(0), row.getCellAt(2));
				row.setTestResult(result);
				testPage.addResult(result);
			} else if (fixtureName.equals("service")) {
				result = addService(row.getCellAt(0), row.getCellAt(1));
				row.setTestResult(result);
				testPage.addResult(result);
			} else {
				Class<?> fixtureClass = repoSetup.getService(fixtureName);
				if (fixtureClass == null) {
					block.setTestResult(new TestResult("Fixture " + fixtureName + " not found", ResultKind.ERROR));
					return;
				}
				Object instance = getClassInstance(fixtureClass);
				for (int cellIndex = 0; cellIndex < row.getCells().size(); cellIndex++) {
					String cell = row.getCellAt(cellIndex);
					try {
						fixtureClass.getField(columnsList.get(cellIndex)).set(instance, cell);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
						row.setTestResult(new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR));
						return;
					}
				}
				try {
					fixtureClass.getMethod("enterRow").invoke(instance);
					row.setTestResult(new TestResult("Done", ResultKind.INFO));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					row.setTestResult(new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR));
				}
			}
		}
		for (BlockLine row : block.getBlockLines()) {
			testPage.addResult(row.getTestResult());
		}
		return;
	}

	private Object getClassInstance(Class<?> clz){
		if(injector != null){
			try{
				return injector.getInstance(clz);
			}catch(ConfigurationException _ce){
				return null;
			}
		}
		return testManager.getClassInstance(clz);
	}
	
	private TestResult addDomain(String domainTestName, String domainClassName, String tableName) {
		if(LOG.isDebugEnabled()){
			LOG.info("Add domain: [" + domainTestName + "] [" + domainClassName + "] [" + tableName + "]");
		}
		return repoSetup.addDomain(domainClassName, domainTestName, tableName);
	}

	private TestResult addEntity(String className, String testName, String searchBy) {
		return repoSetup.addClass(className, testName, searchBy);
	}

	private TestResult addService(String testName, String className) {
		return repoSetup.addService(testName, className);
	}

	/**
	 * @param block
	 * @param testPage
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException 
	 */
	private void runTestPage(TestPage block, TestPage testPage, boolean inlineReport) throws IllegalAccessException, ClassNotFoundException {
		this.run(block, inlineReport);
	}

	/**
	 * @param block
	 * @param testPage
	 * @param inlineReport 
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException 
	 */
	private void runTestBlock(TestBlock block, TestPage testPage, boolean inlineReport) throws IllegalAccessException, ClassNotFoundException {
		
		for (TestLine line : block.getBlockLines()) {
			line.startExecution();
			
			//override with test line call
			TestLineDescriptor descriptor = new TestLineDescriptor(block, line);
			TestResult result = parseServiceCall(descriptor);
			
			line.stopExecution();
			if ("KO".equals(line.getExpected()) && ResultKind.FAILURE.equals(result.getResultKind())) {
				result.setResultKind(ResultKind.SUCCESS);
			}
			if (result.getMessage() != null && line.getExpected() != null && result.getMessage().equals(line.getExpected())) {
				result.setResultKind(ResultKind.SUCCESS);
			}
			line.setTestResult(result);
			testPage.addResult(result);
			if(inlineReport){
				String generatePageHtml = htmlReportGenerator.generatePageHtml(testPage);
				URL resource = this.getClass().getClassLoader() != null ? this.getClass().getClassLoader().getResource("TestResult") : null;
				if(resource != null){
					htmlReportGenerator.writeFile(generatePageHtml, testPage.getPageName(), resource.getPath());
				}
				if(reportUpdateCallBack != null){
					reportUpdateCallBack.onUpdate(generatePageHtml);
				}
			}
			if(ResultKind.FATAL.equals(result.getResultKind())){
				if(reportUpdateCallBack != null){
					reportUpdateCallBack.onFatalStepError(result.getMessage());
				}
				throw new IllegalAccessException("Test execution stopped, due to fail fatal step: " + line + " - Failed !");
			}
		}

	}
	
	

	/**
	 * 
	 * DOCUMENT THIS METHOD
	 * 
	 * @param descriptor 
	 * @return
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private TestResult parseServiceCall(TestLineDescriptor descriptor) throws IllegalAccessException, ClassNotFoundException {
		TestResult result = null;
		String command = descriptor.getCommand();
		
		// Locating service class ////////////////////////////////////
		Class<?> localFixtureClass = locateFixtureClass(descriptor.getTestLineFixtureKind(), descriptor.getTestLineFixtureName(), command); 
		if(localFixtureClass != null){
			Object connector = getClassInstance(localFixtureClass);
			FixtureExecCommandDescriptor commandMethodImpl = findMethodInClass(command, localFixtureClass);
			result = doLocalFixtureCall(connector, commandMethodImpl);
		}
		else if(getClassInstance(ISwingInspectionClient.class) != null){
			// If no class is implementing the command then 
			// process it as a custom command sent through Kryo 
			result = doRemoteFixtureCall(command, descriptor);
		}else{
			result = new TestResult(String.format("Method not found"), ResultKind.ERROR);
		}
		
		if(descriptor.isFailFatalCommand()){
			if(!result.isSuccess()){
				result.setResultKind(ResultKind.FATAL);
			}
		}
		
		return result;
		
	}

	private TestResult doRemoteFixtureCall(String command, TestLineDescriptor descriptor) {
		TestResult result;
		ISwingInspectionClient swingClient = (ISwingInspectionClient) getClassInstance(ISwingInspectionClient.class);
		swingClient.processCustomCommand(buildCommandRequest(command, descriptor));
		
		if(LOG.isDebugEnabled()){
			LOG.debug("Client Plugin Mode: Delegating command interpretation to server plugins !");
		}
		result = new TestResult("Client Plugin Mode: Delegating command interpretation to server plugins !", ResultKind.INFO);
		return result;
	}

	private TestResult doLocalFixtureCall(Object instance, FixtureExecCommandDescriptor fixtureExecDescriptor) {
		TestResult result;
		
		Matcher matcher = fixtureExecDescriptor.matcher;
		matcher.matches();
		
		int groupCount = matcher.groupCount();
		Object[] args = new Object[groupCount];
		for (int i = 0; i < groupCount; i++) {
			args[i] = matcher.group(i + 1);
		}

		try {
			result = (TestResult) fixtureExecDescriptor.method.invoke(instance, args);
		} catch (Exception e) {
			LOG.error("Error found !", e);
			result = new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.FAILURE);
		}
		return result;
	}

	private CommandRequest buildCommandRequest(String command, TestLineDescriptor descriptor) {
		final CommandRequest commandRequest;
		switch (descriptor.getTestLineFixtureKind()) {
		case service:
			commandRequest = new CommandRequest.CommandRequestBuilder(null).ofType(FixtureKind.service.name()).asCustomCommand(command).build();
			break;
		default:
			commandRequest = new CommandRequest.CommandRequestBuilder(null).asCustomCommand(command).build();
			break;
		}
		return commandRequest;
	}

	/**
	 * DOCUMENT
	 * 
	 * @param fixture kind (swing, web, service)
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException 
	 */
	private Class<?> locateFixtureClass(FixtureKind fixtureKind, String fixtureName, String command) throws ClassNotFoundException, IllegalAccessException {
		List<Class<?>> serviceClasses = new ArrayList<Class<?>>();
		if(settingsFile != null){
			Class<?> serviceClass = getServiceClassFromSettings(settingsFile.getFile(), fixtureKind.name());
			if(serviceClass != null){
				LOG.info(String.format("Identified a new service class ( %s ) in setting file !", serviceClass));
				serviceClasses.add(serviceClass);
			}
		}
		
		//round1 - hard match: based on type and name
		if(serviceClasses.size() == 0){
			for (FixtureService fixtureService : fixtureApiServices) {
				if(fixtureService.fixtureKind.equals(fixtureKind) && fixtureService.fixtureName.equals(fixtureName)){
					FixtureExecCommandDescriptor methodAndMatcher = findMethodInClass(command, fixtureService.clazz);
					if(methodAndMatcher != null){
						serviceClasses.add(fixtureService.clazz);
					}
				}
			}
		}
		
		//round2 - soft match: based on type only
		for (FixtureService fixtureService : fixtureApiServices) {
			if(fixtureService.fixtureKind.equals(fixtureKind)){
				FixtureExecCommandDescriptor methodAndMatcher = findMethodInClass(command, fixtureService.clazz);
				if(methodAndMatcher != null){
					serviceClasses.add(fixtureService.clazz);
				}
			}
		}
		
		if (serviceClasses.size() == 0) {
			return null;
		}else if(serviceClasses.size() > 1){
			LOG.warn("Multiple Services of same kind found implementing the same command: " + command);
		}

		Class<?> serviceClass = serviceClasses.get(0);
		LOG.info(serviceClass + " : " + command);
		
		return serviceClass;
	}

	/**
	 * DOCUMENT
	 * 
	 * @param file
	 * @param serviceType
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> getServiceClassFromSettings(String file, String serviceType) throws ClassNotFoundException {
		File f = new File(file);
		try {
			String readFileToString = FileUtils.readFileToString(f);
			Gson gson = new Gson();
			ConfigProvider provider = gson.fromJson(readFileToString, ConfigProvider.class);
			if(provider != null){
				for(Settings setting: provider.settings){
					if(setting.type.equals(serviceType)){
						return Class.forName(setting.className); 
					}
				}
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * DOCUMENT
	 * 
	 * @param command
	 * @param serviceClass
	 * @return
	 */
	public FixtureExecCommandDescriptor findMethodInClass(final String command, final Class<?> serviceClass) {
		FixtureExecCommandDescriptor serviceFixtureConnector = null;
		Method[] methods = serviceClass.getMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				String methodRegex = null;
				if (annotation.annotationType().equals(Check.class)) {
					methodRegex = ((Check) annotation).value();
				}
				if (annotation.annotationType().equals(Display.class)) {
					methodRegex = ((Display) annotation).value();
				}
				if (methodRegex != null) {
					Pattern regexPattern = Pattern.compile(methodRegex);
					Matcher matcher = regexPattern.matcher(command);
					boolean matches = matcher.matches();
					if (matches) {
						serviceFixtureConnector = new FixtureExecCommandDescriptor(method, matcher);
					}
				}
			}
		}
		if(serviceFixtureConnector == null &&  serviceClass.getSuperclass() != null){
			return findMethodInClass(command, serviceClass.getSuperclass());
		}
		return serviceFixtureConnector;
	}


	/**
	 * inner beans
	 *
	 */
	class ConfigProvider{
		public List<Settings> settings;
	}
	
	class Settings{
		public String driver;
		public String type;
		public String className;
	}

	public class FixtureExecCommandDescriptor {
		Method method;
		Matcher matcher;

		public FixtureExecCommandDescriptor(Method method, Matcher matcher) {
			this.method = method;
			this.matcher = matcher;
		}
	}

}

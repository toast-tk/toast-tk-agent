package com.synpatix.toast.runtime.core.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.synaptix.toast.adapter.ActionAdapterCollector;
import com.synaptix.toast.adapter.FixtureService;
import com.synaptix.toast.adapter.web.DefaultWebPage;
import com.synaptix.toast.automation.report.HTMLReportGenerator;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionClient;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.core.runtime.IFeedableSwingPage;
import com.synaptix.toast.core.runtime.IRepositorySetup;
import com.synaptix.toast.core.runtime.ITestManager;
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
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;

 public class ToastTestRunner {

	private static final Logger LOG = LogManager.getLogger(ToastTestRunner.class);
	private static final HTMLReportGenerator htmlReportGenerator = new HTMLReportGenerator();
	private final ITestManager testManager;
	private final IRepositorySetup repoSetup;
	private URL settingsFile;
	private Injector injector;
	private IReportUpdateCallBack reportUpdateCallBack;
	private List<FixtureService> fixtureApiServices;
	
	public ToastTestRunner(ITestManager testManager, IRepositorySetup repoSetup) {
		this.testManager = testManager;
		this.repoSetup = repoSetup;
	}
	
	public ToastTestRunner(ITestManager m, Injector injector, URL settingsFile) {
		this(m, injector.getInstance(IRepositorySetup.class));
		this.injector = injector;
		this.fixtureApiServices = ActionAdapterCollector.listAvailableServicesByInjection(injector);
		this.settingsFile = settingsFile;
		if(settingsFile != null){
			LOG.info("Overriding fixture definitions with settings in " + settingsFile.getFile());
		}
	}

	public ToastTestRunner(ITestManager testEnvManager, Injector injector, URL settings, IReportUpdateCallBack reportUpdateCallBack) {
		this(testEnvManager, injector, settings);
		this.reportUpdateCallBack = reportUpdateCallBack;
	}

	/**
	 * @param result
	 * @return
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException 
	 */
	public ITestPage run(ITestPage testPage, boolean inlineReport) throws IllegalAccessException, ClassNotFoundException {

		testPage.startExecution();
		
		initTestPageVariables(testPage);
		
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
			else if (block instanceof ITestPage) {
				runTestPage((ITestPage) block, testPage, inlineReport);
			} 
		}
		testPage.stopExecution();
		if (testPage.getParsingErrorMessage() != null) {
			LOG.info(testPage.getParsingErrorMessage());
		}
		return testPage;
	}

	private void initTestPageVariables(ITestPage testPage) {
		VariableBlock varBlock = (VariableBlock) testPage.getVarBlock();
		if(varBlock != null){
			List<BlockLine> blockLines = varBlock.getBlockLines();
			for (BlockLine blockLine : blockLines) {
				//TODO: check var collision
				repoSetup.getUserVariables().put(blockLine.getCellAt(0), blockLine.getCellAt(1));
			}
		}
	}
	
	private void runSwingPageBlock(SwingPageBlock block, ITestPage testPage) {
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
	private void runWebPageBlock(WebPageBlock block, ITestPage testPage) {
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
	private void runInsertBlock(InsertBlock block, ITestPage testPage) {
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
	private void runConfigBlock(ConfigBlock block, ITestPage testPage) {
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
	private void runSetupBlock(SetupBlock block, ITestPage testPage) {
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
	private void runTestPage(ITestPage block, ITestPage testPage, boolean inlineReport) throws IllegalAccessException, ClassNotFoundException {
		this.run(block, inlineReport);
	}

	/**
	 * @param block
	 * @param testPage
	 * @param inlineReport 
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException 
	 */
	private void runTestBlock(TestBlock block, ITestPage testPage, boolean inlineReport) throws IllegalAccessException, ClassNotFoundException {
		
		for (TestLine line : block.getBlockLines()) {
			line.startExecution();
			
			//override with test line call
			TestLineDescriptor descriptor = new TestLineDescriptor(block, line);
			TestResult result = parseServiceCall(descriptor);
			
			line.stopExecution();
			if ("KO".equals(line.getExpected()) && ResultKind.FAILURE.equals(result.getResultKind())) {
				result.setResultKind(ResultKind.SUCCESS);
			}
//			else if(line.getExpected() != null && line.getExpected().startsWith("not ")){
//				String resultNotExpected = line.getExpected().substring("not ".length());
//				 if(!resultNotExpected.equals(line.getExpected())){
//					 result.setResultKind(ResultKind.SUCCESS);
//				 }else{
//					 result.setResultKind(ResultKind.ERROR);
//				 }
//			}
			else if (result.getMessage() != null && line.getExpected() != null && result.getMessage().equals(line.getExpected())) {
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
		final String studyCommand = command.replace("$$", "$");
		Class<?> localFixtureClass = locateFixtureClass(descriptor.getTestLineFixtureKind(), descriptor.getTestLineFixtureName(), studyCommand); 
		if(localFixtureClass != null){
			Object connector = getClassInstance(localFixtureClass);
			FixtureExecCommandDescriptor commandMethodImpl = findMethodInClass(command, localFixtureClass);
			if(commandMethodImpl == null){
				commandMethodImpl = findMethodInClass(studyCommand, localFixtureClass);
			}
			result = doLocalFixtureCall(command, connector, commandMethodImpl);
		}
		else if(getClassInstance(ISwingInspectionClient.class) != null){
			// If no class is implementing the command then 
			// process it as a custom command sent through Kryo 
			result = doRemoteFixtureCall(command, descriptor);
			result.setContextualTestSentence(command);
		}
		else{
			result = new TestResult(String.format("Action Implementation - Not Found"), ResultKind.ERROR);
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
		result = new TestResult("Commande Inconnue !", ResultKind.INFO);
		return result;
	}

	private TestResult doLocalFixtureCall(String command, Object instance, FixtureExecCommandDescriptor fixtureExecDescriptor) {
		TestResult result;
		
		Matcher matcher = fixtureExecDescriptor.matcher;
		matcher.matches();
		
		int groupCount = matcher.groupCount();
		Object[] args = new Object[groupCount];
		for (int i = 0; i < groupCount; i++) {
			String group = matcher.group(i + 1);
			args[i] = ToastRunnerHelper.buildArgument(repoSetup, group);
			if(group.startsWith("$$")){
				//nothing
			}
			else if (group.startsWith("$") && args[i] != null ){
				command = command.replaceFirst("\\"+group+"\\b", (String) args[i]);
			}
			else{
				//nothing
			}
		}

		try {
			result = (TestResult) fixtureExecDescriptor.method.invoke(instance, args);
			result.setContextualTestSentence(command);
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
			commandRequest = new CommandRequest.CommandRequestBuilder(null).ofType(ActionAdapterKind.service.name()).asCustomCommand(command).build();
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
	private Class<?> locateFixtureClass(ActionAdapterKind fixtureKind, String fixtureName, String command) throws ClassNotFoundException, IllegalAccessException {
		Set<Class<?>> serviceClasses = new HashSet<Class<?>>();
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
			LOG.error("No Connector found for command: " + command);
			return null;
		}else if(serviceClasses.size() > 1){
			LOG.warn("Multiple Services of same kind found implementing the same command: " + command);
		}

		Class<?> serviceClass = serviceClasses.iterator().next();
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
				if (annotation.annotationType().equals(Action.class)) {
					methodRegex = ((Action) annotation).action();
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

	public static void main(String[] args) {
		String o = "$$dab $$in $df";
		
		String v = o.replace("$$", "$");
		System.out.println(v);
		
	} 
	
}

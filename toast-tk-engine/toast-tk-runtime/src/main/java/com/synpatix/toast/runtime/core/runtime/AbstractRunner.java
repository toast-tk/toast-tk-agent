package com.synpatix.toast.runtime.core.runtime;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.synaptix.toast.automation.report.IHTMLReportGenerator;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synpatix.toast.runtime.core.parse.TestParser;

public abstract class AbstractRunner {

	private static final Logger LOG = LogManager.getLogger(AbstractRunner.class);
	private ITestManager testEnvManager;
	private Injector injector;
	private boolean presetRepoFromWebApp = false;
	private IReportUpdateCallBack reportUpdateCallBack;
	private TestPage localRepositoryTestPage;
	private IHTMLReportGenerator htmlReportGenerator;

	protected AbstractRunner(Injector injector) {
		try {
			this.testEnvManager = injector.getInstance(ITestManager.class);
		} catch (ConfigurationException e) {
			LOG.error("No Test Environement Manager defined !", e);
		}
		this.htmlReportGenerator = injector.getInstance(IHTMLReportGenerator.class);
		this.injector = injector;
	}

	public final void run(String... scenarios) {
		this.presetRepoFromWebApp = false;
		run(testEnvManager, scenarios);
	}

	public final void runRemote(String... scenarios) {
		this.presetRepoFromWebApp = true;
		run(testEnvManager, scenarios);
	}
	
	public final void runRemoteScript(String script) {
		this.presetRepoFromWebApp = true;
		runScript(testEnvManager, null, script);
	}
	
	public void runLocalScript(String wikiScenario, String repoWiki, IReportUpdateCallBack iReportUpdateCallBack) {
		this.reportUpdateCallBack = iReportUpdateCallBack;
		TestParser parser = new TestParser();
		localRepositoryTestPage = parser.readString(repoWiki, "");
		runScript(testEnvManager, null, wikiScenario);
	}

	public final void run(ITestManager testEnvManager, String... scenarios) {
		List<ITestPage> testPages = new ArrayList<ITestPage>();
		initEnvironment();
		for (String fileName : scenarios) {
			System.out.println("Start main test parser: " + fileName);

			// Read test file
			File file = readTestFile(fileName);
			ITestPage result = runScript(testEnvManager, file, fileName);

			testPages.add(result);
		}
		tearDownEnvironment();
		LOG.info(scenarios.length + "file(s) processed");
		RunUtils.printResult(testPages);
	}

	private File readTestFile(String fileName) {
		try {
			return new File(this.getClass().getClassLoader().getResource(fileName).getFile());
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private ITestPage runScript(ITestManager testEnvManager, File file, String script) {
		TestParser testParser = new TestParser();
		ITestPage result = file == null ? testParser.parseString(script) : testParser.parse(file);
		
		// Run test
		URL defaultSettings = this.getClass().getClassLoader().getResource(Property.REDPEPPER_AUTOMATION_SETTINGS_DEFAULT_DIR);
		ToastTestRunner runner = new ToastTestRunner(testEnvManager, injector, defaultSettings, reportUpdateCallBack);

		try {
			if (presetRepoFromWebApp) {
				String repoWiki = RestUtils.downloadRepositoyAsWiki();
				TestParser parser = new TestParser();
				TestPage repoAsTestPageForConveniency = parser.readString(repoWiki, "");
				runner.run(repoAsTestPageForConveniency, false);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Preset repository from webapp rest api...");
				}
			}
			else if(localRepositoryTestPage != null){
				runner.run(localRepositoryTestPage, false);
			}
			beginTest();
			result = runner.run(result, true);
			createAndOpenReport(result);
			endTest();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	

	private void createAndOpenReport(ITestPage testPage) {
		String generatePageHtml = htmlReportGenerator.generatePageHtml(testPage);
		URL resource = this.getClass().getClassLoader() != null ? this.getClass().getClassLoader().getResource("TestResult") : null;
		if(resource != null){
			try {
				if(!Boolean.getBoolean("java.awt.headless")){
					final String pageName = testPage.getPageName();
					htmlReportGenerator.writeFile(generatePageHtml, pageName, resource.getPath());
					File htmlFile = new File(resource.getPath() + "\\" + pageName + ".html");
					Desktop.getDesktop().browse(htmlFile.toURI());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract void tearDownEnvironment();

	public abstract void beginTest();
	
	public abstract void endTest();
	
	public abstract void initEnvironment();
}

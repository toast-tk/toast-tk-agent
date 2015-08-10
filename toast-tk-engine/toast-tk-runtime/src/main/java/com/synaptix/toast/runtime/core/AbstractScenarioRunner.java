package com.synaptix.toast.runtime.core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Injector;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;
import com.synaptix.toast.runtime.report.test.IHTMLReportGenerator;
import com.synaptix.toast.runtime.utils.RunUtils;

public abstract class AbstractRunner {

	private static final Logger LOG = LogManager.getLogger(AbstractRunner.class);


	private Injector injector;

	private boolean presetRepoFromWebApp = false;

	private IReportUpdateCallBack reportUpdateCallBack;

	private TestPage localRepositoryTestPage;

	private IHTMLReportGenerator htmlReportGenerator;

	protected AbstractRunner(
		Injector injector) {
		this.htmlReportGenerator = injector.getInstance(IHTMLReportGenerator.class);
		this.injector = injector;
	}

	public final void run(
		String... scenarios)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		this.presetRepoFromWebApp = false;
		runScenario(scenarios);
	}

	public final void runScenario(
		String... scenarios)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		List<ITestPage> testPages = new ArrayList<ITestPage>();
		initEnvironment();
		for(String fileName : scenarios) {
			System.out.println("Start main test parser: " + fileName);
			// Read test file
			File file = readTestFile(fileName);
			ITestPage result = runScript(file, fileName);
			testPages.add(result);
		}
		tearDownEnvironment();
		LOG.info(scenarios.length + "file(s) processed");
		RunUtils.printResult(testPages);
	}

	public final void runRemote(
		String... scenarios)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		this.presetRepoFromWebApp = true;
		run(scenarios);
	}

	public final void runRemoteScript(
		String script)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		this.presetRepoFromWebApp = true;
		runScript(null, script);
	}

	public void runLocalScript(
		String wikiScenario,
		String repoWiki,
		IReportUpdateCallBack iReportUpdateCallBack)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		this.reportUpdateCallBack = iReportUpdateCallBack;
		TestParser parser = new TestParser();
		this.localRepositoryTestPage = parser.readString(repoWiki, "");
		runScript(null, wikiScenario);
	}

	private File readTestFile(
		String fileName) {
		try {
			return new File(this.getClass().getClassLoader().getResource(fileName).getFile());
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private ITestPage runScript(
		File file,
		String script)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		TestParser testParser = new TestParser();
		ITestPage result = file == null ? testParser.parseString(script) : testParser.parse(file);
		TestRunner runner = TestRunner.FromInjectorWithReportCallBack(injector, reportUpdateCallBack);
		if(this.presetRepoFromWebApp) {
			String repoWiki = RestUtils.downloadRepositoyAsWiki();
			TestParser parser = new TestParser();
			TestPage repoAsTestPageForConveniency = parser.readString(repoWiki, "");
			runner.run(repoAsTestPageForConveniency, false);
		}
		else if(this.localRepositoryTestPage != null) {
			runner.run(this.localRepositoryTestPage, false);
		}
		beginTest();
		result = runner.run(result, true);
		createAndOpenReport(result);
		endTest();
		return result;
	}

	private void createAndOpenReport(
		ITestPage testPage) {
		String generatePageHtml = htmlReportGenerator.generatePageHtml(testPage);
		URL resource = this.getClass().getClassLoader() != null ? this.getClass().getClassLoader()
			.getResource("TestResult") : null;
		if(resource != null) {
			try {
				if(!Boolean.getBoolean("java.awt.headless")) {
					final String pageName = testPage.getPageName();
					this.htmlReportGenerator.writeFile(generatePageHtml, pageName, resource.getPath());
					File htmlFile = new File(resource.getPath() + "\\" + pageName + ".html");
					Desktop.getDesktop().browse(htmlFile.toURI());
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void tearDownEnvironment();

	public abstract void beginTest();

	public abstract void endTest();

	public abstract void initEnvironment();
}

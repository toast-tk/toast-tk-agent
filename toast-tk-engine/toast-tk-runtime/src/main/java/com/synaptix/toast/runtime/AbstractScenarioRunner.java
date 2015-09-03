package com.synaptix.toast.runtime;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.synaptix.toast.core.annotation.EngineEventBus;
import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.TestParser;
import com.synaptix.toast.runtime.report.DefaultTestProgressReporter;
import com.synaptix.toast.runtime.report.IHTMLReportGenerator;
import com.synaptix.toast.runtime.utils.RunUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@FixMe(todo="make the runner generic")
public abstract class AbstractScenarioRunner extends AbstractRunner{

	private static final Logger LOG = LogManager.getLogger(AbstractScenarioRunner.class);

	private Injector injector;

	private boolean presetRepoFromWebApp = false;

	private TestPage localRepositoryTestPage;

	private IHTMLReportGenerator htmlReportGenerator;

	private EventBus eventBus;

	private DefaultTestProgressReporter progressReporter;

	protected AbstractScenarioRunner(
		Injector injector) {
		this.htmlReportGenerator = injector.getInstance(IHTMLReportGenerator.class);
		this.injector = injector;
		this.eventBus = injector.getInstance(Key.get(EventBus.class, EngineEventBus.class));
		this.progressReporter = new DefaultTestProgressReporter(eventBus, htmlReportGenerator);
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
			LOG.info("Start main test parser: " + fileName);
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
		IReportUpdateCallBack callback)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		this.progressReporter.setReportCallBack(callback);
		TestParser parser = new TestParser();
		this.localRepositoryTestPage = parser.readString(repoWiki, "");
		runScript(null, wikiScenario);
	}

	private File readTestFile(
		String fileName) throws IOException {
		return new File(this.getClass().getClassLoader().getResource(fileName).getFile());
	}

	private ITestPage runScript(
		File file,
		String script)
		throws IllegalAccessException, ClassNotFoundException, IOException {
		TestParser testParser = new TestParser();
		ITestPage result = file == null ? testParser.buildFromString(script) : testParser.parse(file.getPath());
		TestRunner runner = new TestRunner(injector);
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
					final String pageName = testPage.getPath();
					this.htmlReportGenerator.writeFile(generatePageHtml, pageName, resource.getPath());
					File htmlFile = new File(resource.getPath() + "\\" + pageName + ".html");
					Desktop.getDesktop().browse(htmlFile.toURI());
				}
			}
			catch(IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}

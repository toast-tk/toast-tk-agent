package com.synpatix.toast.runtime.core.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.synaptix.toast.core.ITestManager;
import com.synaptix.toast.core.Property;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synpatix.toast.runtime.core.parse.TestParser;

public abstract class AbstractRunner {

	private static final Logger LOG = LogManager.getLogger(AbstractRunner.class);
	private ITestManager testEnvManager;
	private Injector injector;
	private boolean presetRepoFromWebApp = false;

	protected AbstractRunner(Injector injector) {
		try {
			this.testEnvManager = injector.getInstance(ITestManager.class);
		} catch (ConfigurationException e) {
			LOG.error("No Test Environement Manager defined !", e);
		}
		this.injector = injector;
	}

	public final void run(String... scenarios) {
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

	public final void run(ITestManager testEnvManager, String... scenarios) {
		List<TestPage> testPages = new ArrayList<TestPage>();

		for (String fileName : scenarios) {
			System.out.println("Start main test parser: " + fileName);

			// Read test file
			File file = readTestFile(fileName);
			TestPage result = runScript(testEnvManager, file, fileName);

			testPages.add(result);
		}

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

	private TestPage runScript(ITestManager testEnvManager, File file, String script) {
		TestParser testParser = new TestParser();
		TestPage result = file == null ? testParser.parseString(script) : testParser.parse(file);
		// Run test
		ToastTestRunner runner = new ToastTestRunner(testEnvManager, injector, this.getClass().getClassLoader()
				.getResource(Property.REDPEPPER_AUTOMATION_SETTINGS_DEFAULT_DIR));

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

			result = runner.run(result, true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}

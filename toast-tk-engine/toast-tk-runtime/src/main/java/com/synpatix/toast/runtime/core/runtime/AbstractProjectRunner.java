package com.synpatix.toast.runtime.core.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.synaptix.toast.core.dao.ICampaign;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synpatix.toast.runtime.core.parse.TestParser;
import com.synpatix.toast.runtime.dao.DAOManager;

public abstract class AbstractProjectRunner {
	
	private static final Logger LOG = LogManager.getLogger(AbstractProjectRunner.class);
	private ITestManager testEnvManager;
	private Injector injector;

	protected AbstractProjectRunner(Injector injector) throws Exception{
		try{
			this.testEnvManager = injector.getInstance(ITestManager.class);
		}catch(ConfigurationException e){
			System.out.println("No Test Environement Manager defined !");
		}
		this.injector = injector;
	}

	public final void test(String projectName, boolean overrideRepoFromWebApp) throws Exception {
		Project project = DAOManager.getInstance().getLastProjectByName(projectName);
		execute(project, overrideRepoFromWebApp);
		DAOManager.getInstance().saveProject(project);
	}

	private void execute(Project project, boolean presetRepoFromWebApp) throws Exception {
		TestRunner runner = new TestRunner(testEnvManager, injector, this.getClass().getClassLoader().getResource("settings/redpepper_descriptor.json"));
		if(presetRepoFromWebApp){
			String repoWiki = RestUtils.downloadRepositoyAsWiki();
			TestParser parser = new TestParser();
			TestPage repoAsTestPageForConveniency = parser.readString(repoWiki, "");
			runner.run(repoAsTestPageForConveniency, false);
			if(LOG.isDebugEnabled()){
				LOG.debug("Preset repository from webapp rest api...");
			}
		}
		execute(project, runner);
	}

	private void execute(Project project, TestRunner runner) throws ClassNotFoundException {
		initEnvironment();
		
		for (ICampaign campaign : project.getCampaigns()) {
			for (ITestPage testPage : campaign.getTestCases()) {
				try {
					beginTest();
					testPage = runner.run(testPage, true);
					endTest();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		tearDownEnvironment();
	}

	public abstract void tearDownEnvironment();

	public abstract void beginTest();
	
	public abstract void endTest();
	
	public abstract void initEnvironment();
	
}

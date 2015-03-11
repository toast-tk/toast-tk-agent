package com.synpatix.toast.runtime.core.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.core.ITestManager;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.domain.impl.report.Campaign;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;
import com.synpatix.toast.runtime.core.parse.TestParser;

public abstract class AbstractProjectRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractProjectRunner.class);
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
	
	Injector mongoServiceInjector = Guice.createInjector(new MongoModule());
	ProjectDaoService.Factory pfactory = mongoServiceInjector.getInstance(ProjectDaoService.Factory.class);
	ProjectDaoService projectService = pfactory.create("test_project_db");

	public final void test(String projectName, boolean overrideRepoFromWebApp) throws Exception {
		Project project = projectService.getLastByName(projectName);
		execute(project, overrideRepoFromWebApp);
		projectService.saveNewIteration(project);
	}

	private void execute(Project project, boolean presetRepoFromWebApp) throws Exception {
		ToastTestRunner runner = new ToastTestRunner(testEnvManager, injector, this.getClass().getClassLoader().getResource("settings/redpepper_descriptor.json"));
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

	private void execute(Project project, ToastTestRunner runner) throws ClassNotFoundException {
		initEnvironment();
		
		for (Campaign campaign : project.getCampaigns()) {
			for (TestPage testPage : campaign.getTestCases()) {
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

package io.toast.tk.agent.run;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.google.inject.Module;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.NotificationManager;
import io.toast.tk.dao.domain.impl.test.block.ITestPage;
import io.toast.tk.dao.domain.impl.test.block.ITestPlan;
import io.toast.tk.plugin.PluginLoader;
import io.toast.tk.runtime.parse.FileHelper;
import io.toast.tk.runtime.parse.TestParser;

public class TestRunner {

	private static final Logger LOG = LogManager.getLogger(TestRunner.class);
	private TestPageRunner testPageRunner;
	private TestPlanRunner testPlanRunner;
	private AgentConfigProvider provider;
	
	public TestRunner(AgentConfigProvider provider){
		this.provider = provider;
	}
	
	public void execute(){
		String scriptsPath = this.provider.get().getScriptsDir();
		Path path = Paths.get(scriptsPath);
		if(!Strings.isEmpty(scriptsPath) || !Files.isDirectory(path)){
			NotificationManager.showMessage("No script directory defined, check agent settings !");
		}
		final List<ITestPage> testScripts = new ArrayList<>();
		TestParser parser = new TestParser();
		try {
			Files.list(path).forEach(p -> {
				try{
					List<String> scriptLines = FileHelper.getScript(new FileInputStream(p.toFile()));
					ITestPage testScript = parser.parse(scriptLines, p.getFileName().toString());
					testScripts.add(testScript);
				}catch(Exception e){
					LOG.error(e.getMessage(), e);
					NotificationManager.showMessage("Unable to parse " + p.getFileName() + " !");
				}
			});
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			NotificationManager.showMessage("Unable to list files in scripts path " + path.getFileName() + " !");
		}
		
		testScripts.forEach(script ->{
			try {
				run(script);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				NotificationManager.showMessage("Failed to execute " + script.getName() + " !");
			}
		});
	}
	
	public static void main(String[] args){
		TestRunner runner = new TestRunner(new AgentConfigProvider());
		runner.execute();
	}
	
	public ITestPage run(ITestPage testPage) throws IOException {
		PluginLoader loader = new PluginLoader(provider);
		Module[] pluginModules = loader.collectGuiceModules(loader.loadPlugins());
		this.testPageRunner =  new TestPageRunner(pluginModules);
		return testPageRunner.runTestPage(testPage);
	}

	public void run(ITestPlan testPlan) throws Exception {
		PluginLoader loader = new PluginLoader(provider);
		Module[] pluginModules = loader.collectGuiceModules(loader.loadPlugins());
		this.testPlanRunner =  new TestPlanRunner(pluginModules);
		testPlanRunner.execute(testPlan, true);
	}
	
}

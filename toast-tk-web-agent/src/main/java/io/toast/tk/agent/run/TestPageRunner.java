package io.toast.tk.agent.run;

import com.google.inject.Module;

import io.toast.tk.runtime.AbstractScenarioRunner;

public class TestPageRunner extends AbstractScenarioRunner{

	public TestPageRunner(Module[] pluginModules) {
		super(pluginModules);
	}

	@Override
	public void tearDownEnvironment() {
		//NO-OP
	}

	@Override
	public void beginTest() {
		//NO-OP
	}

	@Override
	public void endTest() {
		//NO-OP
	}

	@Override
	public void initEnvironment() {
		//NO-OP
	}
	
	@Override
	public String getReportsOutputPath(){
		return null;
	}



}

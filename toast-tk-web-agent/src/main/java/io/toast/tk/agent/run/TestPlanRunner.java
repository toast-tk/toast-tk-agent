package io.toast.tk.agent.run;

import com.google.inject.Module;

import io.toast.tk.runtime.AbstractTestPlanRunner;

public class TestPlanRunner extends AbstractTestPlanRunner{

	public TestPlanRunner(Module[] pluginModules) {
		super(pluginModules);
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
	public void tearDownEnvironment() {
		//NO-OP	
	}

}

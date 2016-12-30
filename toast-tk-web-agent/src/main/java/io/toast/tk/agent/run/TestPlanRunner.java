package io.toast.tk.agent.run;

import com.google.inject.Module;

import io.toast.tk.runtime.AbstractProjectRunner;

public class TestPlanRunner extends AbstractProjectRunner{

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

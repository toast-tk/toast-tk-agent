package com.synaptix.toast.runtime;

import java.util.List;

import com.synaptix.toast.automation.report.IReporter;

/**
 * Main test case runner
 * 
 * @author skokaina
 * 
 */
public class JavaTestRunner {

	private IReporter reporter;

	/**
	 * 
	 * @param reporter
	 */
	public JavaTestRunner(
		IReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * Default constructor
	 */
	public JavaTestRunner() {
	}

	/**
	 * launch a single test opens and close the browser
	 * 
	 * @param script
	 */
	public void launch(
		Class<?> script) {
		try {
			TestScriptBase<?, ?> executable = (TestScriptBase<?, ?>) script.newInstance();
			executable.setReporter(reporter);
			executable.init();
			executable.run();
			executable.end();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param scripts
	 * @param chained
	 *            chain the tests without closing the browser between each test case
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void launch(
		List<Class<?>> scripts,
		boolean chained)
		throws InstantiationException, IllegalAccessException {
		TestScriptBase<?, ?> lastExecutable = null;
		for(Class<?> script : scripts) {
			TestScriptBase<?, ?> executable = (TestScriptBase<?, ?>) script.newInstance();
			executable.setReporter(reporter);
			executable.init();
			executable.run();
			if(!chained) {
				executable.end();
			}
			else {
				lastExecutable = executable;
			}
		}
		if(chained && lastExecutable != null) {
			lastExecutable.end();
		}
	}
}

package com.synpatix.toast.runtime;

import java.util.List;

import com.synaptix.toast.automation.report.IReporter;

/**
 * Main test case runner
 * 
 * @author skokaina
 * 
 */
public class ToastJavaRunner {

	private IReporter reporter;

	/**
	 * 
	 * @param reporter
	 */
	public ToastJavaRunner(IReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * Default constructor
	 */
	public ToastJavaRunner() {

	}

	/**
	 * launch a single test opens and close the browser
	 * 
	 * @param script
	 */
	public void launch(Class<?> script) {
		try {
			TestScriptBase<?, ?> executable = (TestScriptBase<?, ?>) script.newInstance();
			executable.setReporter(reporter);
			executable.init();
			executable.run();
			executable.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param scripts
	 * @param chained
	 *            chain the tests without closing the browser between each test case
	 */
	public void launch(List<Class<?>> scripts, boolean chained) {
		TestScriptBase<?, ?> lastExecutable = null;
		for (Class<?> script : scripts) {
			try {
				TestScriptBase<?, ?> executable = (TestScriptBase<?, ?>) script.newInstance();
				executable.setReporter(reporter);
				executable.init();
				executable.run();
				if (!chained) {
					executable.end();
				} else {
					lastExecutable = executable;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (chained && lastExecutable != null) {
			lastExecutable.end();
		}
	}

}

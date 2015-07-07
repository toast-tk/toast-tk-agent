package com.synaptix.toast.runtime;

import com.synaptix.toast.automation.report.ConsoleReporter;

public class ConsoleRunner extends JavaTestRunner {

	public static void main(
		String[] args) {
		JavaTestRunner runner = new JavaTestRunner(new ConsoleReporter());
		try {
			runner.launch(Class.forName(args[0]));
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

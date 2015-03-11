package com.synaptix.toast.automation.runner;

import com.synaptix.toast.automation.report.ConsoleReporter;

public class ConsoleRedPepperRunner extends RedPepperRunner {

	public static void main(String[] args) {
		RedPepperRunner runner = new RedPepperRunner(new ConsoleReporter());
		try {
			runner.launch(Class.forName(args[0]));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

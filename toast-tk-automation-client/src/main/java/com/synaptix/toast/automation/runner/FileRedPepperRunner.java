package com.synaptix.toast.automation.runner;

import com.synaptix.toast.automation.report.FileReporter;

public class FileRedPepperRunner extends RedPepperRunner {

	public static void main(String[] args) {
		// org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
		FileReporter reporter = new FileReporter("test.out.txt");
		RedPepperRunner runner = new RedPepperRunner(reporter);
		try {
			runner.launch(Class.forName(args[0]));
			reporter.getW().close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

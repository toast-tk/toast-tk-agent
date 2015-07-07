package com.synaptix.toast.runtime;

import com.synaptix.toast.automation.report.FileReporter;

public class FileRunner extends JavaTestRunner {

	public static void main(
		String[] args) {
		// org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
		FileReporter reporter = new FileReporter("test.out.txt");
		JavaTestRunner runner = new JavaTestRunner(reporter);
		try {
			runner.launch(Class.forName(args[0]));
			reporter.getW().close();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

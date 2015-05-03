package com.synpatix.toast.runtime;

import com.synaptix.toast.automation.report.FileReporter;

public class FileRunner extends ToastJavaRunner {

	public static void main(String[] args) {
		// org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
		FileReporter reporter = new FileReporter("test.out.txt");
		ToastJavaRunner runner = new ToastJavaRunner(reporter);
		try {
			runner.launch(Class.forName(args[0]));
			reporter.getW().close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

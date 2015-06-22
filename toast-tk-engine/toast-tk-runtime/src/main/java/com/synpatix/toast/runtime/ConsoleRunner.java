package com.synpatix.toast.runtime;

import com.synaptix.toast.automation.report.ConsoleReporter;

public class ConsoleRunner extends ToastJavaRunner {

	public static void main(String[] args) {
		ToastJavaRunner runner = new ToastJavaRunner(new ConsoleReporter());
		try {
			runner.launch(Class.forName(args[0]));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

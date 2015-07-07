package com.synaptix.toast.automation.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * console reporter, print results and actions to standard output
 * 
 * @author skokaina
 * 
 */
public class FileReporter implements IReporter {

	PrintWriter w;

	public PrintWriter getW() {
		return w;
	}

	public void setW(
		PrintWriter w) {
		this.w = w;
	}

	public FileReporter(
		String file) {
		File f = new File(file);
		try {
			w = new PrintWriter(f);
		}
		catch(FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reportResult(
		WebTestResult<?> result) {
		PrintStream stream = System.out;
		if(!result.isSuccess()) {
			stream = System.err;
		}
		w.write("[+] Result: " + result.getTitle() + " -> Expected: " + result.getExpected() + ", Current:"
			+ result.getCurrent());
	}

	@Override
	public void reportAction(
		WebActionResult result) {
		w.write("[+] Action: " + result.getTitle() + " -> " + result.getAction());
	}

	@Override
	public void reportSimple(
		String toReport) {
		w.write(toReport);
	}
}

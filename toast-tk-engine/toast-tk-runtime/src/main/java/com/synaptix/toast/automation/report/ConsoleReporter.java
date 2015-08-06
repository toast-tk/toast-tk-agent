package com.synaptix.toast.automation.report;

import java.io.PrintStream;

import com.synaptix.toast.runtime.WebActionResult;
import com.synaptix.toast.runtime.WebTestResult;

/**
 * console reporter, print results and actions to standard output
 * 
 * @author skokaina
 * 
 */
public class ConsoleReporter implements IReporter {

	public ConsoleReporter() {
	}

	@Override
	public void reportResult(
		WebTestResult<?> result) {
		PrintStream stream = System.out;
		if(!result.isSuccess()) {
			stream = System.err;
		}
		stream.println("[+] Result: " + result.getTitle() + " -> Expected: " + result.getExpected() + ", Current:"
			+ result.getCurrent());
	}

	@Override
	public void reportAction(
		WebActionResult result) {
		System.out.println("[+] Action: " + result.getTitle() + " -> " + result.getAction());
	}

	@Override
	public void reportSimple(
		String toReport) {
		System.out.println(toReport);
	}
}

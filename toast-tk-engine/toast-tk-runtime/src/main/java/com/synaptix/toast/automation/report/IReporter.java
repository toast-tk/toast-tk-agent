package com.synaptix.toast.automation.report;

import com.synaptix.toast.runtime.WebActionResult;
import com.synaptix.toast.runtime.WebTestResult;

/**
 * 
 * @author skokaina
 * 
 */
public interface IReporter {

	public void reportResult(
		WebTestResult<?> result);

	public void reportAction(
		WebActionResult result);

	public void reportSimple(
		String toReport);
}

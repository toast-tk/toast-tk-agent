package com.synaptix.toast.automation.report;

/**
 * 
 * @author skokaina
 * 
 */
public interface IReporter {

	public void reportResult(WebTestResult<?> result);

	public void reportAction(WebActionResult result);

	public void reportSimple(String toReport);

}

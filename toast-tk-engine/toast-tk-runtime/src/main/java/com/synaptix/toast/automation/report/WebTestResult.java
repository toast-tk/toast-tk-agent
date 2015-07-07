package com.synaptix.toast.automation.report;

/**
 * Web result from a test case execution
 * 
 * @author skokaina
 * 
 * @param <E>
 */
public class WebTestResult<E> implements ITestResult {

	private boolean success;

	private String title;

	private E expected;

	private E current;

	public WebTestResult(
		boolean success,
		String title,
		E expected,
		E current) {
		this.success = success;
		this.title = title;
		this.expected = expected;
		this.current = current;
	}

	public WebTestResult() {
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(
		boolean success) {
		this.success = success;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(
		String title) {
		this.title = title;
	}

	public E getExpected() {
		return expected;
	}

	public void setExpected(
		E expected) {
		this.expected = expected;
	}

	public E getCurrent() {
		return current;
	}

	public void setCurrent(
		E current) {
		this.current = current;
	}
}

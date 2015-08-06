package com.synaptix.toast.runtime;

/**
 * web action result from a test case execution
 */
public class WebActionResult {

	private String title;

	private String action;

	public WebActionResult(
		String title,
		String action) {
		this.title = title;
		this.action = action;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(
		String title) {
		this.title = title;
	}

	public String getAction() {
		return action;
	}

	public void setAction(
		String action) {
		this.action = action;
	}
}

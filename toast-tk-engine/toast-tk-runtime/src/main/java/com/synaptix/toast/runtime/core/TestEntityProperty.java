package com.synaptix.toast.runtime.core;

public class TestEntityProperty {
	private final String testName;

	final String appName;

	public final String entityType;

	public TestEntityProperty(
		String testName,
		String appName,
		String entityType) {
		this.testName = testName;
		this.appName = appName;
		this.entityType = entityType;
	}
}

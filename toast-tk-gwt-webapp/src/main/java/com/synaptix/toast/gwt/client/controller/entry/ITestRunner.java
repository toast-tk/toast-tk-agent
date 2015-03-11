package com.synaptix.toast.gwt.client.controller.entry;

import com.synaptix.toast.gwt.client.ITestCase;

public interface ITestRunner {

	public void onPlay(String testName, ITestCase tCase);

	public void onPlayFixture(String value);
}

package com.synaptix.toast.gwt.client.controller.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.synaptix.toast.gwt.client.ITestCase;
import com.synaptix.toast.gwt.client.controller.entry.ITestContainer;
import com.synaptix.toast.gwt.client.controller.entry.ITestController;
import com.synaptix.toast.gwt.client.service.GreetingService;
import com.synaptix.toast.gwt.client.service.GreetingServiceAsync;
import com.synaptix.toast.gwt.client.view.impl.TestCaseViewImpl;

public class TestController implements ITestController {

	private final GreetingServiceAsync service = GWT.create(GreetingService.class);

	@Override
	public void onPlay(String testName, final ITestCase tCase) {
		String selectedJar = AdminController.getInstance().getSelectedJar();
		if (selectedJar == null) {
			Window.alert("Please select a jar from the admin tab first !");
		} else {
			service.play(selectedJar, testName, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					tCase.setSuccess(true);
					tCase.setResult(result);
					tCase.setLoadVisible(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					tCase.setLoadVisible(false);
					caught.printStackTrace();
				}
			});
		}
	}

	@Override
	public void onPlayFixture(String value) {
		// dialogBox.setResponse(value);
	}

	@Override
	public void loadAllScripts(final ITestContainer container) {
		String selectedJar = AdminController.getInstance().getSelectedJar();
		if (selectedJar == null) {
			Window.alert("Please select a jar from the admin tab first !");
		} else {
			service.loadTestScripts(selectedJar, new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					String res = "";
					for (String test : result) {
						res += test + "<br>";
						container.addTest(new TestCaseViewImpl(test, TestController.this));
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}
}

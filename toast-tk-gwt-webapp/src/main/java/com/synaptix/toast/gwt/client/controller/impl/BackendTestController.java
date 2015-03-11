package com.synaptix.toast.gwt.client.controller.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.synaptix.toast.gwt.client.controller.entry.IBackendTestController;
import com.synaptix.toast.gwt.client.service.GreetingService;
import com.synaptix.toast.gwt.client.service.GreetingServiceAsync;
import com.synaptix.toast.gwt.client.view.entry.IBackendTestView;

public class BackendTestController implements IBackendTestController {

	private final GreetingServiceAsync service = GWT.create(GreetingService.class);

	@Override
	public void loadTests() {
		service.loadBackendTests(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				// backend tests to load
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void runTest(final IBackendTestView v, String backendTest) {
		v.startLoading();
		service.runBackendTest(backendTest, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				v.setResult(result);
				v.endLoading();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				v.endLoading();
			}
		});

	}
}

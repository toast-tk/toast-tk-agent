package com.synaptix.toast.gwt.client.controller.entry;

import com.synaptix.toast.gwt.client.view.entry.IBackendTestView;

public interface IBackendTestController {

	void loadTests();

	void runTest(IBackendTestView backendView, String backendTest);

}

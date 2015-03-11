package com.synaptix.toast.gwt.client.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.controller.entry.ITestContainer;
import com.synaptix.toast.gwt.client.controller.entry.ITestController;

public class TestTabViewImpl extends Composite implements ITestContainer {

	private static TestTabViewImplUiBinder uiBinder = GWT.create(TestTabViewImplUiBinder.class);

	interface TestTabViewImplUiBinder extends UiBinder<Widget, TestTabViewImpl> {
	}

	@UiField
	HTMLPanel testListPanel;
	private final ITestController controller;

	public TestTabViewImpl(ITestController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		this.controller = controller;
	}

	@UiHandler("refreshButton")
	public void onClick(ClickEvent e) {
		controller.loadAllScripts(TestTabViewImpl.this);
	}

	@Override
	public void addTest(TestCaseViewImpl testCaseView) {
		testListPanel.add(testCaseView);
	}

}

package com.synaptix.toast.gwt.client.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.ITestCase;
import com.synaptix.toast.gwt.client.controller.entry.ITestRunner;
import com.synaptix.toast.gwt.client.view.widget.InfoDialogBox;

public class TestCaseViewImpl extends Composite implements ITestCase {

	private static TestCaseUiBinder uiBinder = GWT.create(TestCaseUiBinder.class);

	interface TestCaseUiBinder extends UiBinder<Widget, TestCaseViewImpl> {
	}

	@UiField
	Button playButton;

	@UiField
	Button editButton;

	@UiField
	Image loadingImg;

	@UiField
	Image successImg;

	@UiField
	Image failureImg;

	@UiField
	HorizontalPanel testSlot;

	private final String testName;

	private final ITestRunner runner;

	private String result;

	public TestCaseViewImpl(final String testName, ITestRunner runner) {
		initWidget(uiBinder.createAndBindUi(this));
		this.testName = testName;
		testSlot.add(new HTML("<div class=\"tName\">" + testName + "</div>"));
		loadingImg.getElement().getStyle().setDisplay(Display.NONE);
		failureImg.getElement().getStyle().setDisplay(Display.NONE);
		successImg.getElement().getStyle().setDisplay(Display.NONE);

		successImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				InfoDialogBox infoDialogBox = new InfoDialogBox(result, testName);
				infoDialogBox.setHeight("400px");
				infoDialogBox.setAutoHideEnabled(true);
				infoDialogBox.show();
			}
		});
		this.runner = runner;
	}

	@UiHandler("playButton")
	void onClick(ClickEvent e) {
		loadingImg.getElement().getStyle().clearDisplay();
		runner.onPlay(testName, this);
	}

	@Override
	public void setLoadVisible(boolean visible) {
		if (visible) {
			loadingImg.getElement().getStyle().clearDisplay();
		} else {
			loadingImg.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	@Override
	public void setSuccess(boolean success) {
		if (success) {
			successImg.getElement().getStyle().clearDisplay();
			failureImg.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			failureImg.getElement().getStyle().clearDisplay();
			successImg.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	@Override
	public void setShowRunStatus(boolean show) {
		if (show) {
			loadingImg.getElement().getStyle().clearDisplay();
		} else {
			loadingImg.getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	@Override
	public void setResult(String result) {
		this.result = result;
		successImg.setAltText(result);
	}

}

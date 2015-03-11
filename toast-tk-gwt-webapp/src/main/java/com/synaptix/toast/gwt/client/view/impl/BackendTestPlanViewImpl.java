package com.synaptix.toast.gwt.client.view.impl;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.controller.entry.IBackendTestController;
import com.synaptix.toast.gwt.client.view.entry.IBackendTestView;

public class BackendTestPlanViewImpl extends Composite implements IBackendTestView {

	private static BackendTestPlanUiBinder uiBinder = GWT.create(BackendTestPlanUiBinder.class);

	interface BackendTestPlanUiBinder extends UiBinder<Widget, BackendTestPlanViewImpl> {
	}

	@UiField
	Button refreshButton;

	@UiField
	Button playButton;

	@UiField
	Button editButton;

	@UiField
	HTMLPanel testListPanel;

	@UiField
	TextAreaElement tAreaField;

	@UiField
	HTMLPanel resultPanel;

	@UiField
	ImageElement loader;

	private final IBackendTestController controller;

	public BackendTestPlanViewImpl(final IBackendTestController controller) {
		this.controller = controller;
		initWidget(uiBinder.createAndBindUi(this));
		loader.getStyle().setDisplay(Display.NONE);
		playButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.runTest(BackendTestPlanViewImpl.this, getScript());
			}
		});

		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.loadTests();
			}
		});

		editButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("Edit functionality not implemented yet !");
			}
		});
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		initArea();
	}

	private native void initArea() /*-{
		$wnd.wideArea();
	}-*/;

	@Override
	public void setScript(String script) {
		tAreaField.setValue(script);
	}

	@Override
	public String getScript() {
		return $(".widearea-wrapper textarea").val();
	}

	@Override
	public void setResult(String result) {
		resultPanel.setHeight("400px");
		resultPanel.setWidth("800px");
		resultPanel.add(new HTML(result));
		resultPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
	}

	@Override
	public void startLoading() {
		loader.getStyle().clearDisplay();
	}

	@Override
	public void endLoading() {
		loader.getStyle().setDisplay(Display.NONE);
	}

}

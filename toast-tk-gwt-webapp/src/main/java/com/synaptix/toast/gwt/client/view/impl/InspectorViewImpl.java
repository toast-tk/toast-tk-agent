package com.synaptix.toast.gwt.client.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class InspectorViewImpl extends Composite {

	private static InspectorViewImplUiBinder uiBinder = GWT.create(InspectorViewImplUiBinder.class);

	@UiField
	Frame inspectorFrame;

	interface InspectorViewImplUiBinder extends UiBinder<Widget, InspectorViewImpl> {
	}

	public InspectorViewImpl(String url) {
		initWidget(uiBinder.createAndBindUi(this));
		inspectorFrame.setUrl(url);
	}

	public Frame getFrame() {
		return inspectorFrame;
	}

}

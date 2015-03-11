package com.synaptix.toast.gwt.client.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.controller.entry.IDomainController;
import com.synaptix.toast.gwt.client.view.widget.InfoDialogBox;

public class ElementInfoPanelViewImpl extends Composite {

	private static ElementInfoPanelViewImplUiBinder uiBinder = GWT.create(ElementInfoPanelViewImplUiBinder.class);

	interface ElementInfoPanelViewImplUiBinder extends UiBinder<Widget, ElementInfoPanelViewImpl> {
	}

	@UiField
	TextBox nameField;

	@UiField
	TextBox typeField;

	@UiField
	TextBox locationField;

	@UiField
	TextBox methodField;

	@UiField
	TextBox positionField;

	@UiField
	Button saveButton;

	@UiField
	Button fecthLocationButton;

	private ElementInfoDto info;

	public ElementInfoPanelViewImpl(final IDomainController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.onSaveElement(getElementInfo());
			}
		});
		fecthLocationButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String result = Window.prompt("Which url you want to fecth this element location from ?", "http://gelabpscddb02:8180/psc-portal");
				if (result != null) {
					InspectorViewImpl inpector = new InspectorViewImpl(result);
					InfoDialogBox dialog = new InfoDialogBox(inpector, result);
					dialog.setWidth("840px");
					dialog.show();
					dialog.setModal(false);
					dialog.getElement().getStyle().clearProperty("clip");
				}
			}
		});
	}

	public native Document getFrameDocument() /*-{
		console
				.log($doc.getElementsByTagName("iframe")[0].contentWindow.document);
		return $doc.getElementsByTagName("iframe")[0].contentWindow.document;
	}-*/;

	public void setDetails(ElementInfoDto info) {
		this.info = info;
		nameField.setText(info.getName());
		typeField.setText(info.getType());
		methodField.setText(info.getMethod());
		positionField.setText(String.valueOf(info.getPosition()));
		locationField.setText(info.getLocator());
	}

	private ElementInfoDto getElementInfo() {
		info.setMethod(methodField.getText());
		info.setLocator(locationField.getText());
		info.setPosition(Integer.valueOf(positionField.getText()));
		return info;
	}
}

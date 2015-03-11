package com.synaptix.toast.gwt.client.view.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.synaptix.toast.gwt.client.controller.entry.IAdminController;
import com.synaptix.toast.gwt.client.view.entry.IAdminTabView;

public class AdminTabViewImpl extends Composite implements IAdminTabView {

	private static AdminTabViewImplUiBinder uiBinder = GWT.create(AdminTabViewImplUiBinder.class);

	interface AdminTabViewImplUiBinder extends UiBinder<Widget, AdminTabViewImpl> {
	}

	@UiField
	TextBox baseUrlField;

	@UiField
	TextBox packageFolderField;

	@UiField
	ListBox jarField;

	private final IAdminController controller;

	public AdminTabViewImpl(IAdminController controller) {
		initWidget(uiBinder.createAndBindUi(this));
		this.controller = controller;
	}

	@UiHandler("loadButton")
	public void onClick(ClickEvent e) {
		controller.loadAllJars(AdminTabViewImpl.this);
	}

	@Override
	public void setJars(List<String> jars) {
		setItemsListBox(jarField, jars);
	}

	/**
	 * Set items for ListBox
	 * 
	 * @param listBox
	 * @param items
	 */
	public static final void setItemsListBox(ListBox listBox, List<String> items) {
		listBox.clear();
		if (items != null && !items.isEmpty()) {
			for (String item : items) {
				listBox.addItem(item);
			}
		}
	}

	@Override
	public String getSelectedJar() {
		return jarField.getItemText(jarField.getSelectedIndex());
	}
}
